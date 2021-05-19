package com.example.memer.ADAPTERS

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.R
import kotlinx.android.synthetic.main.imageview_post_profile_page.view.*

class AdapterFragmentSearch(private val itemClickListener: ItemClickListener, private val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var items: List<String> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FragmentSearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.imageview_post_profile_page, parent, false),
            itemClickListener,
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FragmentSearchViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(blogList: List<String>) {
        items = blogList
    }

    class FragmentSearchViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imagePost: ImageView = itemView.imageViewPostProfilePage

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(imageSrc:String){
            val requestOptionsPost = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsPost)
                .load(imageSrc)
                .into(imagePost)
        }

        override fun onClick(v: View?) {
            itemClickListener.onImageItemClick(absoluteAdapterPosition)
        }


    }

    public interface ItemClickListener {
        fun onImageItemClick(position: Int)
        fun onVideoItemClick(position: Int)
    }

}