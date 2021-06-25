package com.example.memer.ADAPTERS

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostState
import com.example.memer.MODELS.PostThumbnailState
import com.example.memer.R
import kotlinx.android.synthetic.main.imageview_post_profile_page.view.*

class AdapterFragmentYourPosts(
    private val itemClickListener: ItemClickListener,
    private val mContext: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "AdapterFragmentYourPost"
    }

    private var items: List<PostContents2> = ArrayList()
    private var currentState: PostThumbnailState = PostThumbnailState.InitialLoading

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> YourPostsAnimationViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.imageview_post_profile_page, parent, false),
                itemClickListener,
                mContext
            )
            else -> YourPostsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.imageview_post_profile_page, parent, false),
                itemClickListener,
                mContext
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is YourPostsViewHolder -> {
                holder.bind(items[position])
            }
            is YourPostsAnimationViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return when (currentState) {
            is PostThumbnailState.Loaded -> items.size
            is PostThumbnailState.InitialLoading -> 16
            is PostThumbnailState.LoadingFailed -> items.size
            is PostThumbnailState.Refreshing -> items.size
            is PostThumbnailState.LoadingMoreData -> items.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentState is PostThumbnailState.InitialLoading) 0 else 1
    }

    fun submitList(postList: List<PostContents2>?) {
        if (postList != null) {
            items = postList
        }
    }

    fun submitState(postThumbnailState: PostThumbnailState) {
        currentState = postThumbnailState
    }

    class YourPostsViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imagePost: ImageView = itemView.imageViewPostProfilePage

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(userPost: PostContents2) {
            val requestOptionsPost = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsPost)
                .load(userPost.postResource)
                .into(imagePost)
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(absoluteAdapterPosition)
        }
    }


    class YourPostsAnimationViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {
        private val imagePost: ImageView = itemView.imageViewPostProfilePage
        fun bind() {
            startAnimation(imagePost)
        }

        private fun startAnimation(view: View) {
            view.background = AppCompatResources.getDrawable(mContext, R.drawable.loading_animation)
            val animationDrawable = view.background as AnimationDrawable
            animationDrawable.setEnterFadeDuration(500)
            animationDrawable.setExitFadeDuration(500)
            animationDrawable.start()
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

}
