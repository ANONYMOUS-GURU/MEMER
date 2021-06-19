package com.example.memer.FRAGMENTS

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import com.example.memer.FIRESTORE.ImageUploads
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelCreatePost
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentPostPreviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentPostPreview : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentPostPreviewBinding
    private lateinit var loadingDialog: LoadingDialog
    private val viewModelPost: ViewModelCreatePost by navGraphViewModels(R.id.navigationAddPost)
    private lateinit var navController: NavController
    private var size = ""
    private val viewModelUser:ViewModelUserInfo by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostPreviewBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.imagePostPostPreview.setImageBitmap(viewModelPost.finalBitmap)
        size = ((viewModelPost.finalBitmap?.byteCount!! * 1.0) / 1_000_000).toString()
        binding.cancelButtonPostPreview.setOnClickListener(this)
        binding.submitButtonPostPreview.setOnClickListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        loadingDialog = LoadingDialog(requireActivity())
        binding.previewPostToolbar.setupWithNavController(navController)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.submitButtonPostPreview.id -> {
                loadingDialog.startLoadingDialog("Uploading Image ... ")
                postImage()
            }
            binding.cancelButtonPostPreview.id -> {
                navController.navigateUp()
            }
        }
    }

    private fun postImage() {
        val postDesc = binding.descriptionPostPreview.text.toString()
//        Toast.makeText(context, "Clicked Image Size = $size Mb", Toast.LENGTH_SHORT).show()

        val uniqueId = (System.currentTimeMillis() / 1000).toString()
        val uploadTask = ImageUploads.uploadBitmapPost(viewModelPost.finalBitmap!!, viewModelUser.userLD.value !! . userId, uniqueId)

        uploadTask
            .addOnSuccessListener {
//                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show()
                getDownloadUrl(viewModelUser.userLD.value !! . userId, uniqueId, postDesc)
            }
            .addOnPausedListener {
                Toast.makeText(context, "Upload Paused", Toast.LENGTH_SHORT).show()
                loadingDialog.dismissDialog()
            }
            .addOnCanceledListener {
                loadingDialog.dismissDialog()
                Toast.makeText(context, "Upload Cancelled", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
//                Toast.makeText(context, "Upload Completed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }
            .addOnProgressListener {
                val progress =
                    "${(100 * (it.bytesTransferred * 1.0) / it.totalByteCount).toInt()} % Uploaded"
                loadingDialog.changeText(progress)
            }


    }

    private fun getDownloadUrl(userId: String, uniqueId: String, postDesc: String) {
        ImageUploads.getDownloadUrlPost(userId, uniqueId).addOnSuccessListener {
            loadingDialog.changeText("Finalizing ... ")
            writePostToDb(it, postDesc)
        }.addOnFailureListener {
            Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
            loadingDialog.dismissDialog()
            // Add in a database collection failed post uploads
        }
    }

    // Implement the entire upload process using cloud functions for reliability
    private fun writePostToDb(uri: Uri, postDesc: String) {

        val postId = viewModelUser.userLD.value !! . userId + (System.currentTimeMillis() / 1000).toString()
        val task = PostDb.addPost(uri.toString(), viewModelUser.userLD.value !! . userId, viewModelUser.userLD.value !! . username, viewModelUser.userLD.value !! . userAvatarReference, postDesc, postId)
        task
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModelUser.reinitializeUser()
                    withContext(Dispatchers.Main) {
                        loadingDialog.dismissDialog()
                        navController.navigate(R.id.action_fragmentPostPreview_to_fragmentHomePage)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Upload of Post Failed", Toast.LENGTH_SHORT).show()
                loadingDialog.dismissDialog()
            }.addOnCanceledListener {
                Toast.makeText(context, "Upload of Post Cancelled", Toast.LENGTH_SHORT).show()
                loadingDialog.dismissDialog()
            }
    }
}