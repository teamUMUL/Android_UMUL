package com.example.umulapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast


class insertInfor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_infor)
        //var : 변수
        //val : 값이 변경되지 못하는 변수
        val heightEdit: EditText = findViewById(R.id.editHeight) //객체 생성
        val weightEdit: EditText = findViewById(R.id.editWeight)
        val birthDateEdit: EditText = findViewById(R.id.editBirthDate)
        val significantEdit: EditText = findViewById(R.id.editSignificant)
        val buttonAddInfor:Button = findViewById(R.id.button_addInfor)


//        var height: String = "" //값을 담을 변수
//        var weight: String = ""
//        var birthDate: String = ""
//        var significant: String = ""


        var height = heightEdit.text.toString().toFloat() //값을 스트링으로 플로트로 저장
        var weight = weightEdit.text.toString().toFloat()
        var birthDate = birthDateEdit.text.toString()
        var significant = significantEdit.text.toString()


        fun onCheckboxClicked(view: View) {
            if (view is CheckBox) {
                val checked: Boolean = view.isChecked

                when (view.id) {
                    R.id.checkBoxM -> {
                        if (checked) {
                            // 남으로 선택했을 때
                        } else {
                            // 남 선택 안했을 때
                        }
                    }
                    R.id.checkBoxF -> {
                        if (checked) {
                            // 여 선택했을 때
                        } else {
                            // 여 선택안했을때
                        }
                    }
                    // TODO: Veggie sandwich
                }
            }
        }
        fun moveToAnotherPage(){
            val intent = Intent(this, insertInfor::class.java)   //다른 화면으로 이동하기 위한 인탠트 객페 생성
            startActivity(intent)
        }

        buttonAddInfor.setOnClickListener{

            moveToAnotherPage()
        }




    }
}