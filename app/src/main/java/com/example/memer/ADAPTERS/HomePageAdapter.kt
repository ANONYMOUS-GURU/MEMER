package com.example.memer.ADAPTERS

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.PostHomePage
import com.example.memer.MODELS.PostState
import com.example.memer.R
import kotlinx.android.synthetic.main.post_comment_view.view.*
import kotlinx.android.synthetic.main.single_meme_view.view.*


class HomePageAdapter(
    private val itemClickListener: ItemClickListener,
    private val onMenuClick: OnMenuClickListener,
    private val mContext: Context,
    private val userId: String
    ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<PostHomePage> = ArrayList()
    private  var currentState:PostState = PostState.InitialLoading

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> HomePageAnimationViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.single_meme_view, parent, false),
                itemClickListener,
                onMenuClick,
                mContext,
                userId
            )
            else -> HomePageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.single_meme_view, parent, false),
                itemClickListener,
                onMenuClick,
                mContext,
                userId
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomePageViewHolder -> {
                holder.bind(items[position])
            }
            is HomePageAnimationViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return when(currentState){
            is PostState.Loaded -> items.size
            is PostState.InitialLoading -> 2
            is PostState.LoadingFailed -> items.size
            is PostState.Refreshing -> items.size
            is PostState.LoadingMoreData -> items.size
        }
    }

    fun getPost(position: Int) : PostHomePage{
        return items[position]
    }


    fun submitList(postList: List<PostHomePage>?) {
        if (postList != null) {
            items = postList
        }
    }
    fun submitState(postState: PostState){
        currentState = postState
    }

    class HomePageViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val onMenuClick: OnMenuClickListener,
        private val mContext: Context,
        private val userId: String
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener,PopupMenu.OnMenuItemClickListener {

        private val userAvatar: ImageView = itemView.userAvatarHome
        private val username: TextView = itemView.usernameHome
        private val imagePost: ImageView = itemView.imagePostHomePage
        private val menuOption: ImageView = itemView.menuOnItemHome
        private val addComment: View = itemView.commentHolderHomePagePost
        private val bookmark: ImageView = itemView.bookmarkHome
        private val likeOption: ImageView = itemView.likeOptionHome
        private val likesCount: TextView = itemView.likeCountHomePage
        private val commentsCount: TextView = itemView.viewMoreCommentPost
        private val postCaption:TextView = itemView.postCationTextView
        private val commentOption:View = itemView.commentOptionLayout
        private lateinit var popupMenu: PopupMenu


        @SuppressLint("SetTextI18n")
        fun bind(postHomePage: PostHomePage) {

            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(postHomePage.postContents.userAvatarReference)
                .circleCrop()
                .into(userAvatar)


            val requestOptionsPost = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptionsPost)
                .load(postHomePage.postContents.postResource)
                .into(imagePost)

            username.text = postHomePage.postContents.username

            if(postHomePage.isBookmarked)
                bookmark.setImageDrawable(getDrawable(mContext, R.drawable.bookmark_filled_black))
            else
                bookmark.setImageDrawable(getDrawable(mContext, R.drawable.bookmark_border_black))


            if(postHomePage.isLiked)
                likeOption.setImageDrawable(getDrawable(mContext, R.drawable.like_icon_filled))
            else
                likeOption.setImageDrawable(getDrawable(mContext, R.drawable.like_icon_border))


            if(postHomePage.postContents.postDescription.isEmpty())
                postCaption.visibility = View.GONE
            else{
                postCaption.visibility = View.VISIBLE
                postCaption.text = postHomePage.postContents.postDescription
            }

            popupMenu = addMenuItem(menuOption, postHomePage.postContents.postOwnerId == userId)

            likesCount.text = postHomePage.postContents.likeCount.toString()
            /*
            * TODO(PostHomePage add first comment of that post as postContents.exampleComment as a
            *  nullable array if postHomePage.exampleComment == null addComment.visibility = View.GONE else commentsCount.text = "See ..." and visible)
            * */
            if(postHomePage.postContents.commentCount > 0 )
            {
                addComment.visibility = View.VISIBLE
                commentsCount.text = " See All ${postHomePage.postContents.commentCount.toString()} Comments"
                addComment.setOnClickListener(this)
            } else{
                addComment.visibility = View.GONE
            }

            userAvatar.setOnClickListener(this)
            username.setOnClickListener(this)
            imagePost.setOnClickListener(this)
            likeOption.setOnClickListener(this)
            bookmark.setOnClickListener(this)
            menuOption.setOnClickListener(this)
            likesCount.setOnClickListener(this)
            commentOption.setOnClickListener(this)

        }

        private fun addMenuItem(itemView: View, postOwnerIsUser: Boolean = false):PopupMenu{
            val popup = PopupMenu(mContext, itemView)
            if(postOwnerIsUser)
                popup.menuInflater.inflate(R.menu.homepage_post_menu_is_user, popup.menu)
            else
                popup.menuInflater.inflate(R.menu.homepage_post_menu, popup.menu)

            popup.setOnMenuItemClickListener(this)
            return popup
        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    userAvatar.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    username.id -> itemClickListener.onUserClick(absoluteAdapterPosition)
                    likeOption.id -> itemClickListener.onLikeClick(absoluteAdapterPosition)
                    addComment.id -> itemClickListener.onCommentClick(absoluteAdapterPosition)
                    commentOption.id -> itemClickListener.onCommentClick(absoluteAdapterPosition)
                    bookmark.id -> itemClickListener.onBookMarkClick(absoluteAdapterPosition)
                    likesCount.id -> itemClickListener.onLikeListClick(absoluteAdapterPosition)
                    menuOption.id -> popupMenu.show()
                }
            }
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (item != null) {
                when(item.itemId){
                    R.id.sharePost -> {
                        onMenuClick.sharePostClick(absoluteAdapterPosition)
                        return true
                    }
                    R.id.editPost -> {
                        onMenuClick.editPostClick(absoluteAdapterPosition)
                    }
                    R.id.deletePost -> {
                        onMenuClick.deletePostClick(absoluteAdapterPosition)
                        return true
                    }
                    R.id.copyLinkPost -> {
                        onMenuClick.copyLinkPostClick(absoluteAdapterPosition)
                        return true
                    }
                    R.id.reportPost -> {
                        onMenuClick.reportPostClick(absoluteAdapterPosition)
                        return true
                    }
                    R.id.savePost -> {
                        onMenuClick.savePostClick(absoluteAdapterPosition)
                        return true
                    }
                    else -> return false
                }
            }
            return false
        }
    }

    class HomePageAnimationViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val onMenuClick: OnMenuClickListener,
        private val mContext: Context,
        private val userId: String
    ) : RecyclerView.ViewHolder(itemView) {

        private val rootLayout: ConstraintLayout = itemView.singleMemeConstraintRootLayout
        private val userAvatar: ImageView = itemView.userAvatarHome
        private val username: TextView = itemView.usernameHome
        private val imagePost: ImageView = itemView.imagePostHomePage
        private val menuOption: ImageView = itemView.menuOnItemHome
        private val addComment: View = itemView.commentHolderHomePagePost
        private val bookmark: ImageView = itemView.bookmarkHome
        private val likeOption: ImageView = itemView.likeOptionHome
        private val likesCount: TextView = itemView.likeCountHomePage
        private val commentsCount: TextView = itemView.viewMoreCommentPost
        private val postCaption:TextView = itemView.postCationTextView
        private val commentOption:View = itemView.commentOptionLayout

        private val relativeLayoutUserHolder : RelativeLayout  = itemView.relativeLayoutSingleMemeUserHolder
        private val relativeLayoutLikeCommentHolder : RelativeLayout  = itemView.relativeLayoutCommentLikeHolderSingleMeme
        private val linearLayoutPostHolder : LinearLayout = itemView.postHolderLinearLayoutSingleMeme

        fun bind() {
            userAvatar.visibility = View.INVISIBLE
            username.visibility = View.INVISIBLE
            imagePost.visibility = View.INVISIBLE
            menuOption.visibility = View.INVISIBLE
            addComment.visibility = View.INVISIBLE
            bookmark.visibility = View.INVISIBLE
            likeOption.visibility = View.INVISIBLE
            likesCount.visibility = View.INVISIBLE
            commentsCount.visibility = View.INVISIBLE
            postCaption.visibility = View.INVISIBLE
            commentOption.visibility = View.INVISIBLE

            startAnimation(relativeLayoutUserHolder)
            startAnimation(linearLayoutPostHolder)
            startAnimation(relativeLayoutLikeCommentHolder)

        }

        private fun startAnimation(view:View){
            view.background = getDrawable(mContext, R.drawable.loading_animation)
            val animationDrawable = view.background as AnimationDrawable
            animationDrawable.setEnterFadeDuration(500)
            animationDrawable.setExitFadeDuration(500)
            animationDrawable.start()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(currentState is PostState.InitialLoading)  0 else 1
    }


    interface ItemClickListener {
        fun onImageItemClick(position: Int)
        fun onVideoItemClick(position: Int)
        fun onLikeClick(position: Int)
        fun onCommentClick(position: Int)
        fun onBookMarkClick(position: Int)
        fun onUserClick(position: Int)
        fun onLikeListClick(position: Int)
    }
    interface OnMenuClickListener{
        fun sharePostClick(position: Int)
        fun editPostClick(position: Int)
        fun deletePostClick(position: Int)
        fun copyLinkPostClick(position: Int)
        fun reportPostClick(position: Int)
        fun savePostClick(position: Int)
    }

}