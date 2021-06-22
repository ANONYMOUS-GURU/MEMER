package com.example.memer.FRAGMENTS

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.ADAPTERS.AdapterComments
import com.example.memer.MODELS.CommentState
import com.example.memer.MODELS.PostContents2
import com.example.memer.NavGraphDirections
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelComments
import com.example.memer.VIEWMODELS.ViewModelCommentsFactory
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentCommentsBinding
import kotlinx.android.synthetic.main.activity_main.*

class FragmentComments : Fragment() , AdapterComments.ItemClickListener , View.OnClickListener {

    private lateinit var binding: FragmentCommentsBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: AdapterComments
    private lateinit var navController: NavController
    private val viewModelUser:ViewModelUserInfo by activityViewModels()
//    private var parentPosition : Int = -1
//    private var commentParentId :String = ""

    private val args:FragmentCommentsArgs by navArgs()
    private lateinit var post:PostContents2

    private lateinit var viewModel:ViewModelComments




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.GONE


        initView()

        return binding.root
    }

    private fun initView(){
        val requestOptionsAvatar = RequestOptions()
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)
        Glide.with(requireContext())
            .applyDefaultRequestOptions(requestOptionsAvatar)
            .load(viewModelUser.userLD.value !! . userAvatarReference) // TODO(CHANGE AVATAR)
            .circleCrop()
            .into(binding.currentUserAvatar)
        binding.submitComment.setOnClickListener(this)
        binding.closeReplyIcon.setOnClickListener(this)
        initRecyclerView()
    }
    private fun initRecyclerView(){
        val recyclerView = binding.recyclerViewCommentsPage
        linearLayoutManager = LinearLayoutManager(context)
        mAdapter = AdapterComments(this, requireActivity(),viewModelUser.userLD.value !! .userId)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        initViewModel()
        binding.commentPageToolbar.setupWithNavController(navController)
    }
    private fun initViewModel(){
        post = args.post
        viewModel = ViewModelProvider(this, ViewModelCommentsFactory(post)).get(ViewModelComments::class.java)

        viewModel.dataLD.observe(viewLifecycleOwner, {
            Log.d(TAG, "onViewCreated: Fired Comment ViewHolder")
            mAdapter.submitList(it)
            mAdapter.notifyDataSetChanged()
        })
        viewModel.commentStateLD.observe(viewLifecycleOwner,{
            when(it){
                is CommentState.ReplyTo -> {onReply(it.userId,it.username,it.commentParentId,it.parentIndex)}
                is CommentState.Default -> {reset()}
                is CommentState.Edit -> {onEdit(it.position,it.parentIndex)}
            }
        })
    }

    private fun reset(){
        binding.userCommentEditText.setText("")
        binding.replyingToTextView.text = ""
        binding.replyingToTextViewLinearLayout.visibility = View.GONE
    }
    override fun onClick(v: View?) {
        when (v?.id){
            binding.submitComment.id -> {
                val str = binding.userCommentEditText.text.toString()
                val commentState = viewModel.commentStateLD.value !!
                if (str.isNotEmpty()) {
                    when(commentState){
                        is CommentState.Default -> viewModel.addCommentPost(post, viewModelUser.userLD.value !!, str)
                        is CommentState.ReplyTo -> viewModel.addCommentPost(post, viewModelUser.userLD.value !!, str, commentState.commentParentId,commentState.parentIndex)
                        is CommentState.Edit -> {
                            val comment = mAdapter.getComment(commentState.position,commentState.parentIndex)
                            viewModel.editComment(post.postId,viewModelUser.userLD.value!!.userId,post.postOwnerId,
                                str,comment.commentParentId,comment.commentId,commentState.parentIndex,commentState.position)
                        }
                    }
                    viewModel.makeCommentDefault()
                } else {
                    Toast.makeText(context, "Add Comment Body", Toast.LENGTH_SHORT).show()
                }
            }
            binding.closeReplyIcon.id -> {
                viewModel.makeCommentDefault()
            }
        }
    }

    override fun onLikeClick(position: Int, parentIndex: Int) {
        TODO("Not yet implemented")
    }
    override fun onUserClick(position: Int, parentIndex: Int) {
        val comment =  mAdapter.getComment(position, parentIndex)
        if(comment.commentOwnerId == viewModelUser.userLD.value !!. userId)
            navController.navigate(R.id.action_global_fragmentProfile)
        else{
            val action  = NavGraphDirections.actionGlobalFragmentRandomUserProfile(comment.commentOwnerId)
            navController.navigate(action)
        }
    }
    override fun onReplyClick(position: Int, parentIndex: Int) {
        val comment = mAdapter.getParentComment(position,parentIndex)
        viewModel.onReplyClick(comment.commentOwnerId,comment.commentOwnerUsername,comment.commentId,if(parentIndex == -1) position else parentIndex)
    }
    @SuppressLint("SetTextI18n")
    private fun onReply(userId:String,username:String,commentParentId: String,parentIndex: Int){
        binding.replyingToTextViewLinearLayout.visibility = View.VISIBLE
        binding.replyingToTextView.text = "Replying To $username"
        binding.userCommentEditText.requestFocus()
    }

    override fun onShowReplies(position: Int, parentIndex: Int) {
        viewModel.getCommentsReplies(post.postId , mAdapter.getCommentParentId(position),position)
    }
    override fun onEditComment(position: Int, parentIndex: Int) {
        viewModel.onEditClick(position,parentIndex)
    }
    private fun onEdit(position: Int,parentIndex: Int){
        val comment = mAdapter.getComment(position,parentIndex)
        binding.userCommentEditText.setText(comment.commentContent)
        binding.userCommentEditText.requestFocus()
        binding.replyingToTextViewLinearLayout.visibility = View.VISIBLE
        binding.replyingToTextView.text = getString(R.string.editing_comment)
    }


    companion object{
        private const val TAG = "FragmentComments"
    }

}