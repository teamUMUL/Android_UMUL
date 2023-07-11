package inu.thebite.umul.activity

import inu.thebite.umul.service.BluetoothService.Companion.ACTION_DATA_RECEIVED
import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityRecordBinding
import inu.thebite.umul.dialog.GameEndDialog
import inu.thebite.umul.service.BluetoothService
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.random.Random


@Suppress("DEPRECATION")
class RecordActivity : AppCompatActivity(), View.OnClickListener {

    //블루투스 통신-------------------------------------

    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private lateinit var mListPairedDevices: List<String>

    private lateinit var mBluetoothHandler: Handler
    private lateinit var mThreadConnectedBluetooth: BluetoothService.ConnectedBluetoothThread
    private lateinit var mBluetoothDevice: BluetoothDevice
    private lateinit var mBluetoothSocket: BluetoothSocket
    private lateinit var bluetoothReceiver : BroadcastReceiver
    private lateinit var bluetoothService: BluetoothService
    private var bound: Boolean = false
    val BT_REQUEST_ENABLE = 1
    val BT_MESSAGE_READ = 2
    val BT_CONNECTING_STATUS = 3
    val BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    //---------------------------------------------------
    private lateinit var binding: ActivityRecordBinding
    private lateinit var gameStartButton: ImageButton
    private lateinit var getChewButton: ImageButton
    private lateinit var getCarrotButton: ImageButton
    private lateinit var gameEndButton: ImageButton
    private lateinit var backPressButton: ImageButton

    private lateinit var characters: ImageView
    private lateinit var carrotBox: ImageView
    private lateinit var overlayView: ConstraintLayout
    private lateinit var ani: AnimationDrawable
    private lateinit var handler: Handler
    private var seconds = 0
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isTimerRunning: Boolean = false
    private var chewCount = 0
    private var totalCnt = 0
    private var successCount = 0
    private var avgABiteCnt = 0
    private var spoonCount = 0
    private var liveChewCount = MutableLiveData(0)

    private var isStart : Boolean = false


    companion object {
        const val ACTION_DATA_RECEIVED = "com.example.bluetooth.DATA_RECEIVED"
        const val EXTRA_DATA = "data"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        binding =
            DataBindingUtil.setContentView<ActivityRecordBinding>(this, R.layout.activity_record)
        binding.recordActivity = this


        bluetoothReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == ACTION_DATA_RECEIVED){
                    if(isStart) {
                        characters.layoutParams.width = 1200
                        characters.requestLayout()
                        chewCount++
                        totalCnt++
                        animateCharacter()
                    }
                }
            }
        }
        val filter = IntentFilter(ACTION_DATA_RECEIVED)
        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        //상단바랑 하단바 숨기기 -> 전체화면
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsCompat.hide(WindowInsetsCompat.Type.navigationBars())
        bluetoothPermissionChecker()

        handler = Handler()
        createClouds()

        overlayView = binding.overlayView
        gameStartButton = binding.gameStartButton

        gameStartButton.bringToFront()
        gameStartButton.setOnClickListener {
            registerReceiver(bluetoothReceiver, filter)

            gameStartButton.isVisible = false
            backPressButton.isVisible = false
            getChewButton.isVisible = true
            characters.isVisible = true
            carrotBox.isVisible = true
            gameEndButton.isVisible = true
            getCarrotButton.isVisible = true
            startTimer()
            isStart = true
        }

        getChewButton = binding.getChew
        characters = binding.characters
        carrotBox = binding.carrotBox
        gameEndButton = binding.gameEndButton
        getCarrotButton = binding.getCarrot
        backPressButton = binding.gameBackPress
        backPressButton.isVisible = true
        getChewButton.isVisible = false
        characters.isVisible = false
        carrotBox.isVisible = false
        gameEndButton.isVisible = false
        getCarrotButton.isVisible = false
        ani = binding.characters.drawable as AnimationDrawable

        ani.isOneShot = true


        // 해당 장치가 블루투스 기능을 지원하는지 알아오는 메서드
  /*      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mBluetoothHandler = object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                if (msg.what == BT_MESSAGE_READ) {  //what =
                    var readMessage: String? = null
                    try {
                        readMessage = String((msg.obj as ByteArray), StandardCharsets.UTF_8)
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    Log.d("handleMessage", readMessage!!)
                    if(readMessage != "Reset"){
                        if(isStart) {
                            characters.layoutParams.width = 1200
                            characters.requestLayout()
                            chewCount++
                            totalCnt++
                            animateCharacter()
                        }
                    }
                }
            }
        }*/
    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.get_chew -> {

            }

            R.id.get_carrot -> {
                characters.layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                characters.requestLayout()
                spoonCount++
                if (chewCount >= 30) {
                    successCount++
                    characters.setImageResource(R.drawable.success_get_carrot)
                    ani = binding.characters.drawable as AnimationDrawable
                    ani.start()
                } else {
                    characters.setImageResource(R.drawable.fail_get_carrot)
                    ani = binding.characters.drawable as AnimationDrawable
                    ani.start()
                }

                chewCount = 0
                setCarrotCarrier()
            }

            R.id.game_end_button -> {
                showEndDialog()
                try {
                    avgABiteCnt = totalCnt / spoonCount


                    setCarrotCarrier()
                } catch (_: Exception) {
                }
                backPressButton.isVisible = true
                gameStartButton.isVisible = true
                getChewButton.isVisible = false
                characters.isVisible = false
                carrotBox.isVisible = false
                gameEndButton.isVisible = false
                getCarrotButton.isVisible = false
                stopTimer()
                Toast.makeText(this, seconds.toString()+"초, 성공횟수 :"+ successCount.toString() + "수저횟수 :"+spoonCount.toString() + "평균저작횟수 : "+avgABiteCnt.toString()+"총 저작횟수 : "+totalCnt.toString(), Toast.LENGTH_LONG).show()
                successCount = 0
                spoonCount = 0
                chewCount = 0
            }

            R.id.game_back_press -> {
                onBackPressed()
            }
            R.id.ble_connect_button -> {
      /*          bluetoothOn()
                listPairedDevices()*/
            }


        }
    }


    private fun startTimer() {
        if (!isTimerRunning) {
            startTime = SystemClock.elapsedRealtime()
            handler.postDelayed(timerRunnable, 0)
            isTimerRunning = true
        }
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable)
            isTimerRunning = false
            seconds = (elapsedTime / 1000).toInt()
        }
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            elapsedTime = currentTime - startTime


            handler.postDelayed(this, 1000) // Update every second
        }
    }

    fun showEndDialog() {
        val dialog = GameEndDialog()
        val args = Bundle()
        args.putString("successNum", successCount.toString())
        dialog.arguments = args
        dialog.show(supportFragmentManager, "click_game_end")
    }

    fun setMainActivityStart() {
        //데이터 안받기
        unregisterReceiver(bluetoothReceiver)

        //RecordActivity -> MainActivity로 이동할 때 activity 를 초기화하기 때문에 sharedPreference도 같이 초기화 됨
        //따라서 초기화하기 전에 intent에 연결 유무 boolean값 넣어주고 MainActivity로 이동
        val pref: SharedPreferences = getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        val isBluetoothConnected = pref.getBoolean("isBluetoothConnected", false)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("inBluetoothConnected", isBluetoothConnected)
        //activity 쌓이지 않도록 activity 초기화
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun animateCharacter() {
        when (chewCount) {
            in 0..4 -> {
                characters.setImageResource(R.drawable.pull_carrot_1)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }

            in 5..8 -> {
                characters.setImageResource(R.drawable.pull_carrot_2)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }

            in 9..12 -> {
                characters.setImageResource(R.drawable.pull_carrot_3)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }

            in 13..16 -> {
                characters.setImageResource(R.drawable.pull_carrot_4)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }

            in 17..20 -> {
                characters.setImageResource(R.drawable.pull_carrot_5)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }

            in 21..25 -> {
                characters.setImageResource(R.drawable.pull_carrot_6)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }

            in 26..30 -> {
                characters.setImageResource(R.drawable.pull_carrot_7)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }

            else -> {
                characters.setImageResource(R.drawable.pull_carrot_8)
                ani = binding.characters.drawable as AnimationDrawable
                ani.start()
            }
        }
    }


    private fun createClouds() {
        for (x in 0 until 20) {
            val cloud = createCloud()
            val set = ConstraintSet()
            val parentLayout = binding.cloudContainer
            cloud.id = View.generateViewId()
            parentLayout.addView(cloud, 0)
            set.clone(parentLayout)
            set.connect(cloud.id, ConstraintSet.TOP, parentLayout.id, ConstraintSet.TOP, 0)
            set.connect(cloud.id, ConstraintSet.END, parentLayout.id, ConstraintSet.END, 0)
            set.connect(cloud.id, ConstraintSet.BOTTOM, parentLayout.id, ConstraintSet.BOTTOM, 0)

            set.setVerticalBias(cloud.id, Random.nextDouble(0.0, 1.0).toFloat())
            set.applyTo(parentLayout)
            animateCloud(cloud)
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createCloud(): ImageView {
        return ImageView(baseContext).apply {
            setImageDrawable(getDrawable(R.drawable.ic_cloud))
            layoutParams = LinearLayout.LayoutParams(
                Random.nextInt(100, 250),
                Random.nextInt(100, 250)
            )
            alpha = Random.nextDouble(0.4, 0.8).toFloat()
            translationX = Random.nextDouble(-500.0, 300.0).toFloat()
        }
    }

    private fun animateCloud(cloud: ImageView) {
        ObjectAnimator.ofFloat(cloud, "translationX", -2400f).apply {
            duration = Random.nextLong(18000, 23000)
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private fun setCarrotCarrier() {
        when (successCount) {
            0 -> {
                carrotBox.setImageResource(R.drawable.carrot_0)
            }

            1 -> {
                carrotBox.setImageResource(R.drawable.carrot_1)
            }

            2 -> {
                carrotBox.setImageResource(R.drawable.carrot_2)
            }

            3 -> {
                carrotBox.setImageResource(R.drawable.carrot_3)
            }

            4 -> {
                carrotBox.setImageResource(R.drawable.carrot_4)
            }

            5 -> {
                carrotBox.setImageResource(R.drawable.carrot_5)
            }

            6 -> {
                carrotBox.setImageResource(R.drawable.carrot_6)
            }

            7 -> {
                carrotBox.setImageResource(R.drawable.carrot_7)
            }

            8 -> {
                carrotBox.setImageResource(R.drawable.carrot_8)
            }

            9 -> {
                carrotBox.setImageResource(R.drawable.carrot_9)
            }

            10 -> {
                carrotBox.setImageResource(R.drawable.carrot_10)
            }

            11 -> {
                carrotBox.setImageResource(R.drawable.carrot_11)
            }

            12 -> {
                carrotBox.setImageResource(R.drawable.carrot_12)
            }

            13 -> {
                carrotBox.setImageResource(R.drawable.carrot_13)
            }

            14 -> {
                carrotBox.setImageResource(R.drawable.carrot_14)
            }

            15 -> {
                carrotBox.setImageResource(R.drawable.carrot_15)
            }

            16 -> {
                carrotBox.setImageResource(R.drawable.carrot_16)
            }

            17 -> {
                carrotBox.setImageResource(R.drawable.carrot_17)
            }

            18 -> {
                carrotBox.setImageResource(R.drawable.carrot_18)
            }

            19 -> {
                carrotBox.setImageResource(R.drawable.carrot_19)
            }

            else -> {
                carrotBox.setImageResource(R.drawable.carrot_20)
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
    }

    // 블루투스 활성화 메서드
/*    @SuppressLint("MissingPermission")
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
    @SuppressLint("MissingPermission")
    fun bluetoothOff() {
        if (mBluetoothAdapter.isEnabled) {
            mBluetoothAdapter.disable()
            Toast.makeText(this, "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

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
                    connectSelectedDevice(
                        items[item].toString()
                    )
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
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID)
            mBluetoothSocket.connect()
            mThreadConnectedBluetooth = ConnectedBluetoothThread(mBluetoothSocket)
            mThreadConnectedBluetooth.start()
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1)
                .sendToTarget()
        } catch (e: IOException) {
            Toast.makeText(this, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            Log.e("Error Reason", e.toString())
        }
    }

    inner class ConnectedBluetoothThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        // 스레드 생성자
        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {

                Toast.makeText(applicationContext, "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        // run() 메서드
        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int
            while (true) {
                try {
                    bytes = mmInStream!!.available() //현재 읽을 수 있는 바이트 수를 리턴
                    if (bytes != 0) {
                        SystemClock.sleep(100)
                        bytes = mmInStream.available()
                        bytes = mmInStream.read(
                            buffer,
                            0,
                            bytes
                        ) //read(byte[]b, int off, int len) -> len만큼 읽어서 byte[]b의 off위치에 저장하고 읽은 바이트 수를 리턴
                        Log.d("run", "run")
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget()
                    }
                } catch (e: IOException) {
                    break
                }
            }
        }

        fun write(str: String) {
            val bytes = str.toByteArray()
            try {
                mmOutStream!!.write(bytes)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }*/
    //----------------------------------------------------------------------------------------------------





}