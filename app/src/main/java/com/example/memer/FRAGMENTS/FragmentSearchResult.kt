package com.example.memer.FRAGMENTS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.memer.ADAPTERS.AdapterFragmentSearchResult
import com.example.memer.databinding.FragmentSearchResultBinding
import com.google.android.material.tabs.TabLayoutMediator


class FragmentSearchResult : Fragment() {

    private lateinit var binding:FragmentSearchResultBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultBinding.inflate(inflater,container,false)

        val viewPager: ViewPager2 =binding.viewPagerSearchResult
        viewPager.adapter= AdapterFragmentSearchResult(this)

        val tabLayout = binding.tabLayoutSearchResult
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position){
                0 -> {
                    tab.text = "Top"
                    binding.searchViewSearchResult.queryHint = "Search"
                }
                1 -> {
                    tab.text = "Accounts"
                    binding.searchViewSearchResult.queryHint = "Search Accounts"
                }
                2 -> {
                    tab.text = "Tags"
                    binding.searchViewSearchResult.queryHint = "Search HashTags"
                }
                else -> {
                    tab.text = "Top"
                    binding.searchViewSearchResult.queryHint = "Search"
                }
            }
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        binding.searchResultToolbar.setupWithNavController(navController)
    }

}