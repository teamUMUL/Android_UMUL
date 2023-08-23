package inu.thebite.umul.activity

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
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityBalloonGameBinding
import inu.thebite.umul.dialog.GameEndDialog
import inu.thebite.umul.model.SaveRecordRequest
import inu.thebite.umul.retrofit.RetrofitSaveRecord
import inu.thebite.umul.service.BluetoothService
import java.lang.ArithmeticException
import java.time.LocalDate
import kotlin.random.Random


@Suppress("DEPRECATION")
class BalloonGameActivity : AppCompatActivity(), View.OnClickListener {

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
    private lateinit var binding: ActivityBalloonGameBinding
    private lateinit var gameStartButton: ImageButton
    private lateinit var gameEndButton: ImageButton
    private lateinit var backPressButton: ImageButton
    private lateinit var character: ImageView
    private lateinit var darkLayer: View
    private lateinit var balloon: ImageView
    private lateinit var balloonPops: ImageView
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
    //private var avgABiteCnt = 0         //avgABiteCnt = 한 입당 저작횟수(totalCnt/spoonCnt)
    //private var successChewCnt = 0      //successChewCnt = 성공할 때의 총 저작횟수
    //private var successAvgABiteCnt = 0  //successAvgABiteCnt = 성공 시에 한 입당 저작횟수(successChewCnt/successCnt)
    //private var failChewCnt = 0         //failChewCnt = 실패할 때의 총 저작횟수
    //private var failAvgABiteCnt = 0     //failAvgABiteCnt = 실패 시에 한 입당 저작횟수(failChewCnt/(spoonCnt-successCnt))
    //private var spoonCnt = 0            //spoonCnt = 한 입 횟수(수저횟수)
    private var isStart : Boolean = false                   //게임 시작 유무
    private lateinit var memberNumber: String
    private lateinit var childName: String

    private var originalBalloonScaleX = 1.0f
    private var originalBalloonScaleY = 1.0f

    companion object {
        const val ACTION_DATA_RECEIVED = "com.example.bluetooth.DATA_RECEIVED"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        binding =
            DataBindingUtil.setContentView<ActivityBalloonGameBinding>(this, R.layout.activity_balloon_game)
        binding.balloonGameActivity = this
        getMemberNumberFromPref()
        getChildNameFromPref()
        gameLevel = intent.getIntExtra("gameLevelState", 0)
        //리시버, 서비스 연결
        bluetoothReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == ACTION_DATA_RECEIVED){
                    if(isStart) {
                        balloon.isVisible = true
                        balloonPops.isVisible = false
                        totalCnt++
                        chewCnt++
                        if(chewCnt%gameLevel == 0){
                            animateCharacter()
                            setBalloonImage()
                        }
                        if(chewCnt==10){
                            chewCnt = 0
                        }
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

        //시계 핸들러
        handler = Handler()

        //원래 풍선 크기 저장
        balloon = binding.balloonGameBalloon
        originalBalloonScaleX = balloon.scaleX
        originalBalloonScaleY = balloon.scaleY

        gameStartButton = binding.balloonGameStart
        gameEndButton = binding.balloonGameEnd
        backPressButton = binding.balloonGameBackPress
        character = binding.balloonGameRabbit
        darkLayer = binding.darkLayer
        balloonPops = binding.balloonGamePops

        gameEndButton.isVisible = false
        character.isVisible = false

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.balloon_game_start->{
                startTimer() //타이머 시작
                registerReceiver(bluetoothReceiver, filter) //데이터 받기 시작
                darkLayer.isVisible = false
                gameStartButton.isVisible = false
                backPressButton.isVisible = false
                character.isVisible = true
                gameEndButton.isVisible = true
                //타이머 시작
                startTimer()
                isStart = true
            }

            R.id.balloon_game_end -> {

                showEndDialog()


                //타이머 종료
                stopTimer()
                // retrofit
/*                val result = SaveRecordRequest(LocalDate.now().toString(), "저녁", seconds, totalCnt, avgABiteCnt, successCnt, successAvgABiteCnt, failAvgABiteCnt)
                Log.d("date = ", result.date)
                Log.d("slot = ", result.slot)
                Log.d("totalTime = ", result.totalTime.toString())
                Log.d("totalCnt = ", result.totalCount.toString())
                Log.d("avgABiteCnt = ", result.biteCountByMouth.toString())
                Log.d("successCnt = ", result.successCount.toString())
                Log.d("successAvgABiteCnt = ", result.countPerSuccess.toString())
                Log.d("failAvgABiteCnt = ", result.countPerFail.toString())

                saveEatingHabitData(result)*/

                backPressButton.isVisible = true
                gameStartButton.isVisible = true
                character.isVisible = false

                gameEndButton.isVisible = false


                Toast.makeText(this, seconds.toString()+"초, 성공횟수 :"+ successCnt.toString() + "총 저작횟수: "+totalCnt.toString(), Toast.LENGTH_LONG).show()
                //모든 데이터 리셋
                resetAllData()
            }
            R.id.balloon_game_back_press->{
                finish()
            }
            //임시버튼
            R.id.temp_balloon_inflate->{
                balloon.isVisible = true
                balloonPops.isVisible = false
                totalCnt++
                chewCnt++
                //레벨이 1이 아닌 경우에는 성공 이펙트가 나온 후에 chewCnt가 오르더라도 3번 or 5번이 될 때까지 계속 토끼가 손을 올리고 있기에
                //레벨이 1이 아닐 때는 chewCnt가 1일 될 때 토끼를 기본 이미지로 변경한다.
                if(gameLevel != 1 && chewCnt == 1){
                    character.setImageResource(R.drawable.balloon_rabbit_1)
                }
                if(chewCnt%gameLevel == 0 || chewCnt == 10){
                    animateCharacter()
                    setBalloonImage()
                }

            }
        }
    }

    private fun animateCharacter(){
        if(chewCnt<10){
            character.setImageResource(R.drawable.balloon_inflate)
            ani = binding.balloonGameRabbit.drawable as AnimationDrawable
            ani.start()
        }
        else{
            character.setImageResource(R.drawable.balloon_pop_success_rabbit)
        }
    }
    private fun setBalloonImage(){
        val delayInMillis : Long =
            if(chewCnt<10){
                500 // 0.5초를 밀리초로 변환
            }else{
                0 //10번째에는 바로 딜레이 없이 바로 터짐
            }
        //2.5초 뒤에 풍선이 바뀜
        Handler(Looper.getMainLooper()).postDelayed({
            if(chewCnt<10){
                balloon.isVisible = true
                balloonPops.isVisible = false
                val balloonScaleFactor = 1.0f+(gameLevel/10f)
                balloon.scaleX *= balloonScaleFactor
                balloon.scaleY *= balloonScaleFactor
            }
            else{
                balloon.isVisible = false
                balloonPops.isVisible = true
                balloon.scaleX = originalBalloonScaleX // 초기 크기로 복원
                balloon.scaleY = originalBalloonScaleY
                addSuccessCnt()
            }
        }, delayInMillis)


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

    private fun showEndDialog() {
        //게임 종료 시 dialog에 성공횟수 데이터 보내면서 열기
        val dialog = GameEndDialog()
        val args = Bundle()
        args.putString("successNum", successCnt.toString())
        args.putString("game_state", "Balloon")
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
    private fun getMemberNumberFromPref(){
        val memberNumberPref = getSharedPreferences("MemberNumber", Context.MODE_PRIVATE)
        memberNumber =  memberNumberPref.getString("MemberNumber", "010-0000-0000").toString()
    }

    private fun getChildNameFromPref(){
        val childNamePref: SharedPreferences = getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
        childName = childNamePref.getString("selectedChild", "홍길동").toString()
    }

    private fun addSuccessCnt(){
        chewCnt = 0
        successCnt++
    }

    private fun resetAllData(){
        //게임 종료 -> 모든 데이터 리셋
        seconds = 0
        startTime = 0
        elapsedTime = 0
        isTimerRunning = false
        totalCnt = 0
        successCnt = 0
        chewCnt = 0
    }
}