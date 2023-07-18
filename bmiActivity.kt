package com.example.umulapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class bmiActivity : AppCompatActivity(),  View.OnClickListener{

    private val iDuration: Long = 1000//지속시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)


    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}