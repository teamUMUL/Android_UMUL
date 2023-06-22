package com.umul.umul_develop_ach.activity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.umul.umul_develop_ach.R
import com.umul.umul_develop_ach.databinding.ActivityMainBinding
import com.umul.umul_develop_ach.fragment.bottomNavFragment.*

import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)









        //하단바 ------------------------------------------
        val bottomNavigation = binding.bottomNavigationView
        val bottomNavigationMenu: Menu = bottomNavigation.menu
        val playBtn = binding.playButton
        val disabledButtonColor = Color.rgb(77, 79, 82) //버튼 비활성화 색 = Gray
        val enabledButtonColor = Color.rgb(0,199,255) //버튼 활성화 색 = Aqua_Blue
        setContentView(binding.root)

        //기본세팅: 플레이버튼회색, 기본화면 띄우기
        playBtn.backgroundTintList = ColorStateList.valueOf(disabledButtonColor)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment())
            .commit()

        //플레이버튼 클릭 시 -> 화면전환, 하단바 선택 비활성화, 버튼 배경 색 변화
        playBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.mainFrame, RecordFragment())
                .commit()
            bottomNavigationMenu.setGroupCheckable(0,false,true)
            playBtn.backgroundTintList = ColorStateList.valueOf(enabledButtonColor)


        }

        //하단 바 클릭 시 -> 화면전환, 플레이버튼 회색, 하단 바 선택 활성화
        bottomNavigation.setOnItemSelectedListener {
            bottomNavigationMenu.setGroupCheckable(0,true,true)
            playBtn.backgroundTintList = ColorStateList.valueOf(disabledButtonColor)


            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, HomeFragment()).commit()
                }
                R.id.report -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, ReportFragment()).commit()
                }
                R.id.BMI -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, BMIFragment()).commit()
                }
                R.id.mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, MyPageFragment()).commit()
                }
            }
            true
        }

    }
}