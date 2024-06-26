package com.example.memer.FRAGMENTS

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.ADAPTERS.HomePageAdapter
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.MODELS.PostState
import com.example.memer.NavGraphDirections
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelHomePagePost
import com.example.memer.VIEWMODELS.ViewModelRandomUserProfile
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentHomePageBinding
import com.example.memer.databinding.FragmentPostListRandomBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class FragmentPostListRandom : Fragment() , HomePageAdapter.ItemClickListener,HomePageAdapter.OnMenuClickListener,
    View.OnClickListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: HomePageAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController

    private val viewModel: ViewModelRandomUserProfile by navGraphViewModels(R.id.nav_random_profile)
    private val viewModelUser: ViewModelUserInfo by activityViewModels()

    private lateinit var onCompleteListener: OnCompleteListener<QuerySnapshot>
    private lateinit var onFailureListener: OnFailureListener

    private lateinit var binding : FragmentPostListRandomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostListRandomBinding.inflate(inflater,container,false)

        onCompleteListener = OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Loaded Data", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Could Not Fetch Data", Toast.LENGTH_SHORT).show()
            }
        }
        onFailureListener = OnFailureListener {
            Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT).show()
        }

        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = Firebase.auth
        navController = Navigation.findNavController(view)
        binding.postListRandomToolbar.setupWithNavController(navController)

        initDataAndViewModel()
    }

    private fun initDataAndViewModel() {
        initRecyclerView()
        Log.d(TAG, "initDataAndViewModel: HEre")
        viewModel.stateListPostLD.observe(viewLifecycleOwner, {
            when (it) {
                PostState.Loaded -> {
                    Log.d(TAG, "onViewCreated: Loaded Data")
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
                    showProgressBar()
                }
                PostState.Failed -> {
                    Log.d(TAG, "initDataAndViewModel: Failed")
                }
            }
        })
        viewModel.postCompleteLD.observe(viewLifecycleOwner,{
            mAdapter.submitList(it)
            mAdapter.notifyDataSetChanged()
        })
        if(viewModel.stateListPostLD.value!! == PostState.DataNotLoaded) {
            viewModel.initListPost(viewModelUser.userLD.value !!.userId)
        }
    }

    private fun initRecyclerView() {
        val recyclerView = binding.postListRandomRecyclerView
        linearLayoutManager = LinearLayoutManager(context)
        mAdapter = HomePageAdapter(this, this,requireActivity(),viewModelUser.userLD.value !! .userId)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M ) {
            binding.postListRandomScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val nv = v as NestedScrollView
                if(scrollY == nv.getChildAt(0).measuredHeight - nv.measuredHeight){
                    if (viewModel.moreDataPresent) {
                        viewModel.getMoreListPost(viewModelUser.userLD.value!!.userId)
                    }
                }
            }
        }
        else{
            binding.postListRandomRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (viewModel.moreDataPresent && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.getMoreListPost(viewModelUser.userLD.value!!.userId)
                    }
                }
            })
        }

    }

    private fun showProgressBar(){
        binding.progressBarPostListRandom.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        binding.progressBarPostListRandom.visibility = View.GONE
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

        viewModel.likeClicked(
            position = position,
            postId = mAdapter.getPost(position).postContents.postId,
            userId = viewModelUser.userLD.value!!.userId,
            username = viewModelUser.userLD.value!!.username,
            userAvatarReference = viewModelUser.userLD.value!!.userAvatarReference,
            nameOfUser = viewModelUser.userLD.value!!.nameOfUser,
            postOwnerId = mAdapter.getPost(position).postContents.postOwnerId,
            incrementLike = !mAdapter.getPost(position).isLiked
        )

    }
    override fun onCommentClick(position: Int) {
        val action =
            NavGraphDirections.actionGlobalFragmentComments(mAdapter.getPost(position).postContents)
        navController.navigate(action)
    }
    override fun onBookMarkClick(position: Int) {
        Log.d(TAG, "onBookMarkClick $position")
        viewModel.bookMarkClicked(
            position = position, userId = viewModelUser.userLD.value!!.userId,
            postId = mAdapter.getPost(position).postContents.postId,
            postOwnerId = mAdapter.getPost(position).postContents.postOwnerId,
            postOwnerUsername = mAdapter.getPost(position).postContents.username,
            postOwnerAvatarReference = mAdapter.getPost(position).postContents.userAvatarReference
        )
    }
    override fun onUserClick(position: Int) {
        // Should add to Relation User if POST_USER != USER
        if (mAdapter.getPost(position).postContents.postOwnerId == viewModelUser.userLD.value!!.userId) {
            navController.navigate(R.id.action_global_fragmentProfile)
        } else {
            val action = NavGraphDirections.actionGlobalFragmentRandomUserProfile(
                mAdapter.getPost(position).postContents.postOwnerId
            )
            navController.navigate(action)
        }
    }

    override fun onLikeListClick(position: Int) {
        val action = NavGraphDirections.actionGlobalFragmentLikes(mAdapter.getPost(position).postContents)
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
        val action = NavGraphDirections.actionGlobalFragmentEditPost(mAdapter.getPost(position).postContents)
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