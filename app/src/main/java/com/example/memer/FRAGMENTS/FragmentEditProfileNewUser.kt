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
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.LoginState
import com.example.memer.MODELS.UserData
import com.example.memer.MODELS.UserTextEditInfo
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelLogin
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentEditProfileBinding
import com.example.memer.databinding.FragmentEditProfileNewUserBinding
import kotlinx.android.synthetic.main.activity_main.*

class FragmentEditProfileNewUser : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentEditProfileNewUserBinding
    private lateinit var navController: NavController
    private val viewModel: ViewModelLogin by navGraphViewModels(R.id.navigation_Log_in)

    companion object{
        const val TAG = "FEditProfileNewUser"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileNewUserBinding.inflate(inflater, container, false)
        requireActivity().bottomNavigationView.visibility = View.GONE
        binding.profileImageEditProfileNewUser.setOnClickListener(this)
        binding.removeProfilePictureEditProfileNewUser.setOnClickListener(this)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        viewModel.imageRefLD.observe(viewLifecycleOwner,{
            setAvatar(it)
        })
        viewModel.loginStateLD.observe(viewLifecycleOwner,{
            if(it is LoginState.Completed){
                navController.navigate(R.id.action_global_fragmentHomePage)
            }
        })

        Log.d(TAG, "onViewCreated: is New User")

        binding.editProfilePageNewUserToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.confirmEditProfile -> {

                    val username = binding.usernameEditProfileNewUserText.text.toString()
                    val nameOfUser = binding.nameEditProfileNewUserText.text.toString()
                    val bio = binding.bioEditProfileNewUserText.text.toString()

                    if(valid(username,nameOfUser,bio)){
                        viewModel.writeNewUser(UserTextEditInfo(username,nameOfUser,bio,viewModel.imageRefLD.value))
                    }

                    true
                }
                else -> false
            }
        }
    }
    override fun onStart() {
        super.onStart()
        setUserTextData(viewModel.userTextEditInfo)
    }
    private fun setUserTextData(userTextEditInfo: UserTextEditInfo){
        binding.apply {
            usernameEditProfileNewUserText.setText(userTextEditInfo.username)
            nameEditProfileNewUserText.setText(userTextEditInfo.nameOfUser)
            bioEditProfileNewUserText.setText(userTextEditInfo.bio)
        }
    }
    private fun setAvatar(userAvatarReference: String?) {

     val requestOptionsAvatar = RequestOptions()
         .placeholder(R.drawable.default_avatar)
         .error(R.drawable.default_avatar)

     Glide.with(binding.profileImageEditProfileNewUser.context)
         .applyDefaultRequestOptions(requestOptionsAvatar)
         .load(userAvatarReference)
         .circleCrop()
         .into(binding.profileImageEditProfileNewUser)
    }


    // TODO(CHANGE THIS FOR UNIQUE USERNAME)
    private fun valid(username:String,nameOfUser:String,bio:String):Boolean{
        return username.isNotEmpty() && nameOfUser.isNotEmpty()
    }
    override fun onStop() {
        super.onStop()
        viewModel.userTextEditInfo = UserTextEditInfo(
            binding.usernameEditProfileNewUserText.text.toString(),
            binding. nameEditProfileNewUserText.text.toString(),
            binding.bioEditProfileNewUserText.text.toString(),
            viewModel.imageRefLD.value
        )
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                binding.profileImageEditProfileNewUser.id -> {
                    navController.navigate(R.id.action_fragmentEditProfileNewUser_to_fragmentProfilePicNewUser)
                    Log.d(TAG, "onClick: navigating To Profile Pic New User")
                }
                binding.removeProfilePictureEditProfileNewUser.id -> {
                    viewModel.updateUserImageReference(null to null)
                }
            }
        }
    }

}