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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityRecordBinding
import inu.thebite.umul.dialog.GameEndDialog
import inu.thebite.umul.model.SaveRecordRequest
import inu.thebite.umul.retrofit.RetrofitSaveRecord
import inu.thebite.umul.retrofit.RetrofitService
import inu.thebite.umul.service.BluetoothService
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.lang.ArithmeticException
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random


@Suppress("DEPRECATION")
class RecordActivity : AppCompatActivity(), View.OnClickListener {

    //블루투스 통신-------------------------------------
    //bluetoothReceiver = Service에서 보낸 데이터를 받기
    //bluetoothService = 블루투스 연결을 유지하기 위한 Service
    private lateinit var bluetoothReceiver : BroadcastReceiver
    private lateinit var bluetoothService: BluetoothService
    private var bound: Boolean = false
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
    private lateinit var getCarrotButton: ImageButton
    private lateinit var gameEndButton: ImageButton
    private lateinit var backPressButton: ImageButton
    private lateinit var characters: ImageView
    private lateinit var carrotBox: ImageView
    private lateinit var overlayView: ConstraintLayout
    private lateinit var ani: AnimationDrawable
    private lateinit var handler: Handler

    private var seconds = 0     //seconds = 식사시간
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isTimerRunning: Boolean = false
    private var chewCnt = 0             //chewCount = 저작횟수(뽑기 -> 초기화)
    private var totalCnt = 0            //totalCnt = 총 저작횟수(뽑기 -> 초기화X)
    private var successCnt = 0          //succssCnt = 성공 횟수(chewCount>=30 -> +1)
    private var avgABiteCnt = 0         //avgABiteCnt = 한 입당 저작횟수(totalCnt/spoonCnt)
    private var successChewCnt = 0      //successChewCnt = 성공할 때의 총 저작횟수
    private var successAvgABiteCnt = 0  //successAvgABiteCnt = 성공 시에 한 입당 저작횟수(successChewCnt/successCnt)
    private var failChewCnt = 0         //failChewCnt = 실패할 때의 총 저작횟수
    private var failAvgABiteCnt = 0     //failAvgABiteCnt = 실패 시에 한 입당 저작횟수(failChewCnt/(spoonCnt-successCnt))
    private var spoonCnt = 0            //spoonCnt = 한 입 횟수(수저횟수)
    private var isStart : Boolean = false                   //게임 시작 유무
    private lateinit var memberNumber: String
    private lateinit var childName: String

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
        val memberNumberPref = getSharedPreferences("MemberNumber", Context.MODE_PRIVATE)
        memberNumber =  memberNumberPref.getString("MemberNumber", "010-0000-0000").toString()
        val pref: SharedPreferences = getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
        childName = pref.getString("selectedChild", "홍길동").toString()
        bluetoothReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == ACTION_DATA_RECEIVED){
                    if(isStart) {
                        characters.layoutParams.width = 1200
                        characters.requestLayout()
                        chewCnt++
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

        handler = Handler()
        createClouds()

        overlayView = binding.overlayView
        gameStartButton = binding.gameStartButton

        gameStartButton.bringToFront()
        gameStartButton.setOnClickListener {
            registerReceiver(bluetoothReceiver, filter)

            gameStartButton.isVisible = false
            backPressButton.isVisible = false
            characters.isVisible = true
            carrotBox.isVisible = true
            gameEndButton.isVisible = true
            getCarrotButton.isVisible = true
            //타이머 시작
            startTimer()
            isStart = true
        }

        characters = binding.characters
        carrotBox = binding.carrotBox
        gameEndButton = binding.gameEndButton
        getCarrotButton = binding.getCarrot
        backPressButton = binding.gameBackPress
        backPressButton.isVisible = true
        characters.isVisible = false
        carrotBox.isVisible = false
        gameEndButton.isVisible = false
        getCarrotButton.isVisible = false
        ani = binding.characters.drawable as AnimationDrawable

        ani.isOneShot = true
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.get_carrot -> {
                //뽑기 -> 성공(chewCnt >= 30)과 실패에 따른 활동
                //뽑기 시 발생하는 애니메이션은 저작활동 시 발생하는 애니메이션과 너비가 다르기 때문에 너비를 Wrap_Content로 설정
                characters.layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                characters.requestLayout()
                spoonCnt++
                if (chewCnt >= 30) {
                    successCnt++
                    successChewCnt += chewCnt
                    //성공 -> success_get_carrot애니메이션 보여줌
                    characters.setImageResource(R.drawable.success_get_carrot)
                    ani = binding.characters.drawable as AnimationDrawable
                    ani.start()
                } else {
                    failChewCnt += chewCnt
                    characters.setImageResource(R.drawable.fail_get_carrot)
                    ani = binding.characters.drawable as AnimationDrawable
                    ani.start()
                }
                chewCnt = 0
                setCarrotCarrier()
            }

            R.id.game_end_button -> {

                showEndDialog()
                try{
                    avgABiteCnt = totalCnt / spoonCnt
                    successAvgABiteCnt = successChewCnt / successCnt
                    failAvgABiteCnt = failChewCnt / (spoonCnt - successCnt)
                }catch (e : ArithmeticException){

                }


                //타이머 종료
                stopTimer()
                // retrofit
                val result = SaveRecordRequest(LocalDate.now().toString(), "저녁", seconds, totalCnt, avgABiteCnt, successCnt, successAvgABiteCnt, failAvgABiteCnt)
                Log.d("date = ", result.date)
                Log.d("slot = ", result.slot)
                Log.d("totalTime = ", result.totalTime.toString())
                Log.d("totalCnt = ", result.totalCount.toString())
                Log.d("avgABiteCnt = ", result.biteCountByMouth.toString())
                Log.d("successCnt = ", result.successCount.toString())
                Log.d("successAvgABiteCnt = ", result.countPerSuccess.toString())
                Log.d("failAvgABiteCnt = ", result.countPerFail.toString())

                saveEatingHabitData(result)

                setCarrotCarrier()

                try {

                } catch (_: Exception) {
                }
                backPressButton.isVisible = true
                gameStartButton.isVisible = true
                characters.isVisible = false
                carrotBox.isVisible = false
                gameEndButton.isVisible = false
                getCarrotButton.isVisible = false


                Toast.makeText(this, seconds.toString()+"초, 성공횟수 :"+ successCnt.toString() + "수저횟수 :"+spoonCnt.toString() + "평균저작횟수 : "+avgABiteCnt.toString()+"총 저작횟수 : "+totalCnt.toString(), Toast.LENGTH_LONG).show()
                //모든 데이터 리셋
                resetAllData()
            }
            R.id.game_back_press->{
                finish()
            }
        }
    }


    private fun saveEatingHabitData(saveRecordRequest: SaveRecordRequest) {
        RetrofitSaveRecord(memberNumber, childName).save(saveRecordRequest)
    }

    //타이머
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
        //게임 종료 시 dialog에 성공횟수 데이터 보내면서 열기
        val dialog = GameEndDialog()
        val args = Bundle()
        args.putString("successNum", successCnt.toString())
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
        intent.putExtra("memberNumber", memberNumber)
        intent.putExtra("childName", childName)
        //activity 쌓이지 않도록 activity 초기화
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun animateCharacter() {
        //각 저작횟수에 따른 캐릭터 애니메이션
        when (chewCnt) {
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
        //구름 생성
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
            //투명도 = alpha
            alpha = Random.nextDouble(0.4, 0.8).toFloat()
            //이동 범위 = translationX
            translationX = Random.nextDouble(-500.0, 300.0).toFloat()
        }
    }

    private fun animateCloud(cloud: ImageView) {
        ObjectAnimator.ofFloat(cloud, "translationX", -2400f).apply {
            //속도 = duration(클 수록 느림)
            duration = Random.nextLong(18000, 23000)
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private fun setCarrotCarrier() {
        //성공횟수에 따른 당근 보관함 이미지 변경
        when (successCnt) {
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

    private fun resetAllData(){
        //게임 종료 -> 모든 데이터 리셋
        seconds = 0
        startTime = 0
        elapsedTime = 0
        isTimerRunning = false
        chewCnt = 0
        totalCnt = 0
        successCnt = 0
        avgABiteCnt = 0
        successChewCnt = 0
        successAvgABiteCnt = 0
        failChewCnt = 0
        failAvgABiteCnt = 0
        spoonCnt = 0
    }
}