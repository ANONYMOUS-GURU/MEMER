package com.example.memer.FRAGMENTS

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.example.memer.ADAPTERS.AdapterFragmentAddPost
import com.example.memer.ADAPTERS.AdapterOnBoarding
import com.example.memer.R
import com.example.memer.databinding.FragmentOnBoardingBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*


class FragmentOnBoarding : Fragment(){

    private lateinit var navController:NavController
    private lateinit var binding:FragmentOnBoardingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.GONE
        initView()
        return binding.root
    }

    private fun initView(){
        val viewPager: ViewPager2 = binding.viewPagerOnBoarding
        viewPager.adapter = AdapterOnBoarding(this)
        TabLayoutMediator(binding.tabDots, viewPager) { tab, position ->
        }.attach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }




}