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
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.ADAPTERS.AdapterFragmentYourPosts
import com.example.memer.MODELS.PostThumbnailState
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelRandomUserProfile
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentRandomUserPostBinding

class FragmentRandomUserPost : Fragment() , AdapterFragmentYourPosts.ItemClickListener{

    private lateinit var binding: FragmentRandomUserPostBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterFragmentYourPosts
    private val viewModelPost: ViewModelRandomUserProfile by navGraphViewModels(R.id.nav_random_profile)
    private val viewModelUser: ViewModelUserInfo by activityViewModels()
    private lateinit var navController: NavController


    companion object{
        private const val TAG = "FragmentRandomUserPost"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView:")
        binding = FragmentRandomUserPostBinding.inflate(inflater,container,false)

        initRecyclerView()


        return binding.root
    }

    private fun initRecyclerView(){
        gridLayoutManager = GridLayoutManager(context,4)
        mAdapter = AdapterFragmentYourPosts(this, requireActivity())
        val recyclerView =binding.recyclerViewFragmentRandomUserPosts
        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (viewModelPost.moreDataPresent && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModelPost.getMorePosts()
                    }
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireParentFragment().findNavController()
        viewModelPost.postLD.observe(viewLifecycleOwner, {
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
        navController.navigate(R.id.action_fragmentRandomUserProfile_to_fragmentPostListRandom)
    }


}