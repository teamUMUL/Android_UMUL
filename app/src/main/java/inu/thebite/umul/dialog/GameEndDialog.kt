package inu.thebite.umul.dialog

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.activity.RecordActivity
import inu.thebite.umul.databinding.FragmentRecordBinding
import inu.thebite.umul.databinding.GameEndDialogBinding
import inu.thebite.umul.fragment.bottomNavFragment.BMIFragment
import inu.thebite.umul.fragment.bottomNavFragment.HomeFragment

class GameEndDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding : GameEndDialogBinding
    private lateinit var carrotBox : ImageView
    var carrotNum = MutableLiveData("")
    var successCount = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_end_dialog, container, false)
        binding.gameEndDialog = this
        binding.lifecycleOwner = this
        val carrotArgs = arguments?.getString("successNum")
        carrotNum.value = " 오늘의 식사\n\"당근 "+ carrotArgs+"개\"를\n 얻었습니다"
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (carrotArgs != null) {
            successCount = carrotArgs.toInt()
        }
        carrotBox = binding.gameEndCarrotBox
        setCarrotCarrier()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
    }
    private fun setOnClickListener(){
        val goReportBtn : Button = binding.goHome
        val cancelBtn : Button = binding.cancel


        goReportBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.goHome -> {
                (activity as RecordActivity?)?.setMainActivityStart()
            }
            R.id.cancel -> {
                dismiss()
            }
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