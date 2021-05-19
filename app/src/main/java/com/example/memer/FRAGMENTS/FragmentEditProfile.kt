package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.memer.MODELS.UserEditableInfo
import com.example.memer.MODELS.UserProfileInfo
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.FragmentEditProfileBinding
import kotlinx.android.synthetic.main.activity_main.*


class FragmentEditProfile : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var navController: NavController
    private val viewModel: ViewModelUserInfo by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        requireActivity().bottomNavigationView.visibility = View.GONE

        getUserData(viewModel.getUserEditInfo().value)

        viewModel.getUserEditInfo().observe(viewLifecycleOwner, Observer {
            getUserData(viewModel.getUserEditInfo().value)
        })
        viewModel.getUserImageReference().observe(viewLifecycleOwner, Observer {
            getImageReference(viewModel.getUserImageReference().value)
        })

        binding.profileImageEditProfile.setOnClickListener(this)
        binding.removeProfilePictureEditProfile.setOnClickListener(this)
        return binding.root
    }

    private fun getUserData(user: UserEditableInfo?) {
        if (user != null) {
            binding.apply {
                usernameEditProfileText.setText(user.username)
                nameEditProfileText.setText(user.nameOfUser)
                bioEditProfileText.setText(user.bio)
            }
        }

    }

    private fun getImageReference(imageReference: String?) {
        val requestOptionsAvatar = RequestOptions()
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)

        Glide.with(binding.profileImageEditProfile.context)
            .applyDefaultRequestOptions(requestOptionsAvatar)
            .load(imageReference)
            .circleCrop()
            .into(binding.profileImageEditProfile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.editProfilePageToolbar.setupWithNavController(navController)
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

        binding.editProfilePageToolbar.setNavigationIcon(R.drawable.close_icon)

    }

    private fun setImageResource(value: String?) {
        viewModel.updateUserImageReference(value)
    }

    private fun saveChanges() {
        val bio = binding.bioEditProfileText.text.toString()
        val username = binding.usernameEditProfileText.text.toString()
        val name = binding.nameEditProfileText.text.toString()
        val user: UserEditableInfo = UserEditableInfo(name, username, bio)
        viewModel.updateUserEditInfo(user)
        // TODO(save changes in cloud)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.profileImageEditProfile -> {    // TODO("upload and change image reference")
                    Toast.makeText(context, "Changing Image", Toast.LENGTH_SHORT).show()
                    setImageResource("https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/javascript_expert_wes_bos.png")
                }
                R.id.removeProfilePictureEditProfile -> {
                    setImageResource(null)
                    Toast.makeText(context, "Changing Image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}