package com.example.memer.FRAGMENTS

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.example.memer.HELPERS.InternalStorage
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.MODELS.PostState
import com.example.memer.NavGraphDirections
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelHomeFactory
import com.example.memer.VIEWMODELS.ViewModelHomePagePost
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentHomePageBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentHomePage : Fragment(), HomePageAdapter.ItemClickListener,HomePageAdapter.OnMenuClickListener,
    View.OnClickListener {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var homePageAdapter: HomePageAdapter
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController

    private val viewModelUser: ViewModelUserInfo by activityViewModels()
    private val viewModelHomePage: ViewModelHomePagePost by activityViewModels()

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

        if(!onBoardingDone){
            Log.d(TAG, "onViewCreated: starting On Boarding")
            navController.navigate(R.id.action_fragmentHomePage_to_fragmentOnBoarding)
        }else{
            initializeUserViewModel()
        }
    }
    private fun initializeUserViewModel() {
        when {
            viewModelUser.userLD.value != null -> {
                Log.d(TAG, "initializeViewModel: Already Present Loading")
                initDataAndViewModel()
            }
            viewModelUser.userExists() -> {
                viewModelUser.initUser()
                Log.d(TAG, "initializeUserViewModel: Loading New User")   //TODO(MOVE THIS TO THE ADD NEW USER PROFILE FRAGMENT AND UPDATE VM USER THERE
                initDataAndViewModel()
            }
            else -> {
                navController.navigate(R.id.action_global_fragmentLogIn)
                Log.d(TAG, "initializeUserViewModel: User Not Found in Internal Going back to Login")
            }
        }
    }

    private fun initDataAndViewModel() {
        initRecyclerView()
        Log.d(TAG, "initDataAndViewModel: Here")
        viewModelHomePage.postLD.observe(viewLifecycleOwner, {
            when (it) {
                is PostState.Loaded -> {
                    Log.d(TAG, "initDataAndViewModel: Loaded ${it.post.size}")
                    homePageAdapter.submitState(it)
                    homePageAdapter.submitList(it.post)
                    homePageAdapter.notifyDataSetChanged()
                }
                is PostState.Refreshing -> {
                    homePageAdapter.submitState(it)
                    Log.d(TAG, "initDataAndViewModel: Refreshing")
                }
                is PostState.LoadingMoreData -> {
                    Log.d(TAG, "initDataAndViewModel: Loading More Data")
                }
                is PostState.InitialLoading -> {

                    Log.d(TAG, "initDataAndViewModel: Initializing")
                }
                is PostState.LoadingFailed -> {
                    Log.d(TAG, "initDataAndViewModel: Failed")
                }
            }

        })
    }

    private fun initialLoadingState(){

    }

    private fun initRecyclerView() {
        val recyclerView = binding.homePageRecyclerView
        linearLayoutManager = LinearLayoutManager(context)
        homePageAdapter = HomePageAdapter(this, this,requireActivity(),viewModelUser.userLD.value !! .userId)

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
        val action = NavGraphDirections.actionGlobalFragmentLikes(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)
    }

    override fun onClick(v: View?) {

    }

    companion object {
        private const val TAG = "FragmentHomePage"
    }

    override fun sharePostClick(position: Int) {
        Log.d(TAG, "sharePostClick: Share")
    }
    override fun editPostClick(position: Int) {
        val action = NavGraphDirections.actionGlobalFragmentEditPost(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)

    }
    override fun deletePostClick(position: Int) {
        Log.d(TAG, "deletePostClick: Delete")
    }
    override fun copyLinkPostClick(position: Int) {
        Log.d(TAG, "copyLinkPostClick: Copy Link")
    }
    override fun reportPostClick(position: Int) {
        Log.d(TAG, "reportPostClick: Report")
    }
    override fun savePostClick(position: Int) {
        Log.d(TAG, "savePostClick: Save")
    }

}