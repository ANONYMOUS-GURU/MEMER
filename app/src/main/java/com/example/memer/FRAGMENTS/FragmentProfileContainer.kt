package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.memer.R
import com.example.memer.databinding.FragmentProfileContainerBinding


class FragmentProfileContainer : Fragment() {

    private lateinit var binding:FragmentProfileContainerBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileContainerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navHostFragment = childFragmentManager.findFragmentById(R.id.navigation_profile) as NavHostFragment
        navController = navHostFragment.navController
    }



}