package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memer.ADAPTERS.AdapterFragmentSearch
import com.example.memer.HELPERS.DataSource
import com.example.memer.R
import com.example.memer.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.activity_main.*

class FragmentSearch : Fragment(), AdapterFragmentSearch.ItemClickListener,View.OnClickListener {

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var searchPageAdapter: AdapterFragmentSearch
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentSearchBinding.inflate(inflater,container,false)
        requireActivity().bottomNavigationView.visibility = View.VISIBLE


        val recyclerView = binding.recyclerViewFragmentSearch
        gridLayoutManager = GridLayoutManager(context,4)
        searchPageAdapter = AdapterFragmentSearch(this, requireActivity())
        searchPageAdapter.submitList(DataSource.createOnlyImageSet())

        recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = searchPageAdapter
        }

        binding.searchEditText.setOnClickListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.fragmentSearch))
        binding.searchPageToolbar.setupWithNavController(navController,appBarConfiguration)
    }

    override fun onImageItemClick(position: Int) {
        Toast.makeText(activity, "Clicked on image at position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onVideoItemClick(position: Int) {
        Toast.makeText(activity, "Clicked on video at position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id){
                R.id.searchEditText -> navController.navigate(R.id.action_fragmentSearch_to_fragmentSearchResult)
            }
        }
    }

}