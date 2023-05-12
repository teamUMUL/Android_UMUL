package com.ach.viewpager2tablayout.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ach.viewpager2tablayout.Fragment.HomeFragment
import com.ach.viewpager2tablayout.Fragment.MyPageFragment
import com.ach.viewpager2tablayout.Fragment.ReportFragment
import com.ach.viewpager2tablayout.R
import com.ach.viewpager2tablayout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val bottomNavigation = binding.bottomNavigation
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.layout_nav_bottom, HomeFragment())
            .commit()

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_nav_bottom, HomeFragment()).commit()
                }
                R.id.report -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_nav_bottom, ReportFragment()).commit()
                }
                R.id.mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_nav_bottom, MyPageFragment()).commit()
                }
            }
            true
        }
    }
}