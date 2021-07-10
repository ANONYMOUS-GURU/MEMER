package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostHomePage
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelEditPost
import com.example.memer.VIEWMODELS.ViewModelEditPostFactory
import com.example.memer.databinding.FragmentEditPostBinding


class FragmentEditPost : Fragment() , View.OnClickListener {


    private val args:FragmentEditPostArgs by navArgs()
    private lateinit var posts:PostContents2
    private lateinit var navController:NavController
    private lateinit var binding:FragmentEditPostBinding
    private lateinit var viewModel:ViewModelEditPost
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater,container,false)
        posts = args.post
        viewModel = ViewModelProvider(this,ViewModelEditPostFactory(posts)).get(ViewModelEditPost::class.java)
        initView()
        return binding.root
    }

    private fun initView(){

        binding.cancelEditPost.setOnClickListener(this)
        binding.submitEditText.setOnClickListener(this)

        binding.postCaptionEditTextView.setText(viewModel.postLD.value!!.postDescription)
        val requestOptionsPost = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(binding.imagePostEdit.context)
            .applyDefaultRequestOptions(requestOptionsPost)
            .load(viewModel.postLD.value!!.postResource)
            .into(binding.imagePostEdit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.editPostPageToolbar.setupWithNavController(navController)
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                binding.submitEditText.id -> {
                    viewModel.editPost(viewModel.postLD.value!!.postId,
                        binding.postCaptionEditTextView.text.toString()
                    )
                    navController.navigateUp()
                }
                binding.cancelEditPost.id -> {
                    navController.navigateUp()
                }
            }
        }
    }


}