package com.example.memer.ADAPTERS

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.GalleryItem
import com.example.memer.R
import kotlinx.android.synthetic.main.gallery_item_view.view.*
import kotlinx.android.synthetic.main.imageview_post_profile_page.view.*

class AdapterGallery(
    private val itemClickListener: ItemClickListener,
    private val mContext: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "AdapterFragmentYourPost"
    }

    private var items: List<GalleryItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GalleryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_item_view, parent, false),
            itemClickListener,
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GalleryViewHolder -> {
                holder.bind(items[position])
            }

        }
    }

    override fun getItemCount(): Int {
        Log.i(TAG, "getItemCount: ${items.size}")
        return items.size
    }

    fun submitList(itemList: List<GalleryItem>?) {
        if (itemList != null) {
            items = itemList
            notifyDataSetChanged()
        }
    }

    fun getUri(position: Int): Uri {
        return items[position].uri
    }

    class GalleryViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imageGallery: ImageView = itemView.galleryItemImage

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(galleryItem: GalleryItem) {

            val requestOptionsPost = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(mContext)
                .applyDefaultRequestOptions(requestOptionsPost)
                .load(galleryItem.uri)
                .thumbnail(0.33f)
                .centerCrop()
                .into(imageGallery)

        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(absoluteAdapterPosition)
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

}
