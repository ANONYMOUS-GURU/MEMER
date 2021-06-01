package com.example.memer.ADAPTERS

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.memer.FRAGMENTS.*

class AdapterFragmentAddPost(fragment: Fragment): FragmentStateAdapter(fragment) {
    companion object{
        private const val TAG = "AdapterFragmentAddPost"
    }

    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> FragmentTemplates()
            1 -> FragmentGallery()
            else -> FragmentTemplates()
        }
    }
}