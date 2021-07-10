package com.example.memer.FRAGMENTS

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostHomePage
import com.example.memer.R
import com.example.memer.VIEWMODELS.*
import com.example.memer.databinding.FragmentSinglePostBinding
import com.example.memer.databinding.SingleMemeViewBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class FragmentSinglePost : Fragment() , View.OnClickListener , PopupMenu.OnMenuItemClickListener{

    private lateinit var binding: FragmentSinglePostBinding
    private val viewModelUser: ViewModelUserInfo by activityViewModels()
    private lateinit var viewModel: VMSinglePost
    private lateinit var postId: String
    private var sharedBy: String? = null
    private lateinit var navController: NavController
    private val args: FragmentSinglePostArgs by navArgs()
    private lateinit var savedStateHandle: SavedStateHandle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSinglePostBinding.inflate(inflater, container, false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        postId = args.postId
        sharedBy = args.sharedBy

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        savedStateHandle = navController.currentBackStackEntry!!.savedStateHandle

        savedStateHandle.getLiveData<Boolean>(LOGIN_SUCCESSFUL).observe(viewLifecycleOwner,
            { success ->
                if (!success) {
                    Log.d(TAG, "onViewCreated: Finishing Activity")
                    requireActivity().finish()
                }
            }
        )

        if (viewModelUser.userLD.value == null) {
            Log.d(TAG, "onViewCreated: user not present in memory")
            if(viewModelUser.userExists()){
                Log.d(TAG, "onViewCreated: user present in internal")
                CoroutineScope(Dispatchers.Main).launch {
                    viewModelUser.initUser()
                    withContext(Dispatchers.Main){
                        initializeViewModel()
                    }
                }
            }
            else{
                Log.d(TAG, "onViewCreated: user not present in internal")
                navController.navigate(R.id.action_global_fragmentLogIn)
            }
        }else{
            Log.d(TAG, "onViewCreated: User Present In memory")
            initializeViewModel()
        }
    }

    private fun initializeViewModel(){
        showProgressBar()
        if(sharedBy == null){
            binding.textSharedByUsername.visibility = View.GONE
        }
        else{
            binding.textSharedByUsername.visibility = View.VISIBLE
//            binding.textSharedByUsername.text = "Shared By "
        }
        viewModel = ViewModelProvider(this, VMSinglePostFactory(postId = postId,
            sharedBy = sharedBy, userId = viewModelUser.userLD.value!!.userId))
            .get(VMSinglePost::class.java)

        viewModel.postLD.observe(viewLifecycleOwner, {
            if(it!=null){
                hideProgressBar()
                initData(it)
            }
        })
    }

    private fun showProgressBar(){
        binding.singlePostProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        binding.singlePostProgressBar.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun initData(post: PostHomePage) {
        val requestOptionsAvatar = RequestOptions()
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)

        Glide.with(requireContext())
            .applyDefaultRequestOptions(requestOptionsAvatar)
            .load(post.postContents.userAvatarReference)
            .circleCrop()
            .into(binding.SinglePostView.userAvatarHome)


        val requestOptionsPost = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(requireContext())
            .applyDefaultRequestOptions(requestOptionsPost)
            .load(post.postContents.postResource)
            .into(binding.SinglePostView.imagePostHomePage)

        binding.SinglePostView.usernameHome.text = post.postContents.username

        if (post.isBookmarked)
            binding.SinglePostView.bookmarkHome.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.bookmark_filled_black
                )
            )
        else
            binding.SinglePostView.bookmarkHome.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.bookmark_border_black
                )
            )


        if (post.isLiked)
            binding.SinglePostView.likeOptionHome.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.like_icon_filled
                )
            )
        else
            binding.SinglePostView.likeOptionHome.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.like_icon_border
                )
            )


        if (post.postContents.postDescription.isEmpty())
            binding.SinglePostView.postCationTextView.visibility = View.GONE
        else {
            binding.SinglePostView.postCationTextView.visibility = View.VISIBLE
            binding.SinglePostView.postCationTextView.text = post.postContents.postDescription
        }

        val popupMenu: PopupMenu = addMenuItem(binding.SinglePostView.menuOnItemHome,
            post.postContents.postOwnerId == viewModelUser.userLD.value!!.userId)

        binding.SinglePostView.likeCountHomePage.text = post.postContents.likeCount.toString()
        /*
        * TODO(PostHomePage add first comment of that post as postContents.exampleComment as a
        *  nullable array if postHomePage.exampleComment == null addComment.visibility = View.GONE else commentsCount.text = "See ..." and visible)
        * */
        if (post.postContents.commentCount > 0) {
            binding.SinglePostView.commentHolderHomePagePost.commentPostRootRelativeLayout.visibility = View.VISIBLE
            binding.SinglePostView.commentCountHomePage.text =
                " See All ${post.postContents.commentCount.toString()} Comments"
            binding.SinglePostView.commentHolderHomePagePost.commentPostRootRelativeLayout.setOnClickListener(this)
        } else {
            binding.SinglePostView.commentHolderHomePagePost.commentPostRootRelativeLayout.visibility = View.GONE
        }

        binding.SinglePostView.userAvatarHome.setOnClickListener(this)
        binding.SinglePostView.usernameHome.setOnClickListener(this)
        binding.SinglePostView.imagePostHomePage.setOnClickListener(this)
        binding.SinglePostView.likeOptionHome.setOnClickListener(this)
        binding.SinglePostView.bookmarkHome.setOnClickListener(this)
        binding.SinglePostView.menuOnItemHome.setOnClickListener(this)
        binding.SinglePostView.likeCountHomePage.setOnClickListener(this)
        binding.SinglePostView.commentOptionHome.setOnClickListener(this)
    }

    private fun addMenuItem(itemView: View, postOwnerIsUser: Boolean = false):PopupMenu{
        val popup = PopupMenu(requireContext(), itemView)
        if(postOwnerIsUser)
            popup.menuInflater.inflate(R.menu.homepage_post_menu_is_user, popup.menu)
        else
            popup.menuInflater.inflate(R.menu.homepage_post_menu, popup.menu)

        popup.setOnMenuItemClickListener(this)
        return popup
    }

    companion object {
        private const val TAG = "FragmentSinglePost"
        private const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }

    override fun onClick(v: View?) {

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return false
    }

}