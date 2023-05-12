package com.ach.viewpager2tablayout.Fragment.DayFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ach.viewpager2tablayout.R


class ReportFragment1_2 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_report_fragment1_2, container, false,)

        setInit(view)
        return view
    }

    private fun setInit(view: View){
        var imageView = view.findViewById<ImageView>(R.id.graph)
    }
}