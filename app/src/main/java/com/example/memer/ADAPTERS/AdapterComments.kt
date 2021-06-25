package com.example.memer.ADAPTERS

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.Comment
import com.example.memer.R
import com.example.memer.databinding.FragmentCommentsBinding
import kotlinx.android.synthetic.main.comment_single_view.view.*
import kotlinx.android.synthetic.main.single_comment_only_view.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class AdapterComments(
    private val itemClickListener: ItemClickListener,
    private val mContext: Context,
    private val userId:String,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<Pair<Comment,ArrayList<Comment>>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_single_view, parent, false),
            itemClickListener,
            mContext,
            userId
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentsViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(commentList: ArrayList<Pair<Comment,ArrayList<Comment>>>) {
        items = commentList
    }
    fun getUsername(position: Int):String{
        return items[position].first.commentOwnerUsername
    }
    fun getComment(position: Int,parentPosition: Int = -1):Comment{
        return if(parentPosition == -1) items[position].first else items[parentPosition].second[position]
    }
    fun getParentComment(position: Int,parentIndex: Int = -1):Comment{
        return if(parentIndex == -1) items[position].first else items[parentIndex].first
    }
    fun getCommentParentId(position: Int):String{
        return items[position].first.commentId
    }

    class CommentsViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context,
        private val userId: String
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener , View.OnLongClickListener {

        private val username: TextView = itemView.commentRootView.usernameComment
        private val userAvatar: ImageView = itemView.commentRootView.userAvatarComment
        private val commentContent: TextView = itemView.commentRootView.userCommentContent
        private val reply: TextView = itemView.commentRootView.replyComment
        private val timeComment: TextView = itemView.commentRootView.timeComment
        private val likeComment: ImageView = itemView.commentRootView.likeComment
        private val showReplies: TextView = itemView.showComments
        private val repliesRecyclerView: RecyclerView = itemView.replyRecyclerView
        private val commentRootView:View = itemView.commentRootView

        private lateinit var mAdapter: AdapterReplies


        fun bind(commentReplyPair: Pair<Comment,ArrayList<Comment>>) {

            val comment = commentReplyPair.first
            val replyList = commentReplyPair.second

            if (comment.commentReplyCount > 0L) {
                showReplies.visibility = View.VISIBLE
                showReplies.text = "${comment.commentReplyCount}"
                showReplies.setOnClickListener(this)

                repliesRecyclerView.visibility = View.VISIBLE

                mAdapter = AdapterReplies(itemClickListener, mContext,userId)
                mAdapter.submitList(replyList)
                mAdapter.submitIndex(absoluteAdapterPosition)

                repliesRecyclerView.apply {
                    layoutManager = LinearLayoutManager(mContext)
                    itemAnimator = DefaultItemAnimator()
                    adapter = mAdapter
                }
            }
            else{
                showReplies.visibility =View.GONE
                repliesRecyclerView.visibility = View.GONE
            }

            username.text = comment.commentOwnerUsername
            commentContent.text = comment.commentContent

            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(comment.commentOwnerUserAvatar)
                .circleCrop()
                .into(userAvatar)

            timeComment.text = getTime(comment.createdAt)

            likeComment.setOnClickListener(this)
            userAvatar.setOnClickListener(this)
            username.setOnClickListener(this)
            reply.setOnClickListener(this)

            if(comment.commentOwnerId == userId){
                commentRootView.setOnLongClickListener(this)
            }
        }

        private fun getTime(date: Date?): String {
            return "10h"
        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    likeComment.id -> itemClickListener.onLikeClick(absoluteAdapterPosition)
                    userAvatar.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    userAvatar.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    reply.id -> itemClickListener.onReplyClick(absoluteAdapterPosition)
                    showReplies.id -> itemClickListener.onShowReplies(absoluteAdapterPosition)
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (v != null) {
                return when (v.id) {
                    commentRootView.id -> {
                        itemClickListener.onEditComment(absoluteAdapterPosition)
                        true
                    }
                    else -> false
                }
            }
            return false
        }
    }

    interface ItemClickListener {
        fun onLikeClick(position: Int,parentIndex:Int = -1)
        fun onUserClick(position: Int,parentIndex:Int = -1)
        fun onReplyClick(position: Int,parentIndex:Int = -1)
        fun onShowReplies(position: Int,parentIndex:Int = -1)
        fun onEditComment(position: Int,parentIndex: Int = -1)
    }
}


class AdapterReplies(
    private val itemClickListener: AdapterComments.ItemClickListener,
    private val mContext: Context,
    private val userId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<Comment> = ArrayList()
    private var indexOriginalList = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReplyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.single_comment_only_view, parent, false),
            itemClickListener,
            mContext,indexOriginalList,
            userId
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReplyViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(replyList:ArrayList<Comment>){
        items = replyList
        notifyDataSetChanged()
    }
    fun submitIndex(index:Int){
        indexOriginalList = index
    }

    class ReplyViewHolder(
        itemView: View,
        private val itemClickListener: AdapterComments.ItemClickListener,
        private val mContext: Context,
        private val commentParentIndex:Int,
        private val userId: String
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener , View.OnLongClickListener {

        private val username: TextView = itemView.usernameComment
        private val userAvatar: ImageView = itemView.userAvatarComment
        private val commentContent: TextView = itemView.userCommentContent
        private val reply: TextView = itemView.replyComment
        private val timeComment: TextView = itemView.timeComment
        private val likeComment: ImageView = itemView.likeComment


        fun bind(comment: Comment) {
            username.text = comment.commentOwnerUsername  // TODO(CHANGE NAME)
            commentContent.text = comment.commentContent

            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(comment.commentOwnerUserAvatar) // TODO(CHANGE AVATAR)
                .circleCrop()
                .into(userAvatar)

            timeComment.text = getTime(comment.createdAt)

            likeComment.setOnClickListener(this)
            userAvatar.setOnClickListener(this)
            username.setOnClickListener(this)
            reply.setOnClickListener(this)
            if(comment.commentOwnerId == userId){
                itemView.setOnLongClickListener(this)
            }

        }

        private fun getTime(date: Date?): String {
            return "10h"
        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    likeComment.id -> itemClickListener.onLikeClick(absoluteAdapterPosition,commentParentIndex)
                    userAvatar.id -> itemClickListener.onUserClick(absoluteAdapterPosition,commentParentIndex)
                    userAvatar.id -> itemClickListener.onUserClick(absoluteAdapterPosition,commentParentIndex)
                    reply.id -> itemClickListener.onReplyClick(absoluteAdapterPosition,commentParentIndex)
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (v != null) {
                return when (v.id) {
                    itemView.id -> {
                        itemClickListener.onEditComment(absoluteAdapterPosition,commentParentIndex)
                        true
                    }
                    else -> false
                }
            }
            return false
        }
    }


}