package com.example.memer.FRAGMENTS

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterGallery
import com.example.memer.MODELS.GalleryItem
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelCreatePost
import com.example.memer.VIEWMODELS.ViewModelGallery
import com.example.memer.databinding.FragmentGalleryBinding


class FragmentGallery : Fragment(), AdapterGallery.ItemClickListener {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterGallery
    private val viewModelCreatePost: ViewModelCreatePost by navGraphViewModels(R.id.navigationAddPost)
    private val viewModelGallery:ViewModelGallery by viewModels()
    private lateinit var navController: NavController

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModelGallery.loadImages()
            } else {
                   Toast.makeText(context,"Allow Permission For ACCESS",Toast.LENGTH_LONG).show()
            }
        }

    companion object{
        private const val TAG = "FragmentGallery"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        initRecyclerView()
        viewModelGallery.imagesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            mAdapter.submitList(it)
        })

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED) {
                viewModelGallery.loadImages()

        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController =requireParentFragment().findNavController()
    }
    private fun initRecyclerView() {
        gridLayoutManager = GridLayoutManager(context, 4)
        mAdapter = AdapterGallery(this, requireActivity())
        val recyclerView = binding.recyclerViewGallery

        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }
    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Clicked Your Posts at $position", Toast.LENGTH_SHORT).show()
        viewModelCreatePost.uriData = mAdapter.getUri(position)
        navController.navigate(R.id.action_fragmentAddPost_to_fragmentCrop)
    }

}