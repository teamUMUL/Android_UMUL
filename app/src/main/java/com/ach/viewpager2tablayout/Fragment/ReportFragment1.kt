package com.ach.viewpager2tablayout.Fragment

import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_1
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_2
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_3
import com.ach.viewpager2tablayout.R
import com.ach.viewpager2tablayout.databinding.ActivityMainBinding
import com.ach.viewpager2tablayout.databinding.FragmentReport1Binding
import org.w3c.dom.Text


class ReportFragment1 : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentReport1Binding

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
        val view = inflater.inflate(R.layout.fragment_report1, container, false)

        val reportBtn11 = view.findViewById<Button>(R.id.btn1_1)
        val reportBtn12 = view.findViewById<Button>(R.id.btn1_2)
        val reportBtn13 = view.findViewById<Button>(R.id.btn1_3)

        reportBtn11.setOnClickListener(this)
        reportBtn12.setOnClickListener(this)
        reportBtn13.setOnClickListener(this)

        setInit(view)
        return view
    }


    override fun onClick(v: View?){
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        binding = FragmentReport1Binding.inflate(layoutInflater)


        when(v?.id){
            R.id.btn1_1 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_1())
                transaction.addToBackStack(null)
                transaction.commit()

                binding.reportName.text = "#총 저작횟수"
            }
            R.id.btn1_2 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_2())
                transaction.addToBackStack(null)
                transaction.commit()


                binding.reportName.text = "#총 식사시간"
            }
            R.id.btn1_3 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_3())
                transaction.addToBackStack(null)
                transaction.commit()

                binding.reportName.text = "#한 입당 저작횟수"
            }
        }
        true
    }



    private fun setInit(view: View) {
        var imageView = view.findViewById<ImageView>(R.id.graph)
    }
}