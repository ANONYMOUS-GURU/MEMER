package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.memer.R
import com.example.memer.databinding.FragmentOnBoardingScreen1Binding


class OnBoardingScreen1 : Fragment() {


    private lateinit var binding:FragmentOnBoardingScreen1Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingScreen1Binding.inflate(inflater,container,false)

        binding.btnOnBoarding1.setOnClickListener{
            requireActivity().findViewById<ViewPager2>(R.id.viewPagerOnBoarding).currentItem = 1
        }

        return binding.root
    }


}