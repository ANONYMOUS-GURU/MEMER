package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels

import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.ADAPTERS.AdapterFragmentProfile
import com.example.memer.MODELS.UserEditableInfo
import com.example.memer.MODELS.UserNonEditInfo
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class FragmentProfile : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private val viewModel: ViewModelUserInfo by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        mAuth = Firebase.auth

        viewModel.userEditLiveData.observe(viewLifecycleOwner, Observer {
            getUserData(it)
        })
        viewModel.userNonEditInfoLiveData.observe(viewLifecycleOwner, Observer {
            getUserData(it)
        })
        viewModel.userImageReferenceLiveData.observe(viewLifecycleOwner, Observer {
            getImageReference(it)
        })

        binding.editProfileButtonProfilePage.setOnClickListener(this)

        val viewPager: ViewPager2 = binding.viewPagerProfilePage
        viewPager.adapter = AdapterFragmentProfile(this)

        val tabLayout = binding.tabLayoutProfilePage
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
//                    tab.text = "Your Posts"
                    tab.icon =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
                1 -> {
//                    tab.text = "Tagged Posts"
                    tab.icon =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
                else -> {
//                    tab.text = "Your Posts"
                    tab.icon =
                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
            }
        }.attach()

        return binding.root
    }

    private fun getUserData(user: UserEditableInfo?) {
        if (user != null) {
            binding.apply {
                bioProfilePage.text = user.bio
                usernameProfilePage.text = user.username
                nameOfUserProfilePage.text = user.nameOfUser
            }
        }

    }

    private fun getUserData(user: UserNonEditInfo?) {
        if (user != null) {
            binding.apply {
                profileFollowingCount.text = user.followingCount.toString()
                profileFollowersCount.text = user.followersCount.toString()
                profilePostCount.text = user.postCount.toString()
            }
        }
    }

    private fun getImageReference(imageReference: Pair<String?,String?>) {

        val requestOptionsAvatar = RequestOptions()
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)

        Glide.with(binding.profilePageImage.context)
            .applyDefaultRequestOptions(requestOptionsAvatar)
            .load(imageReference.second)
            .circleCrop()
            .into(binding.profilePageImage)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.fragmentProfile))
        binding.profilePageToolbar.setupWithNavController(navController, appBarConfiguration)
        binding.profilePageToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.fragmentAddPost -> {
                    navController.navigate(R.id.action_fragmentProfile_to_fragmentAddPost)
                    true
                }
                R.id.fragmentLogIn -> {
                    mAuth.signOut()
                    navController.navigate(R.id.action_global_fragmentLogIn)
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.editProfileButtonProfilePage -> navController.navigate(R.id.action_fragmentProfile_to_fragmentEditProfile)
            }
        }
    }

}