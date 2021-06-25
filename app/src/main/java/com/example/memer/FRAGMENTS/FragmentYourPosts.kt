package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.ADAPTERS.AdapterFragmentYourPosts
import com.example.memer.MODELS.PostState
import com.example.memer.MODELS.PostThumbnailState
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentYourPostsBinding


class FragmentYourPosts : Fragment(), AdapterFragmentYourPosts.ItemClickListener {

    private lateinit var binding: FragmentYourPostsBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterFragmentYourPosts
    private val viewModelUser:ViewModelUserInfo by activityViewModels()
    private lateinit var navController: NavController


    companion object{
        private const val TAG = "FragmentYourPosts"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYourPostsBinding.inflate(inflater,container,false)

        initRecyclerView()
        if(viewModelUser.postLD.value!! is PostThumbnailState.InitialLoading){
            viewModelUser.getUserPosts(viewModelUser.userLD.value !! . userId)
        }

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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (viewModelUser.moreDataPresent && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModelUser.getMoreUserPosts(viewModelUser.userLD.value!!.userId)
                    }
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireParentFragment().findNavController()
        viewModelUser.postLD.observe(viewLifecycleOwner, {
            when (it) {
                is PostThumbnailState.Loaded -> {
                    Log.d(TAG, "initDataAndViewModel: Loaded ${it.post.size}")
                    mAdapter.submitState(it)
                    mAdapter.submitList(it.post)
                    mAdapter.notifyDataSetChanged()
                }
                is PostThumbnailState.Refreshing -> {
                    mAdapter.submitState(it)
                    Log.d(TAG, "initDataAndViewModel: Refreshing")
                }
                is PostThumbnailState.LoadingMoreData -> {
                    Log.d(TAG, "initDataAndViewModel: Loading More Data")
                }
                is PostThumbnailState.InitialLoading -> {
                    Log.d(TAG, "initDataAndViewModel: Initializing")
                }
                is PostThumbnailState.LoadingFailed -> {
                    Log.d(TAG, "initDataAndViewModel: Failed")
                }
            }
        })

    }

    override fun onItemClick(position: Int) {
        navController.navigate(R.id.action_fragmentProfile_to_fragmentPostListUser)
    }


}