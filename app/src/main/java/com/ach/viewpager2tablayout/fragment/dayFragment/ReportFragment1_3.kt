package com.ach.viewpager2tablayout.fragment.dayFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ach.viewpager2tablayout.R


class ReportFragment1_3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_report_fragment1_3, container, false,)

        setInit(view)
        return view
    }

    private fun setInit(view: View){
        var imageView = view.findViewById<ImageView>(R.id.graph)
    }
}