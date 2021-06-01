package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterGallery
import com.example.memer.ADAPTERS.AdapterTemplates
import com.example.memer.R
import com.example.memer.databinding.FragmentTemplatesBinding


class FragmentTemplates : Fragment() , AdapterTemplates.ItemClickListener {

    private lateinit var binding:FragmentTemplatesBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterTemplates

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTemplatesBinding.inflate(inflater,container,false)
        initRecyclerView()

        return binding.root
    }
    private fun initRecyclerView(){
        gridLayoutManager = GridLayoutManager(context,4)
        mAdapter = AdapterTemplates(this, requireActivity())
        mAdapter.submitList(ArrayList())
        val recyclerView =binding.recyclerViewTemplates

        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context,"Clicked Your Posts at $position", Toast.LENGTH_SHORT).show()
    }

}