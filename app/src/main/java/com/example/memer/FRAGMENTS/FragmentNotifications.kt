package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.memer.databinding.FragmentUserActivityBinding
import kotlinx.android.synthetic.main.activity_main.*


class FragmentNotifications : Fragment() {

    private lateinit var binding: FragmentUserActivityBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentUserActivityBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE

        return binding.root
    }


}