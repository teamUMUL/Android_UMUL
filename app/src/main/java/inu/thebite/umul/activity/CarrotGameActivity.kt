package inu.thebite.umul.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityCarrotGameBinding
import inu.thebite.umul.dialog.GameEndDialog
import inu.thebite.umul.model.SaveRecordRequest
import inu.thebite.umul.retrofit.RetrofitSaveRecord
import inu.thebite.umul.service.BluetoothService
import kotlin.random.Random


@Suppress("DEPRECATION")
class CarrotGameActivity : AppCompatActivity(), View.OnClickListener {

    //블루투스 통신-------------------------------------
    //bluetoothReceiver = Service에서 보낸 데이터를 받기
    //bluetoothService = 블루투스 연결을 유지하기 위한 Service
    private lateinit var bluetoothReceiver : BroadcastReceiver
    private lateinit var bluetoothService: BluetoothService
    private var bound: Boolean = false
    private var filter: IntentFilter? = null

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
    private lateinit var binding: ActivityCarrotGameBinding
    private lateinit var gameStartButton: ImageButton
    private lateinit var gameEndButton: ImageButton
    private lateinit var backPressButton: ImageButton
    private lateinit var tempPullCarrotButton: ImageButton
    private lateinit var characters: ImageView
    private lateinit var darkLayer: View
    private lateinit var ani: AnimationDrawable
    private lateinit var handler: Handler
    private var gameLevel = 0
    private var seconds = 0     //seconds = 식사시간
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isTimerRunning: Boolean = false
    private var chewCnt = 0             //chewCount = 저작횟수(뽑기 -> 초기화)
    private var totalCnt = 0            //totalCnt = 총 저작횟수(뽑기 -> 초기화X)
    private var successCnt = 0          //succssCnt = 성공 횟수(chewCount>=30 -> +1)
    private var isStart : Boolean = false                   //게임 시작 유무
    private lateinit var memberNumber: String
    private lateinit var childName: String
    //---------------------------------------------------
    private var screenWidth : Int = 0
    private var screenHeight : Int = 0

    companion object {
        const val ACTION_DATA_RECEIVED = "com.example.bluetooth.DATA_RECEIVED"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        binding =
            DataBindingUtil.setContentView<ActivityCarrotGameBinding>(this, R.layout.activity_carrot_game)
        binding.carrotGameActivity = this
        getMemberNumberFromPref()
        getChildNameFromPref()

        gameLevel = intent.getIntExtra("gameLevelState", 0)
        Toast.makeText(this, gameLevel.toString(), Toast.LENGTH_LONG).show()

        bluetoothReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == ACTION_DATA_RECEIVED){
                    if(isStart) {
                        totalCnt++
                        chewCnt++
                        animateCharacters()
                        addSuccessCnt()
                    }
                }
            }
        }


        filter = IntentFilter(ACTION_DATA_RECEIVED)
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


        darkLayer = binding.darkLayer
        characters = binding.carrotGameRabbitBearCarrot
        gameEndButton = binding.carrotGameEnd
        gameStartButton = binding.carrotGameStart
        backPressButton = binding.carrotGameBackPress
        tempPullCarrotButton = binding.tempPullCarrot

        darkLayer.isVisible = true
        characters.isVisible = false
        gameEndButton.isVisible = false
        gameStartButton.isVisible = true
        backPressButton.isVisible = true
        tempPullCarrotButton.isVisible = false

        val displayMetrics : DisplayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        Toast.makeText(this, "x: $screenWidth y:$screenHeight", Toast.LENGTH_LONG).show()

        //디스플레이에서 비율만큼 캐릭터 크기 설정
        resizeImageView(characters, 0.7f, 0.7f)

        //디스플레이에서 비율만큼 버튼 크기 설정
        resizeImageButton(backPressButton, 0.15f, 0.15f)
        resizeImageButton(gameStartButton, 0.3f, 0.3f)
        resizeImageButton(tempPullCarrotButton, 0.15f, 0.15f)
        resizeImageButton(gameEndButton, 0.15f, 0.15f)
    }

    private fun resizeImageView(imageView:ImageView, newX: Float, newY: Float){
        val imageWidth : Int = (screenWidth*newX).toInt()
        val imageHeight : Int = (screenHeight*newY).toInt()
        val characterLayoutParams: ViewGroup.LayoutParams = imageView.layoutParams
        characterLayoutParams.width = imageWidth
        characterLayoutParams.height = imageHeight
        imageView.layoutParams = characterLayoutParams
    }

    private fun resizeImageButton(imageButton: ImageButton, newX: Float, newY: Float){
        val btnWidth: Int = (screenWidth * newX).toInt()
        val btnHeight: Int = (screenHeight * newY).toInt()
        val btnLayoutParams = imageButton.layoutParams
        btnLayoutParams.width = btnWidth
        btnLayoutParams.height = btnHeight
        imageButton.layoutParams = btnLayoutParams
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.carrot_game_start->{
                startTimer() //타이머 시작
                registerReceiver(bluetoothReceiver, filter) //데이터 받기 시작
                darkLayer.isVisible = false
                gameStartButton.isVisible = false
                backPressButton.isVisible = false
                characters.isVisible = true
                gameEndButton.isVisible = true
                tempPullCarrotButton.isVisible = true
                //타이머 시작
                startTimer()
                isStart = true
            }

            R.id.carrot_game_end -> {

                showEndDialog()

                //타이머 종료
                stopTimer()

                backPressButton.isVisible = true
                gameStartButton.isVisible = true
                characters.isVisible = false
                gameEndButton.isVisible = false


                Toast.makeText(this, seconds.toString()+"초, 성공횟수 :"+ successCnt.toString() + "총 저작횟수: "+totalCnt.toString(), Toast.LENGTH_LONG).show()
                //모든 데이터 리셋
                resetAllData()
            }
            R.id.carrot_game_back_press->{
                finish()
            }
            //임시버튼
            R.id.temp_pull_carrot->{
                totalCnt++
                chewCnt++
                animateCharacters()
                addSuccessCnt()
            }
        }
    }

    private fun animateCharacters(){
        when (gameLevel) {
            1 -> {
                when (chewCnt) {
                    in 1..2 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_1)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    3 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_2)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    4 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_3)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }


                    5 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_4)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    6 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_5)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    7 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_6)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    8 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_7)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    9 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_8)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    10 -> {
                        characters.setImageResource(R.drawable.carrot_game_success)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }
                }
            }
            3 -> {
                when (chewCnt) {
                    in 1..2 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_1)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    3 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_3)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    in 4..5 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_3)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }


                    6 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_5)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    in 7..8 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_5)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    9 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_8)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    10 -> {
                        characters.setImageResource(R.drawable.carrot_game_success)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }
                }
            }
            else -> {
                when (chewCnt) {
                    in 1..4 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_1)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    5 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_4)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    in 6..9 -> {
                        characters.setImageResource(R.drawable.carrot_game_pulled_carrot_4)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }

                    10 -> {
                        characters.setImageResource(R.drawable.carrot_game_success)
                        ani = binding.carrotGameRabbitBearCarrot.drawable as AnimationDrawable
                        ani.start()
                    }
                }

            }
        }

    }

    private fun addSuccessCnt(){
        if(chewCnt == 10){
            chewCnt = 0
            successCnt++
        }
    }

    private fun getMemberNumberFromPref(){
        val memberNumberPref = getSharedPreferences("MemberNumber", Context.MODE_PRIVATE)
        memberNumber =  memberNumberPref.getString("MemberNumber", "010-0000-0000").toString()
    }

    private fun getChildNameFromPref(){
        val childNamePref: SharedPreferences = getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
        childName = childNamePref.getString("selectedChild", "홍길동").toString()
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

    private fun resetAllData(){
        //게임 종료 -> 모든 데이터 리셋
        seconds = 0
        startTime = 0
        elapsedTime = 0
        isTimerRunning = false
        chewCnt = 0
        totalCnt = 0
        successCnt = 0
    }
}