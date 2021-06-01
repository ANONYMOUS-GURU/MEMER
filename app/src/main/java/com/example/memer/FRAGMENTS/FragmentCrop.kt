package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelCreatePost
import com.example.memer.databinding.FragmentCropBinding
import com.theartofdev.edmodo.cropper.CropImageView

class FragmentCrop : Fragment(), CropImageView.OnCropImageCompleteListener {
    private lateinit var binding: FragmentCropBinding
    private lateinit var navController: NavController
    private val viewModel: ViewModelCreatePost by navGraphViewModels(R.id.navigationAddPost)
    private lateinit var loadingDialog:LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentCropBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.cropImageView.setImageUriAsync(viewModel.uriData);

        binding.cropPageToolbar.setupWithNavController(navController)
        binding.cropPageToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.doneCrop -> {
                    onDoneClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun onDoneClick(){
        binding.cropImageView.setOnCropImageCompleteListener(this)
        binding.cropImageView.getCroppedImageAsync()
        loadingDialog.startLoadingDialog("Cropping Image ... ")
        Log.d(TAG, "onDoneClick: ${System.currentTimeMillis()}")
    }
    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        if (result != null) {
            viewModel.update(result.bitmap)
        }
        Log.d(TAG, "onDoneClick: ${System.currentTimeMillis()}")
        loadingDialog.dismissDialog()
        navController.navigateUp()
    }
    companion object{
        private const val TAG = "FragmentCrop"
    }
}