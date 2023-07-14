package inu.thebite.umul.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import inu.thebite.umul.fragment.dayFragment.ReportFragment1_1
import inu.thebite.umul.fragment.dayFragment.ReportFragment1_2
import inu.thebite.umul.fragment.dayFragment.ReportFragment1_3
import inu.thebite.umul.R
import inu.thebite.umul.databinding.FragmentReport1Binding
import java.time.LocalDate


class ReportFragment1 : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentReport1Binding

    //실시간 반응이 필요한 경우 MutableLiveData 타입 사용
    var reportName = MutableLiveData("#총 저작횟수")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.beginTransaction().replace(R.id.reportFrame1, ReportFragment1_1())
            .commit()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report1, container, false)
        binding.reportFragment1 = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener(){
        val btnSequence = binding.btnContainer1.children
        btnSequence.forEach {
            btn -> btn.setOnClickListener(this)
        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?){
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()

        when(v?.id){
            R.id.btn1_1 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_1())
                transaction.addToBackStack(null)
                transaction.commit()

                reportName.value = "#총 저작횟수"
            }
            R.id.btn1_2 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_2())
                transaction.addToBackStack(null)
                transaction.commit()


                reportName.value = "#총 식사시간"
            }
            R.id.btn1_3 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_3())
                transaction.addToBackStack(null)
                transaction.commit()

                reportName.value = "#한 입당 저작횟수"


            }
        }
        true
    }




}