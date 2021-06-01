package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.ADAPTERS.HomePageAdapter
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelHomePagePost
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentHomePage : Fragment(), HomePageAdapter.ItemClickListener, View.OnClickListener {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var homePageAdapter: HomePageAdapter
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private val viewModelHomePage: ViewModelHomePagePost by viewModels()
    private val viewModelUser: ViewModelUserInfo by activityViewModels()

    private val maxLikeLimit = 1
    private val minLikeLimit = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        mAuth = Firebase.auth
        initRecyclerView()
        requireActivity().bottomNavigationView.visibility = View.VISIBLE
        return binding.root
    }

    private fun initRecyclerView() {
        val recyclerView = binding.homePageRecyclerView
        linearLayoutManager = LinearLayoutManager(context)
        homePageAdapter = HomePageAdapter(this, requireActivity())

        recyclerView.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = homePageAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (viewModelHomePage.moreDataPresent && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModelHomePage.getMoreData(viewModelUser.userLD.value!!.userId)
                    }
                }
            })
        }
    }

    private fun initializeViewModel(): Boolean {

        if (mAuth.currentUser == null) {
            navController.navigate(R.id.action_global_fragmentLogIn)
            return false
        }

        if (viewModelUser.userLD.value == null) {
            loadingDialog.startLoadingDialog("Loading User Data ... ")
            CoroutineScope(IO).launch {
                viewModelUser.initializeUser()
                Log.d(TAG, "initializeViewModel: Got User")
                withContext(Dispatchers.Main) {
                    initData()
                    loadingDialog.dismissDialog()
                }
            }
        } else {
            initData()
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.fragmentHomePage))
        binding.homePageToolbar.setupWithNavController(navController, appBarConfiguration)
        binding.homePageToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.fragmentChat -> {
                    navController.navigate(R.id.action_fragmentHomePage_to_fragmentChat)
                    true
                }
                else -> false
            }
        }

        if (!initializeViewModel())
            return


    }

    private fun initData() {
        viewModelHomePage.postLD.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreateView: ${it.size}")
            homePageAdapter.submitList(it)
            homePageAdapter.notifyDataSetChanged()
        })
        viewModelHomePage.getData(viewModelUser.userLD.value!!.userId)
    }

    override fun onImageItemClick(position: Int) {
        Toast.makeText(activity, "Clicked on image at position $position", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onVideoItemClick(position: Int) {
        Toast.makeText(activity, "Clicked on video at position $position", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onLikeClick(position: Int) {
        Log.d("FragmentHomePage", "onLikeClick $position")
        if (homePageAdapter.getPost(position).likeCount == 0L)
            viewModelHomePage.likeClicked(
                position,
                homePageAdapter.getPost(position).postContents.postId,
                viewModelUser.userLD.value!!.userId,
                homePageAdapter.getPost(position).postContents.postOwnerId,
                true
            )
        else{
            viewModelHomePage.likeClicked(
                position,
                homePageAdapter.getPost(position).postContents.postId,
                viewModelUser.userLD.value!!.userId,
                homePageAdapter.getPost(position).postContents.postOwnerId,
                false
            )
        }
    }


    override fun onCommentClick(position: Int) {
        val action  = FragmentHomePageDirections.actionFragmentHomePageToFragmentComments(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)
    }

    override fun onBookMarkClick(position: Int) {
        Log.d("FragmentHomePage", "onBookMarkClick $position")
//        viewModelHomePage.bookMarkClicked(position)
    }

    override fun onUserClick(position: Int) {
        // Should add to Relation User if POST_USER != USER
        navController.navigate(R.id.action_fragmentHomePage_to_fragmentRandomUserProfile)
    }

    override fun onMenuClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {

    }


    companion object {
        private const val TAG = "FragmentHomePage"
    }

}