package com.example.memer.ADAPTERS

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.GalleryItem
import com.example.memer.MODELS.TemplatesItem
import com.example.memer.R
import kotlinx.android.synthetic.main.gallery_item_view.view.*
import kotlinx.android.synthetic.main.imageview_post_profile_page.view.*

class AdapterTemplates(
    private val itemClickListener: ItemClickListener,
    private val mContext: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "AdapterTemplates"
    }

    private var items: List<TemplatesItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TemplatesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_item_view, parent, false),
            itemClickListener,
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TemplatesViewHolder -> {
                holder.bind(items[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(itemList: List<TemplatesItem>?) {
        if (itemList != null) {
            items = itemList
        }
    }

    class TemplatesViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imageGalley: ImageView = itemView.galleryItemImage

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(templatesItem: TemplatesItem) {
//            val requestOptionsPost = RequestOptions()
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_background)
//
//            Glide.with(mContext)
//                .load(templatesItem.pictureUri.getPath())
//                .into(imageGalley);
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(absoluteAdapterPosition)
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

}
