package com.example.memer.ADAPTERS

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.memer.FRAGMENTS.FragmentAccountsSearch
import com.example.memer.FRAGMENTS.FragmentTagsSearch
import com.example.memer.FRAGMENTS.FragmentTopSearch

class AdapterFragmentSearchResult(fragment: Fragment): FragmentStateAdapter(fragment) {
    companion object{
        private const val TAG = "AdapterFragmentSearch"
    }

    override fun getItemCount(): Int {
        return 3;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> FragmentTopSearch()
            1 -> FragmentAccountsSearch()
            2 -> FragmentTagsSearch()
            else -> FragmentTopSearch()
        }
    }
}