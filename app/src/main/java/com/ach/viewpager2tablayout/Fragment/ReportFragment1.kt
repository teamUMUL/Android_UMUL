package com.ach.viewpager2tablayout.Fragment

import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_1
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_2
import com.ach.viewpager2tablayout.Fragment.DayFragment.ReportFragment1_3
import com.ach.viewpager2tablayout.R


class ReportFragment1 : Fragment(), View.OnClickListener {

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

        when(v?.id){
            R.id.btn1_1 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_1())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.btn1_2 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_2())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.btn1_3 -> {
                transaction.replace(R.id.reportFrame1, ReportFragment1_3())
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