package com.ach.viewpager2tablayout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val NUM_PAGES = 2

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {MyFragment.newInstance("일간레포트", "")}
            else -> {MyFragment.newInstance("주간레포트", "")}
        }
    }
}