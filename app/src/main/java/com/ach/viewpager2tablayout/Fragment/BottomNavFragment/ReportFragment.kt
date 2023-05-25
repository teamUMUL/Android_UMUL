package com.ach.viewpager2tablayout.Fragment.BottomNavFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ach.viewpager2tablayout.Adapter.ReportPagerAdapter
import com.ach.viewpager2tablayout.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class ReportFragment : Fragment() {
    private lateinit var viewGroup: ViewGroup


    //레포트
    private val tabLists = listOf(
        "일간 레포트",
        "주간 레포트"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewGroup = inflater.inflate(R.layout.fragment_report, container, false) as ViewGroup
        val viewPager: ViewPager2 = viewGroup.findViewById<ViewPager2>(R.id.viewPager)
        val tabs: TabLayout = viewGroup.findViewById<TabLayout>(R.id.tabs)

        setInit()

        TabLayoutMediator(tabs, viewPager){
                tab, position ->
            tab.text = tabLists[position]
        }.attach()

        return viewGroup
    }

    private fun setInit(){

        val viewPageSetUp : ViewPager2 = viewGroup.findViewById(R.id.viewPager)
        val setUpPagerAdapter =
            activity.let{ ReportPagerAdapter(it!!) }

        viewPageSetUp.adapter = setUpPagerAdapter
        viewPageSetUp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPageSetUp.offscreenPageLimit = 2

    }
}


