package com.example.umulapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class insertInfor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_infor)

        val buttonAddInfor = findViewById<Button>(R.id.button_addInfor)

        fun moveToAnotherPage(){
            val intent = Intent(this, insertInfor::class.java)
            startActivity(intent)
        }

        buttonAddInfor.setOnClickListener{
            moveToAnotherPage()
        }




    }
}