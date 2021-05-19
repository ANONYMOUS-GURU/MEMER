package com.example.memer.ADAPTERS

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.PostThumbnail
import com.example.memer.R
import kotlinx.android.synthetic.main.imageview_post_profile_page.view.*

class AdapterFragmentYourPosts(
    private val itemClickListener: ItemClickListener,
    private val mContext: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val TAG = "AdapterFragmentYourPost"
    }

    private var items: List<PostThumbnail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return YourPostsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.imageview_post_profile_page, parent, false),
            itemClickListener,
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is YourPostsViewHolder -> {
                holder.bind(items[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(blogList: List<PostThumbnail>?) {
        if(blogList!=null){
            items = blogList
        }
    }

    class YourPostsViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView),View.OnClickListener {

        private val imagePost: ImageView = itemView.imageViewPostProfilePage

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(postThumbnail:PostThumbnail){
            val requestOptionsPost = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsPost)
                .load(postThumbnail.thumbnailReference)
                .into(imagePost)
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(absoluteAdapterPosition)
        }
    }

     interface ItemClickListener {
        fun onItemClick(position: Int)
    }

}
