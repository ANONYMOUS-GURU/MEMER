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
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterFragmentYourPosts
import com.example.memer.ADAPTERS.AdapterRandomUserPostImage
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelRandomUserProfile
import com.example.memer.VIEWMODELS.ViewModelRandomUserProfileFactory
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.VIEWMODELS.ViewModelUserPost
import com.example.memer.databinding.FragmentProfileContainerBinding
import com.example.memer.databinding.FragmentRandomUserPostBinding
import com.example.memer.databinding.FragmentRandomUserProfileBinding

class FragmentRandomUserPost : Fragment() , AdapterRandomUserPostImage.ItemClickListener{

    private lateinit var binding: FragmentRandomUserPostBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterRandomUserPostImage
    private val viewModelPost: ViewModelRandomUserProfile by navGraphViewModels(R.id.nav_random_profile)
    private val viewModelUser: ViewModelUserInfo by activityViewModels()

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
        mAdapter = AdapterRandomUserPostImage(this, requireActivity())
        val recyclerView =binding.recyclerViewFragmentRandomUserPosts
        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelPost.postLD.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
            mAdapter.notifyDataSetChanged()
        })

    }
    override fun onItemClick(position: Int) {
        Toast.makeText(context,"Clicked Your Posts at $position", Toast.LENGTH_SHORT).show()
//        val postId =position+1
//        viewModel.deletePosts("7",postId.toString())
    }


}