package com.example.memer.ADAPTERS

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.memer.FRAGMENTS.FragmentTaggedPosts
import com.example.memer.FRAGMENTS.FragmentYourPosts


class AdapterFragmentProfile(fragment:Fragment): FragmentStateAdapter(fragment) {
    companion object{
        private const val TAG = "AdapterFragmentProfile"
    }

    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> FragmentYourPosts()
            1 -> FragmentTaggedPosts()
            else -> FragmentYourPosts()
        }
    }
}