package com.example.memer.FRAGMENTS

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.ADAPTERS.HomePageAdapter
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.MODELS.PostState
import com.example.memer.NavGraphDirections
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelHomeFactory
import com.example.memer.VIEWMODELS.ViewModelHomePagePost
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*



class FragmentHomePage : Fragment(), HomePageAdapter.ItemClickListener,
    HomePageAdapter.OnMenuClickListener,
    View.OnClickListener {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var homePageAdapter: HomePageAdapter
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var savedStateHandle:SavedStateHandle

    private val viewModelUser: ViewModelUserInfo by activityViewModels()
    private lateinit var viewModelHomePage: ViewModelHomePagePost

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ")
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())

        requireActivity().bottomNavigationView.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        mAuth = Firebase.auth
        navController = Navigation.findNavController(view)

        savedStateHandle = navController.currentBackStackEntry!!.savedStateHandle


        binding.homePageToolbar.setupWithNavController(navController)
        binding.homePageToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.fragmentChat -> {
                    navController.navigate(R.id.action_fragmentHomePage_to_fragmentChat)
                    true
                }
                else -> false
            }
        }

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val onBoardingDone = sharedPref.getBoolean(getString(R.string.on_boarding_done), false)

        if (!onBoardingDone) {
            Log.d(TAG, "onViewCreated: starting On Boarding")
            navController.navigate(R.id.action_fragmentHomePage_to_fragmentOnBoarding)
        } else {
            viewModelUser.userLD.observe(viewLifecycleOwner, { user ->
                if (user != null) {
                    Log.d(TAG, "initializeViewModel: Already Present Loading")
                    initDataAndViewModel()
                } else {
                    Log.d(TAG, "onViewCreated: Navigating to log in")
                    navController.navigate(R.id.action_global_fragmentLogIn)
                }
            })
        }

        savedStateHandle.getLiveData<Boolean>(LOGIN_SUCCESSFUL).observe(viewLifecycleOwner,
            { success ->
                if (!success) {
                    Log.d(TAG, "onViewCreated: Finishing Activity")
                    requireActivity().finish()
                }
            })
        
        // TODO(Go to login if not new user directly change the savedStateHandle and do the needful)
        // TODO(Else login success -> go to add profile new user after popping the login fragment
        // TODO(and use savedStateHandle of backStack which will be homeFragment as login popped so no changes there)
    }


    private fun initDataAndViewModel() {
        viewModelHomePage =
            ViewModelProvider(this, ViewModelHomeFactory(viewModelUser.userLD.value!!.userId)).get(
                ViewModelHomePagePost::class.java
            )
        initRecyclerView()
        viewModelHomePage.stateLD.observe(viewLifecycleOwner, {
            when (it) {
                 PostState.Loaded -> {
                    hideProgressBar()
                }
                 PostState.Refreshing -> {
                    Log.d(TAG, "initDataAndViewModel: Refreshing")
                }
                 PostState.Loading -> {
                    Log.d(TAG, "initDataAndViewModel: Loading More Data")
                    showProgressBar()
                }
                 PostState.DataNotLoaded -> {
                    Log.d(TAG, "initDataAndViewModel: Data Not Loaded")
                }
                 PostState.Failed -> {
                    Log.d(TAG, "initDataAndViewModel: Failed")
                }
                else -> {}
            }
        })
        viewModelHomePage.postLD.observe(viewLifecycleOwner,{
            homePageAdapter.submitList(it)
            homePageAdapter.notifyDataSetChanged()
        })
    }

    private fun showProgressBar() {
        binding.progressBarHomePagePost.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBarHomePagePost.visibility = View.GONE
    }

    private fun initRecyclerView() {
        val recyclerView = binding.homePageRecyclerView
        linearLayoutManager = LinearLayoutManager(context)
        homePageAdapter =
            HomePageAdapter(this, this, requireActivity(), viewModelUser.userLD.value!!.userId)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = homePageAdapter
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            binding.homePageScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val nv = v as NestedScrollView
                if (scrollY == nv.getChildAt(0).measuredHeight - nv.measuredHeight) {
                    if (viewModelHomePage.moreDataPresent) {
                        viewModelHomePage.getMoreData(viewModelUser.userLD.value!!.userId)
                    }
                }
            }
        } else {
            binding.homePageRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (viewModelHomePage.moreDataPresent && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModelHomePage.getMoreData(viewModelUser.userLD.value!!.userId)
                    }
                }
            })
        }
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

        viewModelHomePage.likeClicked(
            position = position,
            postId = homePageAdapter.getPost(position).postContents.postId,
            userId = viewModelUser.userLD.value!!.userId,
            username = viewModelUser.userLD.value!!.username,
            userAvatarReference = viewModelUser.userLD.value!!.userAvatarReference,
            nameOfUser = viewModelUser.userLD.value!!.nameOfUser,
            postOwnerId = homePageAdapter.getPost(position).postContents.postOwnerId,
            incrementLike = !homePageAdapter.getPost(position).isLiked
        )
    }

    override fun onCommentClick(position: Int) {
        val action =
            NavGraphDirections.actionGlobalFragmentComments(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)
    }

    override fun onBookMarkClick(position: Int) {
        Log.d(TAG, "onBookMarkClick $position")
        viewModelHomePage.bookMarkClicked(
            position = position, userId = viewModelUser.userLD.value!!.userId,
            postId = homePageAdapter.getPost(position).postContents.postId,
            postOwnerId = homePageAdapter.getPost(position).postContents.postOwnerId,
            postOwnerUsername = homePageAdapter.getPost(position).postContents.username,
            postOwnerAvatarReference = homePageAdapter.getPost(position).postContents.userAvatarReference
        )
    }

    override fun onUserClick(position: Int) {
        // Should add to Relation User if POST_USER != USER
        if (homePageAdapter.getPost(position).postContents.postOwnerId == viewModelUser.userLD.value!!.userId) {
            navController.navigate(R.id.action_global_fragmentProfile)
        } else {
            val action = NavGraphDirections.actionGlobalFragmentRandomUserProfile(
                homePageAdapter.getPost(position).postContents.postOwnerId
            )
            navController.navigate(action)
        }
    }

    override fun onLikeListClick(position: Int) {
        val action =
            NavGraphDirections.actionGlobalFragmentLikes(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)
    }

    override fun onClick(v: View?) {

    }

    companion object {
        private const val TAG = "FragmentHomePage"
        private const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }

    override fun sharePostClick(position: Int) {
        Log.d(TAG, "sharePostClick: Share")
    }

    override fun editPostClick(position: Int) {
        val action =
            NavGraphDirections.actionGlobalFragmentEditPost(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)

    }

    override fun deletePostClick(position: Int) {
        Log.d(TAG, "deletePostClick: Delete")
    }

    override fun copyLinkPostClick(position: Int) {
        Log.d(TAG, "copyLinkPostClick: Copy Link")
        val clipBoardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Post Link",getLink(
            homePageAdapter.getPost(position).postContents.postId,
            viewModelUser.userLD.value!!.userId)
        )
        clipBoardManager.setPrimaryClip(clipData)
        Toast.makeText(context,"Copied Link",Toast.LENGTH_SHORT).show()
    }

    override fun reportPostClick(position: Int) {
        Log.d(TAG, "reportPostClick: Report")
    }

    override fun savePostClick(position: Int) {
        Log.d(TAG, "savePostClick: Save")
    }

    private fun getLink(postId:String,userId:String):String{
        return "https://www.memer.example.com/${postId}/${userId}"
    }
}