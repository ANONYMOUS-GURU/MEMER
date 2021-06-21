package com.example.memer.FRAGMENTS

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.memer.R
import com.example.memer.databinding.FragmentOnBoardingScreen3Binding

class OnBoardingScreen3 : Fragment() {

    private lateinit var navController:NavController
    private lateinit var binding: FragmentOnBoardingScreen3Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingScreen3Binding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireParentFragment().findNavController()
        binding.btnOnBoarding3.setOnClickListener{
            onBoardingDone()
            navController.navigate(R.id.action_global_fragmentHomePage)
        }
    }

    private fun onBoardingDone(){
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean(getString(com.example.memer.R.string.on_boarding_done), true)
            apply()
        }
    }

}