package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.memer.R
import com.example.memer.databinding.ActivityMainBinding
import com.example.memer.databinding.FragmentPostContainerBinding
import kotlinx.android.synthetic.main.activity_main.*


class FragmentPostContainer : Fragment() {

    private lateinit var binding: FragmentPostContainerBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().bottomNavigationView.visibility = View.GONE
        binding = FragmentPostContainerBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navHostFragment = childFragmentManager.findFragmentById(R.id.make_post_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

}