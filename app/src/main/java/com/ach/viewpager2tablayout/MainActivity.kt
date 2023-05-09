package com.ach.viewpager2tablayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.ach.viewpager2tablayout.databinding.ActivityMainBinding
import com.ach.viewpager2tablayout.databinding.FragmentMyBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val tabIcon = listOf(
        R.drawable.baseline_format_list_bulleted_24,
        R.drawable.baseline_map_24,
        R.drawable.baseline_info_24
    )

    private val tabLists = listOf(
        "일간 레포트",
        "주간 레포트"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.apply {
            adapter = MyPagerAdapter(context as FragmentActivity)
        }

        TabLayoutMediator(binding.tabs, binding.viewPager){
            tab, position ->
            tab.text = tabLists[position]
        }.attach()
    }
}