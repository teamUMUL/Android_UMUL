package inu.thebite.umul.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityRecordBinding
import inu.thebite.umul.dialog.GameEndDialog
import kotlin.random.Random


class RecordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityRecordBinding
    private lateinit var gameStartButton: ImageButton
    private lateinit var getChewButton: ImageButton
    private lateinit var getCarrotButton: ImageButton
    private lateinit var gameEndButton : ImageButton
    private lateinit var characters : ImageView
    private lateinit var carrotBox : ImageView
    private lateinit var overlayView: ConstraintLayout
    private lateinit var ani : AnimationDrawable
    private var chewCount = 0
    private var totalCnt = 0
    private var successCount = 0
    private var avgABiteCnt = 0
    private var spoonCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        binding = DataBindingUtil.setContentView<ActivityRecordBinding>(this, R.layout.activity_record)
        binding.recordActivity = this

        //상단바랑 하단바 숨기기 -> 전체화면
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsCompat.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsCompat.hide(WindowInsetsCompat.Type.navigationBars())


        createClouds()

        overlayView = binding.overlayView
        gameStartButton = binding.gameStartButton

        gameStartButton.bringToFront()
        gameStartButton.setOnClickListener {
            gameStartButton.isVisible = false
            getChewButton.isVisible = true
            characters.isVisible = true
            carrotBox.isVisible = true
            gameEndButton.isVisible = true
            getCarrotButton.isVisible = true
        }

        getChewButton = binding.getChew
        characters = binding.characters
        carrotBox = binding.carrotBox
        gameEndButton = binding.gameEndButton
        getCarrotButton = binding.getCarrot
        getChewButton.isVisible = false
        characters.isVisible = false
        carrotBox.isVisible = false
        gameEndButton.isVisible = false
        getCarrotButton.isVisible = false
        ani = binding.characters.drawable as AnimationDrawable

        ani.isOneShot = true
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.get_chew -> {
                characters.layoutParams.width = 1200
                characters.requestLayout()
                chewCount++
                totalCnt++
                animateCharacter()
            }
            R.id.get_carrot -> {
                characters.layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                characters.requestLayout()
                spoonCount++
                if(chewCount>=30){
                    successCount++
                    characters.setImageResource(R.drawable.success_get_carrot)
                    ani = binding.characters.drawable as AnimationDrawable
                    ani.start()
                }
                else{
                    characters.setImageResource(R.drawable.fail_get_carrot)
                    ani = binding.characters.drawable as AnimationDrawable
                    ani.start()
                }

                chewCount=0
                setCarrotCarrier()
            }
            R.id.game_end_button -> {
                showEndDialog()
                try {
                    avgABiteCnt = totalCnt/spoonCount
                    successCount = 0
                    spoonCount = 0
                    chewCount = 0
                    setCarrotCarrier()
                }catch (_: Exception){
                }
                gameStartButton.isVisible = true
                getChewButton.isVisible = false
                characters.isVisible = false
                carrotBox.isVisible = false
                gameEndButton.isVisible = false
                getCarrotButton.isVisible = false

            }


        }
    }

    fun showEndDialog(){
        val dialog = GameEndDialog()
        val args = Bundle()
        args.putString("successNum" , successCount.toString())
        dialog.arguments = args
        dialog.show(supportFragmentManager, "click_game_end")
    }
    fun setMainActivityStart(){
        val intent = Intent(this, MainActivity::class.java)
        //activity 쌓이지 않도록 activity 초기화
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun animateCharacter(){
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
        for (x in 0 until 12) {
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
        ObjectAnimator.ofFloat(cloud, "translationX", -2000f).apply {
            duration = Random.nextLong(6000, 15000)
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private fun setCarrotCarrier(){
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



}