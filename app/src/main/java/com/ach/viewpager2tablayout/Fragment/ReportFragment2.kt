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


class ReportFragment2 : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report2, container, false)


        setInit(view)
        return view
    }



    private fun setInit(view: View) {
        var imageView = view.findViewById<ImageView>(R.id.graph)
    }
}