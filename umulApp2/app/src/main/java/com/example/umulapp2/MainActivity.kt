package com.example.umulapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.umulapp2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
//    var DB:DBHelper?=null
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val buttonMain = findViewById<Button>(R.id.button_main)

        fun moveToAnotherPage(){
            val intent = Intent(this, insertInfor::class.java)
            startActivity(intent)
        }

        buttonMain.setOnClickListener{
            moveToAnotherPage()
        }








    }
}