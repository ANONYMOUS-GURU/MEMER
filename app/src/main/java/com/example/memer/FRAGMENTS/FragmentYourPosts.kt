package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterFragmentYourPosts
import com.example.memer.HELPERS.DataSource
import com.example.memer.VIEWMODELS.ViewModelUserPagePosts
import com.example.memer.databinding.FragmentYourPostsBinding


class FragmentYourPosts : Fragment(), AdapterFragmentYourPosts.ItemClickListener {

    private lateinit var binding: FragmentYourPostsBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterFragmentYourPosts
    private val viewModel:ViewModelUserPagePosts by viewModels()

    companion object{
        private const val TAG = "FragmentYourPosts"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYourPostsBinding.inflate(inflater,container,false)

        initRecyclerView()

        viewModel.getPosts("7").observe(viewLifecycleOwner, {
            mAdapter.notifyDataSetChanged()
        })


        return binding.root
    }

    private fun initRecyclerView(){
        gridLayoutManager = GridLayoutManager(context,4)
        mAdapter = AdapterFragmentYourPosts(this, requireActivity())
        mAdapter.submitList(viewModel.getPosts("7").value)
        val recyclerView =binding.recyclerViewFragmentYourPosts

        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context,"Clicked Your Posts at $position",Toast.LENGTH_SHORT).show()
//        val postId =position+1
//        viewModel.deletePosts("7",postId.toString())
    }


}