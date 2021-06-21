package com.example.memer.ADAPTERS

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.memer.FRAGMENTS.*

class AdapterOnBoarding(fragment: Fragment): FragmentStateAdapter(fragment) {
    companion object {
        private const val TAG = "AdapterOnBoarding"
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingScreen1()
            1 -> OnBoardingScreen2()
            2 -> OnBoardingScreen3()
            else -> OnBoardingScreen1()
        }
    }
}