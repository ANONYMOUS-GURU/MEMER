package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.MODELS.UserData
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class FragmentProfile : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private val viewModel: ViewModelUserInfo by activityViewModels()
    private lateinit var loadingDialog:LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        loadingDialog = LoadingDialog(requireActivity())

        mAuth = Firebase.auth

        binding.editProfileButtonProfilePage.setOnClickListener(this)
        binding.profilePageImage.setOnClickListener(this)

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.fragmentProfile))
        binding.profilePageToolbar.setupWithNavController(navController, appBarConfiguration)
        binding.profilePageToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOut -> {
                    signOut()
                    true
                }
                else -> false
            }
        }
        viewModel.userLD.observe(viewLifecycleOwner, Observer {
            getUserData(it)
        })
        getUserData(viewModel.userLD.value)
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.editProfileButtonProfilePage -> navController.navigate(R.id.action_fragmentProfile_to_fragmentEditProfile)
                binding.profilePageImage.id -> navController.navigate(R.id.action_fragmentProfile_to_fragmentProfilePic)
            }
        }
    }

    private fun signOut() {
        loadingDialog.startLoadingDialog("Signing Out ... ")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.signOut().addOnCompleteListener {
            if(it.isSuccessful){
                viewModel.signOut()
                Toast.makeText(context,"Successfully signed out",Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_global_fragmentLogIn)
            }
            else{
                Toast.makeText(context,"Error Logging Out",Toast.LENGTH_SHORT).show()
            }
            loadingDialog.dismissDialog()
        }
    }
    private fun getUserData(user: UserData?) {
        if (user != null) {
            binding.apply {
                bioProfilePage.text = user.bio
                usernameProfilePage.text = user.username
                nameOfUserProfilePage.text = user.nameOfUser
                profileFollowingCount.text = user.userFollowersCount.toString()
                profileFollowersCount.text = user.userFollowersCount.toString()
                profilePostCount.text = user.userPostCount.toString()
            }
            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(binding.profilePageImage.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(user.userAvatarReference)
                .circleCrop()
                .into(binding.profilePageImage)
        }
    }

}