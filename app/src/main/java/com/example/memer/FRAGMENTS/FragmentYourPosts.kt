package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterFragmentYourPosts
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.VIEWMODELS.ViewModelUserPost
import com.example.memer.databinding.FragmentYourPostsBinding


class FragmentYourPosts : Fragment(), AdapterFragmentYourPosts.ItemClickListener {

    private lateinit var binding: FragmentYourPostsBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterFragmentYourPosts
    private val viewModelUserPost:ViewModelUserPost by viewModels()
    private val viewModelUser:ViewModelUserInfo by activityViewModels()

    companion object{
        private const val TAG = "FragmentYourPosts"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYourPostsBinding.inflate(inflater,container,false)

        initRecyclerView()
        viewModelUserPost.getUserPosts(viewModelUser.userLD.value !! . userId)
        return binding.root
    }

    private fun initRecyclerView(){
        gridLayoutManager = GridLayoutManager(context,4)
        mAdapter = AdapterFragmentYourPosts(this, requireActivity())
        val recyclerView =binding.recyclerViewFragmentYourPosts
        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelUserPost.postsLiveData.observe(viewLifecycleOwner, {
            Log.d(TAG, "init block: observe ${it.size}")
            mAdapter.submitList(it)
            mAdapter.notifyDataSetChanged()
        })

    }
    override fun onItemClick(position: Int) {
        Toast.makeText(context,"Clicked Your Posts at $position",Toast.LENGTH_SHORT).show()
//        val postId =position+1
//        viewModel.deletePosts("7",postId.toString())
    }


}