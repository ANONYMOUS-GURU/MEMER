package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.UserData
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentRandomUserProfileBinding
import kotlinx.android.synthetic.main.activity_main.*

class FragmentRandomUserProfile : Fragment() , View.OnClickListener{

    private lateinit var binding:FragmentRandomUserProfileBinding
    private  val viewModelUser : ViewModelUserInfo by activityViewModels()
    private lateinit var navController : NavController
    private lateinit var mUser : UserData
    private lateinit var randomUserProfile : UserData
    private val args: FragmentRandomUserProfileArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomUserProfileBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        randomUserProfile =args.randomUser
        initView()

        return binding.root
    }

    private fun initView(){
        binding.bioRandomProfilePage.text = randomUserProfile.bio
        binding.nameOfUserRandomProfilePage.text  = randomUserProfile.nameOfUser
        binding.randomProfileFollowersCount.text = randomUserProfile.userFollowersCount.toString()
        binding.randomProfileFollowingCount.text = randomUserProfile.userFollowingCount.toString()
        binding.randomProfilePostCount.text  = randomUserProfile.userPostCount.toString()
        binding.usernameRandomProfilePage.text = randomUserProfile.username

        val requestOptionsAvatar = RequestOptions()
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)

        Glide.with(binding.randomProfilePageImage.context)
            .applyDefaultRequestOptions(requestOptionsAvatar)
            .load(randomUserProfile.userAvatarReference)
            .circleCrop()
            .into(binding.randomProfilePageImage)

        binding.followRandomUserButton.setOnClickListener(this)
        binding.messageRandomUserButton.setOnClickListener(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        mUser = viewModelUser.userLD.value !!




    }

    override fun onClick(v: View?) {
        when (v?.id){
            binding.messageRandomUserButton.id -> {}
            binding.followRandomUserButton.id  -> {}
        }
    }

}