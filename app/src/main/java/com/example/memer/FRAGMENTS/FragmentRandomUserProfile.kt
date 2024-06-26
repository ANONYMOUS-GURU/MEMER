package com.example.memer.FRAGMENTS

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.ADAPTERS.AdapterRandomUserProfile
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.UserData
import com.example.memer.MODELS.UserProfileInfo
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelRandomUserProfile
import com.example.memer.VIEWMODELS.ViewModelRandomUserProfileFactory
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentRandomUserProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_random_user_profile.*

class FragmentRandomUserProfile : Fragment() , View.OnClickListener{

    private lateinit var binding:FragmentRandomUserProfileBinding
    private  val viewModelUser : ViewModelUserInfo by activityViewModels()
    private lateinit var navController : NavController
    private lateinit var randomUserId : String
    private  var randomUserProfile: UserProfileInfo? = null
    private lateinit var userData : UserData
    private val args: FragmentRandomUserProfileArgs by navArgs()
    //TODO (DECLARE INSIDE OnCREATE)
    private val viewModelRandomUser : ViewModelRandomUserProfile by navGraphViewModels(R.id.nav_random_profile){
        ViewModelRandomUserProfileFactory(viewModelUser.userLD.value !! .userId , args.randomUserId)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomUserProfileBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE
        randomUserId =args.randomUserId
        userData = viewModelUser.userLD.value !!

        initView(null)

        return binding.root
    }

    private fun initView(userProfileInfo: UserProfileInfo?){
        randomUserProfile = userProfileInfo
        if(randomUserProfile == null){
            // MAKE THE LOADING SCREEN
            startAnimation(binding.followMessageLayout)
            startAnimation(binding.userProfileInfoLayout)
        }
        else{
            stopAnimation(binding.followMessageLayout)
            stopAnimation(binding.userProfileInfoLayout)

            binding.bioRandomProfilePage.text = randomUserProfile?.bio
            binding.nameOfUserRandomProfilePage.text  = randomUserProfile?.nameOfUser
            binding.randomProfilePostCount.text  = randomUserProfile?.postCount.toString()
            binding.usernameRandomProfilePage.text = randomUserProfile?.username

            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(binding.randomProfilePageImage.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(randomUserProfile?.userAvatarReference)
                .circleCrop()
                .into(binding.randomProfilePageImage)

            binding.followRandomUserButton.setOnClickListener(this)
            binding.messageRandomUserButton.setOnClickListener(this)
        }



        val viewPager: ViewPager2 = binding.viewPagerRandomProfilePage
        viewPager.adapter = AdapterRandomUserProfile(this)

        val tabLayout = binding.tabLayoutRandomProfilePage
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
//                    tab.text = "Your Posts"
                    tab.icon =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
//                1 -> {
////                    tab.text = "Tagged Posts"
//                    tab.icon =
//                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
//                }
                else -> {
//                    tab.text = "Your Posts"
                    tab.icon =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
            }
        }.attach()



    }

    private fun showPosts(post : ArrayList<PostContents2>){
        if(post.size == 0){
            binding.randomUserAccountPrivateLayout.visibility = View.VISIBLE
            binding.randomUserPosts.visibility = View.GONE
        }else{
            binding.randomUserPosts.visibility = View.VISIBLE
            binding.randomUserAccountPrivateLayout.visibility = View.GONE
        }
    }

    private fun startAnimation(view:ViewGroup){
        view.children.forEach {
            it.visibility = View.INVISIBLE
        }

        view.background = AppCompatResources.getDrawable(requireContext(), R.drawable.loading_animation)
        val animationDrawable = view.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(600)
        animationDrawable.setExitFadeDuration(600)
        animationDrawable.start()
    }
    private fun stopAnimation(view:ViewGroup){
        view.children.forEach {
            it.visibility = View.VISIBLE
        }
        view.setBackgroundResource(0)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewModelRandomUser.dataLD.observe(viewLifecycleOwner, {
            Log.d(TAG, "onViewCreated: ${it.postCount}")
            initView(it)
        })
//        viewModelRandomUser.postLD.observe(viewLifecycleOwner,{
//            showPosts(it)
//        })
    }
    override fun onClick(v: View?) {
        when (v?.id){
            binding.messageRandomUserButton.id -> {}
        }
    }

    companion object{
        private const val TAG = "FRandomUserProfile"
    }
}