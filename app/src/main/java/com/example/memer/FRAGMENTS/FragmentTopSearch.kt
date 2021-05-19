package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memer.ADAPTERS.AdapterFragmentTopSearch
import com.example.memer.HELPERS.DataSource
import com.example.memer.databinding.FragmentTopSearchBinding

class FragmentTopSearch : Fragment(), AdapterFragmentTopSearch.ItemClickListener {

    private lateinit var binding: FragmentTopSearchBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: AdapterFragmentTopSearch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopSearchBinding.inflate(inflater,container,false)

        linearLayoutManager = LinearLayoutManager(context)
        mAdapter = AdapterFragmentTopSearch(this,requireContext())
        mAdapter.submitList(DataSource.createOnlyImageSet())
        val recyclerView =binding.recyclerViewTopSearch

        recyclerView.apply {
            layoutManager = linearLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context,"Clicked Tagged Posts at $position", Toast.LENGTH_SHORT).show()
    }

}