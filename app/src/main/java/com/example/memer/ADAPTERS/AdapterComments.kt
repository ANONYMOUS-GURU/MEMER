package com.example.memer.ADAPTERS

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memer.R
import com.example.memer.databinding.FragmentCommentsBinding
import kotlinx.android.synthetic.main.comment_single_view.view.*

class AdapterComments(val itemClickListener: ItemClickListener, val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mList:ArrayList<Int> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    fun submitList(commentList:List<Int>){

    }

    class CommentsViewHolder(
        itemView: View,
        private val itemClickListener: HomePageAdapter.ItemClickListener,
        private val mContext: Context,
    ) : RecyclerView.ViewHolder(itemView),View.OnClickListener {


        private val username: TextView = itemView.findViewById(R.id.usernameComment)
        private val userAvatar: ImageView = itemView.findViewById(R.id.userAvatarComment)
        private val commentContent: TextView = itemView.findViewById(R.id.userCommentCommentPage)
        private val likedIcon: ImageView = itemView.findViewById(R.id.likeComment)
        private val reply:TextView = itemView.findViewById(R.id.replyComment)
        private val timeComment:TextView = itemView.findViewById(R.id.timeComment)


        fun bind(){

            username.text = ""
            commentContent.text = ""
            if(true){
                likedIcon.setImageResource(R.drawable.like_icon_filled)
            }
            else{
                likedIcon.setImageResource(R.drawable.like_icon_border)
            }

            timeComment.text = getTime()

            userAvatar.setOnClickListener(this)
            username.setOnClickListener(this)
            likedIcon.setOnClickListener(this)
            reply.setOnClickListener(this)

        }

        private fun getTime():String{
            return "10h"
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }


    }

    interface ItemClickListener {
        fun onItemClick()
    }
}