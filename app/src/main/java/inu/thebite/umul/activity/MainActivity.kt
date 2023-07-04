package inu.thebite.umul.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.forEach
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityMainBinding
import inu.thebite.umul.fragment.bottomNavFragment.*
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val disabledButtonColor = Color.rgb(62, 97, 67) //버튼 비활성화 색 = 녹색
    private val enabledButtonColor = Color.rgb(0,199,255) //버튼 활성화 색 = Aqua_Blue


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)



        //하단바 ------------------------------------------
        val bottomNavigation = binding.bottomNavigationView
        val bottomNavigationMenu: Menu = bottomNavigation.menu
        val playBtn = binding.playButton
        setContentView(binding.root)

        //가운데 빈 버튼 비활성화
        bottomNavigationMenu.findItem(R.id.placeholder).isEnabled = false
        //꾹 누른 경우 뜨는 tooltip 끄기
        bottomNavigationMenu.forEach {
            TooltipCompat.setTooltipText(bottomNavigation.findViewById(it.itemId), null)
        }
        //기본세팅: 플레이버튼회색, 기본화면 띄우기
        playBtn.backgroundTintList = ColorStateList.valueOf(disabledButtonColor)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment())
            .commit()

        //플레이버튼 클릭 시 -> 화면전환, 하단바 선택 비활성화, 버튼 배경 색 변화
        playBtn.setOnClickListener {
/*            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)*/

            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, RecordReadyFragment()).commit()
            enableRecordButton(playBtn, bottomNavigationMenu)

        }
        //하단 바 클릭 시 -> 화면전환, 플레이버튼 회색, 하단 바 선택 활성화
        bottomNavigation.setOnItemSelectedListener {
            disableRecordButton(playBtn, bottomNavigationMenu)

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


    fun disableRecordButton(playBtn : FloatingActionButton, bottomNavigationMenu : Menu){
        bottomNavigationMenu.setGroupCheckable(0,true,true)
        playBtn.backgroundTintList = ColorStateList.valueOf(disabledButtonColor)

    }
    fun enableRecordButton(playBtn: FloatingActionButton, bottomNavigationMenu : Menu){
        bottomNavigationMenu.setGroupCheckable(0,false,true)
        playBtn.backgroundTintList = ColorStateList.valueOf(enabledButtonColor)

    }



    fun setRecordChecked(){
        val bottomNavigation = binding.bottomNavigationView
        val bottomNavigationMenu: Menu = bottomNavigation.menu
        val recordButton = binding.playButton
        recordButton.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.mainFrame, RecordReadyFragment())
                .commit()
            bottomNavigationMenu.setGroupCheckable(0,false,true)
            recordButton.backgroundTintList = ColorStateList.valueOf(enabledButtonColor)
        }
        bottomNavigationMenu.setGroupCheckable(0,false,true)
        recordButton.backgroundTintList = ColorStateList.valueOf(enabledButtonColor)

    }

    fun setGameActivityStart(){
        val intent = Intent(this, RecordActivity::class.java)
        startActivityForResult(intent, 2)
    }


    fun setBLE(){
        val pairIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        startActivityForResult(pairIntent, 0)
    }

    fun setBMIChecked(){
        itemSelectedListenerSetting(R.id.BMI)
    }

    fun setHomeChecked(){
        itemSelectedListenerSetting(R.id.home)
    }

    fun setReportChecked(){
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, ReportFragment())
            .commit()
        itemSelectedListenerSetting(R.id.report)
    }



    fun itemSelectedListenerSetting(itemId : Int){
        val bottomNavigation = binding.bottomNavigationView
        val bottomNavigationMenu: Menu = bottomNavigation.menu
        val playBtn = binding.playButton
        bottomNavigation.setOnItemSelectedListener {
            disableRecordButton(playBtn, bottomNavigationMenu)
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

        bottomNavigation.selectedItemId = itemId

    }




}