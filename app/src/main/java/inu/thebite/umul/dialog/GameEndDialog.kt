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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.databinding.FragmentRecordBinding
import inu.thebite.umul.databinding.GameEndDialogBinding
import inu.thebite.umul.fragment.bottomNavFragment.BMIFragment
import inu.thebite.umul.fragment.bottomNavFragment.HomeFragment

class GameEndDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding : GameEndDialogBinding

    var carrotNum = MutableLiveData("")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_end_dialog, container, false)
        binding.gameEndDialog = this
        binding.lifecycleOwner = this
        val carrotArgs = arguments?.getString("successNum")
        carrotNum.value = "당근을 총 "+ carrotArgs+"개 뽑았습니다!"
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
    }
    private fun setOnClickListener(){
        val goReportBtn : Button = binding.goReport
        val cancelBtn : Button = binding.cancel


        goReportBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.goReport -> {
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, BMIFragment())
                    .commit()
                (activity as MainActivity?)?.setReportChecked()
            }
            R.id.cancel -> {
                dismiss()
            }
        }
    }

}