package com.ach.viewpager2tablayout.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_1
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_2
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_3
import com.ach.viewpager2tablayout.Fragment.WeekFragment.ReportFragment2_1
import com.ach.viewpager2tablayout.Fragment.WeekFragment.ReportFragment2_2
import com.ach.viewpager2tablayout.Fragment.WeekFragment.ReportFragment2_3
import com.ach.viewpager2tablayout.R


class ReportFragment2 : Fragment(), View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report2, container, false)

        val reportBtn21 = view.findViewById<Button>(R.id.btn2_1)
        val reportBtn22 = view.findViewById<Button>(R.id.btn2_2)
        val reportBtn23 = view.findViewById<Button>(R.id.btn2_3)

        reportBtn21.setOnClickListener(this)
        reportBtn22.setOnClickListener(this)
        reportBtn23.setOnClickListener(this)

        setInit(view)
        return view
    }


    override fun onClick(v: View?){

        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()

        when(v?.id){
            R.id.btn2_1 -> {
                transaction.replace(R.id.reportFrame2, ReportFragment2_1())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.btn2_2 -> {
                transaction.replace(R.id.reportFrame2, ReportFragment2_2())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.btn2_3 -> {
                transaction.replace(R.id.reportFrame2, ReportFragment2_3())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        true
    }

    private fun setInit(view: View) {
        var imageView = view.findViewById<ImageView>(R.id.graph)
    }
}