package com.example.memer.ADAPTERS

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.memer.FRAGMENTS.FragmentYourPosts


class AdapterFragmentProfile(fragment:Fragment): FragmentStateAdapter(fragment) {
    companion object{
        private const val TAG = "AdapterFragmentProfile"
    }

    override fun getItemCount(): Int {
        return 1;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> FragmentYourPosts()
            else -> FragmentYourPosts()
        }
    }
}