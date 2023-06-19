package com.ach.viewpager2tablayout.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ach.viewpager2tablayout.fragment.ReportFragment1
import com.ach.viewpager2tablayout.fragment.ReportFragment2

class ReportPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    val fragmentList = listOf<Fragment>(ReportFragment1(), ReportFragment2())

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
    }
}
