package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterFragmentTaggedPosts
import com.example.memer.HELPERS.DataSource
import com.example.memer.databinding.FragmentTaggedPostsBinding


class FragmentTaggedPosts : Fragment(), AdapterFragmentTaggedPosts.ItemClickListener {

    private lateinit var binding: FragmentTaggedPostsBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: AdapterFragmentTaggedPosts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaggedPostsBinding.inflate(inflater,container,false)

        gridLayoutManager = GridLayoutManager(context,4)
        mAdapter = AdapterFragmentTaggedPosts(this, requireActivity())
        mAdapter.submitList(DataSource.createOnlyImageSet())
        val recyclerView =binding.recyclerViewFragmentTaggedPosts

        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context,"Clicked Tagged Posts at $position", Toast.LENGTH_SHORT).show()
    }


}