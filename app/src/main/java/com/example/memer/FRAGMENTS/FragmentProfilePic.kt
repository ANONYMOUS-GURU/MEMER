package com.example.memer.FRAGMENTS

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterGallery
import com.example.memer.FIRESTORE.ImageUploads
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelGallery
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentProfilePicBinding
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FragmentProfilePic : Fragment(),AdapterGallery.ItemClickListener , CropImageView.OnCropImageCompleteListener{

    private lateinit var binding : FragmentProfilePicBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterGallery
    private lateinit var navController: NavController
    private val viewModelGallery: ViewModelGallery by viewModels()
    private val viewModelUserInfo:ViewModelUserInfo by activityViewModels()
    private lateinit var currentPhotoPath: String
    private  var uri: Uri? = null
    private lateinit var loadingDialog: LoadingDialog


    private val requestPermissionLauncherGallery =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModelGallery.loadImages()
            } else {
                Toast.makeText(context,"Allow Permission For ACCESS", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePicBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.GONE
        loadingDialog = LoadingDialog(requireActivity())

        initRecyclerView()
        viewModelGallery.imagesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            mAdapter.submitList(it)
        })

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED) {
            viewModelGallery.loadImages()

        } else {
            requestPermissionLauncherGallery.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.updateProfilePicToolbar.setupWithNavController(navController)
        binding.updateProfilePicToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addPostCamera -> {
                    launchCamera()
                    true
                }
                R.id.postItem -> {
                    onDoneClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerView() {
        gridLayoutManager = GridLayoutManager(context, 4)
        mAdapter = AdapterGallery(this, requireActivity())
        val recyclerView = binding.recyclerViewProfilePicGallery

        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Clicked Your Posts at $position", Toast.LENGTH_SHORT).show()
        binding.updateProfilePicCropImage.setImageUriAsync(mAdapter.getUri(position))
    }

    private fun launchCamera(){
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
        } else {
            requestPermissionLauncherCamera.launch(
                Manifest.permission.CAMERA
            )
        }
    }
    private fun dispatchTakePictureIntent() {
        uri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.memer.fileProvider",
            createImageFile()
        )
        getCameraImageLauncher.launch(uri)
    }
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat.getDateTimeInstance().format(Date())
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    private val requestPermissionLauncherCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(context, "Allow Permission For ACCESS", Toast.LENGTH_LONG).show()
        }
    }
    private val getCameraImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            Log.i(TAG, "Got image at: $")
            Toast.makeText(context, "Got Image $uri", Toast.LENGTH_SHORT).show()
            binding.updateProfilePicCropImage.setImageUriAsync(uri)
        }
    }

    private fun onDoneClick(){
        binding.updateProfilePicCropImage.setOnCropImageCompleteListener(this)
        binding.updateProfilePicCropImage.getCroppedImageAsync()
        loadingDialog.startLoadingDialog("Cropping Image ... ")
        Log.d(TAG, "onDoneClick: ${System.currentTimeMillis()}")
    }
    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        if (result != null) {
            val uniqueId = (System.currentTimeMillis()/1000).toString()
            loadingDialog.changeText("Uploading Image ... ")
            val task = ImageUploads.uploadBitmapProfile(result.bitmap,viewModelUserInfo.userLD.value !! . userId , uniqueId)
            task.addOnSuccessListener {
                getDownloadUrl(viewModelUserInfo.userLD.value !! . userId, uniqueId)
            }
        }

    }

    private fun getDownloadUrl(userId: String, uniqueId: String) {
        ImageUploads.getDownloadUrlProfile(userId, uniqueId).addOnSuccessListener {
            loadingDialog.changeText("Finalizing ... ")
            viewModelUserInfo.updateUserImageReference(it.toString() to it.toString(),viewModelUserInfo.userLD.value !! . userId)
            loadingDialog.dismissDialog()
            navController.navigateUp()
        }.addOnFailureListener {
            Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
            loadingDialog.dismissDialog()
            // Add in a database collection failed post uploads
        }
    }
    companion object{
        private const val TAG = "FragmentProfilePic"
    }

}