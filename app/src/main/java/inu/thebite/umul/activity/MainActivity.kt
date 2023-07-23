package inu.thebite.umul.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.forEach
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inu.thebite.umul.BluetoothConnectionCallback
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityMainBinding
import inu.thebite.umul.fragment.bottomNavFragment.*
import inu.thebite.umul.service.BluetoothService
import java.time.LocalDate
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), BluetoothConnectionCallback {
    private lateinit var binding : ActivityMainBinding
    private var homeFragment: HomeFragment? = null
    private lateinit var memberNumber : String
    private lateinit var childName: String

    private val disabledButtonColor = Color.rgb(62, 97, 67) //버튼 비활성화 색 = 녹색
    private val enabledButtonColor = Color.rgb(0,199,255) //버튼 활성화 색 = Aqua_Blue
    private lateinit var mBluetoothAdapter : BluetoothAdapter   //블루투스 어댑터
    private lateinit var bluetoothService: BluetoothService     //블루투스 서비스
    private var bound: Boolean = false                          //Service 연결 유무
    private var isConnected : Boolean = false                   //블루투스 연결 유무
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            bound = true

            //연결 될 때 isBluetoothConnected 함수를, 연결 끊을 때 isBluetoothDisconnected 함수를 사용하기 위해서 인터페이스 사용
            bluetoothService.setBluetoothConnectionCallback(this@MainActivity)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }
    //Service에서 UI접근이 힘들기 때문에 연결된 기기 리스트를 보여주는 것은 MainActivity에서 실행
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private lateinit var mListPairedDevices: List<String>

    val BT_REQUEST_ENABLE = 1

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        memberNumber = intent.getStringExtra("memberNumber").toString()
        childName = intent.getStringExtra("childName").toString()
        Log.d("MainActivity memberNumber = ", memberNumber)
        //블루투스 권한 확인
        bluetoothPermissionChecker()

        //intent에서 블루투스 연결 유무에 대한 값을 sharedPreference에 저장 (RecordActivity -> MainActivity로 이동 시 intent에 블루투스 연결 유무 값 전달)
        val pref: SharedPreferences = getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        val isConnectedFromRecordActivity = intent.getBooleanExtra("inBluetoothConnected", false)
        editor.putBoolean("isBluetoothConnected",isConnectedFromRecordActivity)
        editor.apply()
        isConnected = pref.getBoolean("isBluetoothConnected", false)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        startService(intent)

        //하단바 ------------------------------------------
        val bottomNavigation = binding.bottomNavigationView
        val bottomNavigationMenu: Menu = bottomNavigation.menu
        val playBtn = binding.playButton
        val bundle = Bundle()
        setContentView(binding.root)
        //하단 바 세번째(가운데) 버튼 비활성화(플레이 버튼 아님)
        bottomNavigationMenu.findItem(R.id.placeholder).isEnabled = false
        //꾹 누른 경우 뜨는 tooltip 끄기
        bottomNavigationMenu.forEach {
            TooltipCompat.setTooltipText(bottomNavigation.findViewById(it.itemId), null)
        }
        //기본세팅: 플레이 버튼 회색, 기본화면 띄우기
        bundle.putString("memberNumber", memberNumber)
        bundle.putString("childName", childName)
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
        playBtn.backgroundTintList = ColorStateList.valueOf(disabledButtonColor)
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame, homeFragment)
            .commit()

        //플레이버튼 클릭 시 -> 화면전환, 하단바 선택 비활성화, 버튼 배경 색 변화
        playBtn.setOnClickListener {
            bundle.putString("memberNumber", memberNumber)
            bundle.putString("childName", childName)
            val recordReadyFragment = RecordReadyFragment()
            recordReadyFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, recordReadyFragment).commit()
            enableRecordButton(playBtn, bottomNavigationMenu)

        }
        //하단 바 클릭 시 -> 화면전환, 플레이버튼 회색, 하단 바 선택 활성화
        bottomNavigation.setOnItemSelectedListener {
            disableRecordButton(playBtn, bottomNavigationMenu)

            when (it.itemId) {
                R.id.home -> {
                    bundle.putString("memberNumber", memberNumber)
                    bundle.putString("childName", childName)
                    val homeFragment = HomeFragment()
                    homeFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, homeFragment).commit()
                }
                R.id.report -> {
                    bundle.putString("date", LocalDate.now().toString())
                    bundle.putString("childName", childName)
                    val reportFragment = ReportFragment()
                    reportFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, reportFragment).commit()
                }
                R.id.BMI -> {
                    bundle.putString("memberNumber", memberNumber)
                    bundle.putString("childName", childName)
                    val bmiFragment = BMIFragment()
                    bmiFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, bmiFragment).commit()
                }
                R.id.mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, MyPageFragment()).commit()
                }
            }
            true
        }



    }

    //가운데 PlayButton 활성화 색으로 변경
    fun disableRecordButton(playBtn : FloatingActionButton, bottomNavigationMenu : Menu){
        bottomNavigationMenu.setGroupCheckable(0,true,true)
        playBtn.backgroundTintList = ColorStateList.valueOf(disabledButtonColor)

    }
    //가운데 PlayButton 비활성화 색으로 변경
    fun enableRecordButton(playBtn: FloatingActionButton, bottomNavigationMenu : Menu){
        bottomNavigationMenu.setGroupCheckable(0,false,true)
        playBtn.backgroundTintList = ColorStateList.valueOf(enabledButtonColor)

    }

    //RecordReadyFragment에서 게임 실행 누를 시 RecordActivity 실행
    fun startRecordActivity(){
        val intent = Intent(this, RecordActivity::class.java)
        intent.putExtra("memberNumber", memberNumber)
        intent.putExtra("childName", childName)
        startActivityForResult(intent, 2)
    }

    //자녀 추가 버튼 클릭 시 자녀 추가 Activity 실행
    fun startInsertInfoActivity(){
        val intent = Intent(this, InsertChildInformationActivity::class.java)
        intent.putExtra("memberNumber", memberNumber)
        startActivity(intent)
    }

    //하단 바에서 BMI선택
    fun setBMIChecked(){
        itemSelectedListenerSetting(R.id.BMI)
    }

    //하단 바에서 Home선택
    fun setHomeChecked(){
        itemSelectedListenerSetting(R.id.home)
    }

    //플레이 버튼 선택
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

    //하단 바 클릭에 따른 활동
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



    //블루투스 권환 확인(Android 12이상인 경우 BLUETOOTH외에도 다른 권한 설정이 필요)
    fun bluetoothPermissionChecker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                BT_REQUEST_ENABLE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.BLUETOOTH
                ),
                BT_REQUEST_ENABLE
            )
        }
    }

    //블루투스 켜져있는 지 확인, 블루투스 연결X -> 페어링 리스트 보여주기, 블루투스 연결 O -> 연결 끊기
    fun setBLE(){
        bluetoothOn()
        if(isConnected){
            bluetoothService.disconnect()
        }else{
            listPairedDevices()
        }
    }

    //블루투스 켜기
    @SuppressLint("MissingPermission")
    fun bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show()
        } else {
            if (mBluetoothAdapter.isEnabled) {

            } else {
                Toast.makeText(applicationContext, "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG)
                    .show()
                val intentBluetoothEnable = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE)
            }
        }
    }

    // 블루투스 연결 하라는 알림에서 어떤 버튼을 클릭하느냐에 따른 활동
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BT_REQUEST_ENABLE) {
            if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                Toast.makeText(this, "블루투스 활성화", Toast.LENGTH_LONG).show()
            } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                Toast.makeText(this, "취소", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Service에서 UI건드는 것은 안되기에 list호출은 MainActivity 상에서 진행
    @SuppressLint("MissingPermission")
    fun listPairedDevices() {
        if (mBluetoothAdapter.isEnabled) {
            mPairedDevices = mBluetoothAdapter.bondedDevices
            if ((mPairedDevices as MutableSet<BluetoothDevice>?)!!.size > 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("장치 선택")
                mListPairedDevices = ArrayList()
                for (device in (mPairedDevices as MutableSet<BluetoothDevice>?)!!) {
                    (mListPairedDevices as ArrayList<String>).add(device.name)
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                val items = (mListPairedDevices as ArrayList<String>).toTypedArray<CharSequence>()
                (mListPairedDevices as ArrayList<String>).toTypedArray<CharSequence>()
                builder.setItems(
                    items
                ) { dialog: DialogInterface?, item: Int ->
                    val intent = Intent(this, BluetoothService::class.java)
                    startService(intent)
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    //선택 이후로는 Service에서 진행
                    bluetoothService.connectSelectedDevice(items[item].toString())
                }
                val alert = builder.create()
                alert.show()
            } else {
                Toast.makeText(this, "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //연결 될 때 sharedPreference 사용해서 연결 유무 저장 -> HomeFragment에서 사용하기 위해서
    override fun connecting() {
        //BluetoothService에서 연결될 때 사용
        homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.mainFrame, homeFragment!!, "MyFragment")
            .commit()
        isConnected = true

        val pref: SharedPreferences = getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("isBluetoothConnected",true)
        editor.apply()
    }
    override fun disconnecting() {
        //BluetoothService에서 연결 끊을 때 사용
        val bundle = Bundle()
        homeFragment = HomeFragment()
        bundle.putString("memberNumber", memberNumber)
        homeFragment!!.arguments
        supportFragmentManager.beginTransaction()
            .add(R.id.mainFrame, homeFragment!!, "MyFragment")
            .commit()
        isConnected = false
        val pref: SharedPreferences = getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("isBluetoothConnected",false)
        editor.apply()
    }
}