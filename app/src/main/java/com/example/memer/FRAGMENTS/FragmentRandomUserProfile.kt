package com.example.memer.FRAGMENTS

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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

class FragmentRandomUserProfile : Fragment() , View.OnClickListener{

    private lateinit var binding:FragmentRandomUserProfileBinding
    private  val viewModelUser : ViewModelUserInfo by activityViewModels()
    private lateinit var navController : NavController
    private lateinit var randomUserId : String
    private lateinit var randomUserProfile: UserProfileInfo
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

        return binding.root
    }

    private fun initView(it:UserProfileInfo){
        randomUserProfile = it
        binding.bioRandomProfilePage.text = randomUserProfile.bio
        binding.nameOfUserRandomProfilePage.text  = randomUserProfile.nameOfUser
        binding.randomProfileFollowersCount.text = randomUserProfile.followersCount.toString()
        binding.randomProfileFollowingCount.text = randomUserProfile.followingCount.toString()
        binding.randomProfilePostCount.text  = randomUserProfile.postCount.toString()
        binding.usernameRandomProfilePage.text = randomUserProfile.username

        val requestOptionsAvatar = RequestOptions()
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)

        Glide.with(binding.randomProfilePageImage.context)
            .applyDefaultRequestOptions(requestOptionsAvatar)
            .load(randomUserProfile.userAvatarReference)
            .circleCrop()
            .into(binding.randomProfilePageImage)

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

        binding.followRandomUserButton.setOnClickListener(this)
        binding.messageRandomUserButton.setOnClickListener(this)

    }
    @SuppressLint("SetTextI18n")
    private fun changeFollowButton(status:Boolean){
        if(status){
            binding.followRandomUserButton.text = "UnFollow"
        }else{
            binding.followRandomUserButton.text = "Follow"
        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        viewModelRandomUser.dataLD.observe(viewLifecycleOwner,{
            initView(it)
        })
        viewModelRandomUser.postLD.observe(viewLifecycleOwner,{
            showPosts(it)
        })
        viewModelRandomUser.isFollowingLD.observe(viewLifecycleOwner,{
            changeFollowButton(it)
        })

    }
    override fun onClick(v: View?) {
        when (v?.id){
            binding.messageRandomUserButton.id -> {}
            binding.followRandomUserButton.id  -> {
                viewModelRandomUser.followUser()
            }
        }
    }

}