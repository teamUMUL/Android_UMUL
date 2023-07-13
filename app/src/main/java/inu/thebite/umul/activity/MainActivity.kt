package inu.thebite.umul.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.forEach
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityMainBinding
import inu.thebite.umul.fragment.bottomNavFragment.*
import inu.thebite.umul.service.BluetoothService
import java.io.IOException
import java.time.LocalDate
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val disabledButtonColor = Color.rgb(62, 97, 67) //버튼 비활성화 색 = 녹색
    private val enabledButtonColor = Color.rgb(0,199,255) //버튼 활성화 색 = Aqua_Blue
    private lateinit var mBluetoothAdapter : BluetoothAdapter
    private lateinit var bluetoothService: BluetoothService
    private var bound: Boolean = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private lateinit var mListPairedDevices: List<String>
    private lateinit var mBluetoothHandler: Handler
    private lateinit var mThreadConnectedBluetooth: RecordActivity.ConnectedBluetoothThread
    private lateinit var mBluetoothDevice: BluetoothDevice
    private lateinit var mBluetoothSocket: BluetoothSocket
    val BT_REQUEST_ENABLE = 1
    val BT_MESSAGE_READ = 2
    val BT_CONNECTING_STATUS = 3
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        bluetoothPermissionChecker()
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
                    bundle.putString("date", LocalDate.now().toString())
                    val reportFragment = ReportFragment()
                    reportFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, reportFragment).commit()
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
        //bluetoothOn()
        stopService(intent)
        listPairedDevices()
/*        val pairIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        startActivityForResult(pairIntent, 0)*/
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
    @SuppressLint("MissingPermission")
    fun bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show()
        } else {
            if (mBluetoothAdapter.isEnabled) {
                Toast.makeText(applicationContext, "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(applicationContext, "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG)
                    .show()
                val intentBluetoothEnable = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE)
            }
        }
    }

    // 블루투스 비활성화 메서드


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

    @SuppressLint("MissingPermission")
    fun connectSelectedDevice(selectedDeviceName: String) {
        for (tempDevice in mPairedDevices) {
            if (selectedDeviceName == tempDevice.name) {
                mBluetoothDevice = tempDevice

                break
            }
        }

    }



}