package com.example.memer.FRAGMENTS

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memer.ADAPTERS.AdapterComments
import com.example.memer.ADAPTERS.HomePageAdapter
import com.example.memer.R
import com.example.memer.databinding.FragmentCommentsBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.comment_single_view.view.*


class FragmentComments : Fragment() {

    private lateinit var binding: FragmentCommentsBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: AdapterComments
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.GONE


        return binding.root
    }

    fun initRecyclerView(){
        linearLayoutManager = LinearLayoutManager(context)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)

    }

}