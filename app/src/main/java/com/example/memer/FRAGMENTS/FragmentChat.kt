package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.memer.databinding.FragmentChatBinding
import kotlinx.android.synthetic.main.activity_main.*

class FragmentChat : Fragment() {

    private lateinit var binding: FragmentChatBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentChatBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.GONE



        return binding.root
    }


}