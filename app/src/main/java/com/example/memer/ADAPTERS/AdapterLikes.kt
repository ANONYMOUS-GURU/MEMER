package com.example.memer.ADAPTERS

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.LikedBy
import com.example.memer.R
import kotlinx.android.synthetic.main.single_like_element.view.*

class AdapterLikes(private val itemClickListener: ItemClickListener, val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<LikedBy> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LikesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.single_like_element, parent, false),
            itemClickListener,
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LikesViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getUserId(position: Int):String{
        return items[position].userId
    }

    fun submitList(postList: List<LikedBy>) {
        items = postList
    }

    class LikesViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val userAvatar: ImageView = itemView.userAvatarLikePage
        private val username: TextView = itemView.usernameLikePage
        private val nameOfUser: TextView = itemView.nameOfUserLikePage
        private val followButton: Button = itemView.followButtonLikePage

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(likedBy: LikedBy) {

            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(likedBy.userAvatarReference)
                .circleCrop()
                .into(userAvatar)



            userAvatar.setOnClickListener(this)
            username.setOnClickListener(this)
            nameOfUser.setOnClickListener(this)
            followButton.setOnClickListener(this)

            username.text = likedBy.username
            nameOfUser.text = likedBy.nameOfUser
        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    nameOfUser.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    userAvatar.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    username.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    followButton.id -> itemClickListener.onFollowButtonClick(absoluteAdapterPosition)
                }
            }
        }
    }


    interface ItemClickListener{
        fun onUserClick(position: Int)
        fun onFollowButtonClick(position: Int)
    }
}