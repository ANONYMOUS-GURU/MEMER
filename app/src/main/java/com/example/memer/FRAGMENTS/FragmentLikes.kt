package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.ADAPTERS.AdapterLikes
import com.example.memer.ADAPTERS.HomePageAdapter
import com.example.memer.MODELS.PostContents2
import com.example.memer.NavGraphDirections
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelHomePagePost
import com.example.memer.VIEWMODELS.ViewModelLikes
import com.example.memer.VIEWMODELS.ViewModelLikesFactory
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentLikesBinding
import com.example.memer.databinding.FragmentRandomUserPostBinding
import kotlinx.android.synthetic.main.activity_main.*

class FragmentLikes : Fragment() ,  AdapterLikes.ItemClickListener{

    private lateinit var binding: FragmentLikesBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: AdapterLikes
    private lateinit var navController: NavController
    private lateinit var viewModel: ViewModelLikes
    private val viewModelUser: ViewModelUserInfo by activityViewModels()
    private lateinit var post:PostContents2
    private val args:FragmentLikesArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikesBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        post = args.post
        initViews()
        viewModel = ViewModelProvider(this,ViewModelLikesFactory(post,viewModelUser.userLD.value!!.userId)).get(ViewModelLikes::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)

        viewModel.likeListLD.observe(viewLifecycleOwner, {
            Log.d(TAG, "onViewCreated: Fired Comment ViewHolder")
            mAdapter.submitList(it)
            mAdapter.notifyDataSetChanged()
        })

        binding.likePageToolbar.setupWithNavController(navController)
    }

    private fun initViews(){
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val recyclerView = binding.likePageRecyclerView
        linearLayoutManager = LinearLayoutManager(context)
        mAdapter = AdapterLikes(this, requireActivity())

        recyclerView.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (viewModel.isMoreDataPresent && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.getLikes()
                    }
                }
            })
        }
    }

    override fun onUserClick(position: Int) {
        if(mAdapter.getUserId(position) == viewModelUser.userLD.value!!.userId){
            navController.navigate(R.id.action_global_fragmentProfile)
        }
        else{
            val action = NavGraphDirections.actionGlobalFragmentRandomUserProfile(mAdapter.getUserId(position))
            navController.navigate(action)
        }

    }

    override fun onFollowButtonClick(position: Int) {
        TODO("Not yet implemented")
    }


    companion object{
        private const val TAG = "FragmentLikes"
    }

}