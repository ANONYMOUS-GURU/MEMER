package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.UserData
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentEditProfileBinding
import kotlinx.android.synthetic.main.activity_main.*

class FragmentEditProfile : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var navController: NavController
    private val viewModel: ViewModelUserInfo by activityViewModels()

    companion object{
        const val TAG = "FragmentEditProfile"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        requireActivity().bottomNavigationView.visibility = View.GONE
        getUserData(viewModel.userLD.value)
        binding.profileImageEditProfile.setOnClickListener(this)
        binding.removeProfilePictureEditProfile.setOnClickListener(this)
        return binding.root
    }

    private fun getUserData(user: UserData?) {
        if (user != null) {
            binding.apply {
                usernameEditProfileText.setText(user.username)
                nameEditProfileText.setText(user.nameOfUser)
                bioEditProfileText.setText(user.bio)
            }

            val requestOptionsAvatar = RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)

            Glide.with(binding.profileImageEditProfile.context)
                .applyDefaultRequestOptions(requestOptionsAvatar)
                .load(user.userAvatarReference)
                .circleCrop()
                .into(binding.profileImageEditProfile)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        if(viewModel.userLD.value !! .isNewUser) {
            Log.d(TAG, "onViewCreated: is New User")
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.fragmentEditProfile))
            binding.editProfilePageToolbar.setupWithNavController(navController,appBarConfiguration)
        }
        else
            binding.editProfilePageToolbar.setupWithNavController(navController)

        viewModel.userLD.observe(viewLifecycleOwner, {
            if(it!=null)
                getUserData(it)
        })



        binding.editProfilePageToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.confirmEditProfile -> {
                    saveChanges()
                    navController.navigateUp()
                    true
                }
                else -> false
            }
        }
        // TODO("Override navigateUp instead of using the hack of removing the button altogether")
//        if(! viewModel.userLD.value !! .isNewUser)
//            binding.editProfilePageToolbar.setNavigationIcon(R.drawable.close_icon)
    }

    private fun setImageResource(value: Pair<String?,String?>) {
        viewModel.updateUserImageReference(value , viewModel.userLD.value !! .userId)
    }
    private fun saveChanges() {
        val bio = binding.bioEditProfileText.text.toString()
        val username = binding.usernameEditProfileText.text.toString()
        val name = binding.nameEditProfileText.text.toString()
        val user = UserData(
            viewModel.userLD.value !! .userId,username,name,viewModel.userLD.value !!.signInType,
            viewModel.userLD.value!!.phoneNumber,bio,viewModel.userLD.value!!.userProfilePicReference,
            viewModel.userLD.value!!.userPostCount,viewModel.userLD.value!!.userFollowersCount,
            viewModel.userLD.value!!.userFollowingCount,viewModel.userLD.value!!.userAvatarReference,
            viewModel.userLD.value!!.isAuthenticated,viewModel.userLD.value!!.isNewUser
        )


        viewModel.updateUser(user)
        // TODO(save changes in cloud)
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.profileImageEditProfile -> {    // TODO("upload and change image reference")
                    navController.navigate(R.id.action_fragmentEditProfile_to_fragmentProfilePic)
                }
                R.id.removeProfilePictureEditProfile -> {
                    setImageResource(null to null)
                    Toast.makeText(context, "Changing Image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}