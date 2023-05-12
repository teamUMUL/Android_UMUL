package com.ach.viewpager2tablayout.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_1
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_2
import com.ach.viewpager2tablayout.Fragment.ReportFragment1
import com.ach.viewpager2tablayout.Fragment.ReportFragment2

class FragPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    val fragmentList = listOf<Fragment>(ReportFragment1(), ReportFragment2())

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
    }
}
