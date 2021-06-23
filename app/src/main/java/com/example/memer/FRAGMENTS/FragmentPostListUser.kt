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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.ADAPTERS.HomePageAdapter
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.NavGraphDirections
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelHomePagePost
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentHomePageBinding
import com.example.memer.databinding.FragmentPostListUserBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class FragmentPostListUser : Fragment(),HomePageAdapter.ItemClickListener,HomePageAdapter.OnMenuClickListener,
    View.OnClickListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var homePageAdapter: HomePageAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentPostListUserBinding

    private val viewModelUser: ViewModelUserInfo by activityViewModels()

    private lateinit var onCompleteListener: OnCompleteListener<QuerySnapshot>
    private lateinit var onFailureListener: OnFailureListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostListUserBinding.inflate(inflater,container,false)

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
        binding.postListUserToolbar.setupWithNavController(navController)

        initDataAndViewModel()
    }

    private fun initDataAndViewModel() {
        initRecyclerView()
        Log.d(TAG, "initDataAndViewModel: HEre")
        viewModelUser.postCompleteLD.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreateView: ${it.size}")
            homePageAdapter.submitList(it)
            homePageAdapter.notifyDataSetChanged()
        })
        viewModelUser.initListPost(viewModelUser.userLD.value !!.userId)
    }

    private fun initRecyclerView() {
        val recyclerView = binding.postListUserRecyclerView
        linearLayoutManager = LinearLayoutManager(context)
        homePageAdapter = HomePageAdapter(this, this,requireActivity(),viewModelUser.userLD.value !! .userId)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = homePageAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (viewModelUser.moreDataPresent && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModelUser.getMoreListPost(viewModelUser.userLD.value!!.userId)
                    }
                }
            })
        }
    }

    override fun onImageItemClick(position: Int) {
        Toast.makeText(activity, "Clicked on image at position $position", Toast.LENGTH_SHORT)
            .show()
        val action = NavGraphDirections.actionGlobalFragmentLikes(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)
    }
    override fun onVideoItemClick(position: Int) {
        Toast.makeText(activity, "Clicked on video at position $position", Toast.LENGTH_SHORT)
            .show()
    }
    override fun onLikeClick(position: Int) {
        Log.d("FragmentHomePage", "onLikeClick $position")

        viewModelUser.likeClicked(
            position,
            homePageAdapter.getPost(position).postContents.postId,
            viewModelUser.userLD.value!!.userId,
            viewModelUser.userLD.value!!.username,
            viewModelUser.userLD.value!!.userAvatarReference,
            viewModelUser.userLD.value!!.nameOfUser,
            homePageAdapter.getPost(position).postContents.postOwnerId,
            homePageAdapter.getPost(position).isLiked == 0L
        )

    }
    override fun onCommentClick(position: Int) {
        val action =
            NavGraphDirections.actionGlobalFragmentComments(homePageAdapter.getPost(position).postContents)
        navController.navigate(action)
    }
    override fun onBookMarkClick(position: Int) {
        Log.d(TAG, "onBookMarkClick $position")
        viewModelUser.bookMarkClicked(
            position, viewModelUser.userLD.value!!.userId,
            homePageAdapter.getPost(position).postContents.postId,
            homePageAdapter.getPost(position).postContents.postOwnerId
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

    override fun onClick(v: View?) {

    }

    companion object {
        private const val TAG = "FragmentHomePage"
    }

    override fun sharePostClick(position: Int) {
        Log.d(TAG, "sharePostClick: Share")
    }

    override fun editPostClick(position: Int) {
        Log.d(TAG, "editPostClick: Edit")
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