package com.example.memer.ADAPTERS

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostHomePage
import com.example.memer.R
import kotlinx.android.synthetic.main.single_meme_view.view.*

class HomePageAdapter(private val itemClickListener: ItemClickListener, val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<PostHomePage> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HomePageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.single_meme_view, parent, false),
            itemClickListener,
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomePageViewHolder -> {
                holder.bind(items[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getPost(position: Int) : PostHomePage{
        return items[position]
    }


    fun submitList(postList: List<PostHomePage>?) {
        if (postList != null) {
            items = postList
        }
    }

    class HomePageViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val userAvatar: ImageView = itemView.userAvatarHome
        private val username: TextView = itemView.usernameHome
        private val imagePost: ImageView = itemView.imagePostHomePage
        private val menuOnItem: TextView = itemView.menuOnItemHome
        private val addComment: ImageView = itemView.commentHome
        private val bookmark: ImageView = itemView.bookmarkHome
        private val likeOption: ImageView = itemView.likeOptionHome
        private val likesCount: TextView = itemView.likeCountHomePage
        private val commentsCount: TextView = itemView.commentCountHomePage

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(postHomePage: PostHomePage) {

            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(postHomePage.userAvatarReference)
                .circleCrop()
                .into(userAvatar)

            val requestOptionsPost = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsPost)
                .load(postHomePage.postContents.postResource)
                .into(imagePost)

            userAvatar.setOnClickListener(this)
            username.setOnClickListener(this)
            imagePost.setOnClickListener(this)
            likeOption.setOnClickListener(this)
            addComment.setOnClickListener(this)
            bookmark.setOnClickListener(this)
            menuOnItem.setOnClickListener(this)
            likesCount.setOnClickListener(this)
            commentsCount.setOnClickListener(this)

            username.text = postHomePage.username

            if(postHomePage.likeCount > 0)
                likeOption.setImageDrawable(getDrawable(mContext,R.drawable.like_icon_filled))
            else
                likeOption.setImageDrawable(getDrawable(mContext,R.drawable.like_icon_border))

            if(postHomePage.isCommented)
                addComment.setImageDrawable(getDrawable(mContext,R.drawable.default_avatar))
            else
                addComment.setImageDrawable(getDrawable(mContext,R.drawable.comment_icon))

            likesCount.text = postHomePage.postContents.likeCount.toString()
            commentsCount.text = postHomePage.postContents.commentCount.toString()

        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    itemView.id -> itemClickListener.onImageItemClick(absoluteAdapterPosition)
                    userAvatar.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    username.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    likeOption.id -> itemClickListener.onLikeClick(absoluteAdapterPosition)
                    addComment.id -> itemClickListener.onCommentClick(absoluteAdapterPosition)
                    commentsCount.id -> itemClickListener.onCommentClick(absoluteAdapterPosition)
                    bookmark.id -> itemClickListener.onBookMarkClick(absoluteAdapterPosition)
                    menuOnItem.id -> itemClickListener.onMenuClick(absoluteAdapterPosition)
                }
            }
        }
    }

    interface ItemClickListener {
        fun onImageItemClick(position: Int)
        fun onVideoItemClick(position: Int)
        fun onLikeClick(position: Int)
        fun onCommentClick(position: Int)
        fun onBookMarkClick(position: Int)
        fun onUserClick(position: Int)
        fun onMenuClick(position: Int)
    }

}