package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memer.ADAPTERS.AdapterFragmentAccount
import com.example.memer.HELPERS.DataSource
import com.example.memer.databinding.FragmentAccountsSearchBinding

class FragmentAccountsSearch : Fragment(), AdapterFragmentAccount.ItemClickListener {

    private lateinit var binding: FragmentAccountsSearchBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: AdapterFragmentAccount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountsSearchBinding.inflate(inflater,container,false)

        linearLayoutManager = LinearLayoutManager(context)
        mAdapter = AdapterFragmentAccount(this,requireContext())
        mAdapter.submitList(DataSource.createOnlyImageSet())
        val recyclerView =binding.recyclerViewAccountsSearch

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