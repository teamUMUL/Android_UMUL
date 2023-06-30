package inu.thebite.umul.fragment.bottomNavFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import inu.thebite.umul.R
import inu.thebite.umul.databinding.FragmentBMIBinding


class BMIFragment : Fragment(), View.OnClickListener {
    private lateinit var binding : FragmentBMIBinding
    var feedback_title = MutableLiveData("")
    var feedback_first = MutableLiveData("")
    var feedback_second = MutableLiveData("")
    var height : Float = 0.0f
    var weight : Float = 0.0f
    private var bmi : Float = 0.0f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_b_m_i, container, false)
        binding.bmiFragment = this
        binding.lifecycleOwner = this
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener(){
        val calcBtn = binding.calcButton
        calcBtn.setOnClickListener(this)

    }


    override fun onClick(v: View?){
        when(v?.id){
            R.id.calc_button -> {
                height = binding.editHeight.text.toString().toFloat()/100.0f
                weight = binding.editWeight.text.toString().toFloat()
                bmi = weight/(height*height)
                bmi = kotlin.math.round(bmi*100)/100

                feedback_title.value = "BMI지수는 "+bmi.toString()+"입니다."

            }
        }
    }
}