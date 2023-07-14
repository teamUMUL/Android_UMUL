package inu.thebite.umul.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityInsertInforBinding
import inu.thebite.umul.databinding.ActivityMainBinding
import inu.thebite.umul.databinding.ActivityRecordBinding
import inu.thebite.umul.model.SaveChildrenRequest
import inu.thebite.umul.retrofit.RetrofitAPI
import inu.thebite.umul.retrofit.RetrofitChildren
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Date

class InsertInfoActivity : AppCompatActivity(),  View.OnClickListener {

    private lateinit var binding : ActivityInsertInforBinding
    private lateinit var heightEdit : EditText
    private lateinit var weightEdit : EditText
    private lateinit var birthDateEdit : EditText
    private lateinit var significantEdit : EditText
    private lateinit var buttonAddInfor : Button
    private lateinit var radioButtonM : Button
    private lateinit var radioButtonF : Button
    private var height = 0f
    private var weight = 0f
    private var birthDate = ""
    private var significant = ""
    private var gender = ""
    private var memberNumber = "010-1234-5678"      // 추후 번호 입력 activity에서 정보 전달 값으로 대체
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert_infor)
        binding.inserInfoActivity = this
        setContentView(binding.root)

        heightEdit = binding.editHeight
        weightEdit = binding.editWeight
        birthDateEdit = binding.editBirthDate
        significantEdit = binding.editSignificant
        buttonAddInfor = binding.buttonAddInfor
        radioButtonM = binding.radioButtonM
        radioButtonF = binding.radioButtonF

        birthDateEdit.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if(b){
                birthDateEdit.hint = ""
            }else{
                birthDateEdit.hint = "생년월일을 입력해주세요(ex : 2018-01-01)"
            }
        }
        significantEdit.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if(b){
                significantEdit.hint = ""
            }
            else{
                significantEdit.hint = "특이사항을 입력해주세요 ()"
            }
        }

    }

    override fun onClick(v: View?) {


        when(v?.id){
            R.id.button_back -> {
                finish()
            }
            R.id.radioButtonM -> {
                gender = "M"
            }
            R.id.radioButtonF -> {
                gender = "F"
            }
            R.id.button_addInfor -> {
                try {
                    height = heightEdit.text.toString().toFloat() //값을 스트링으로 플로트로 저장
                    weight = weightEdit.text.toString().toFloat()
                    // birthDate formatter 설정 필요 "yyyy-MM-dd"
                    birthDate = birthDateEdit.text.toString()
                    significant = significantEdit.text.toString()
                    if(gender != "" && height != 0.0f && weight!=0.0f && birthDate != ""){
                        Toast.makeText(this, gender+" "+height.toString()+"cm "+weight.toString()+"kg "+birthDate+" "+significant, Toast.LENGTH_LONG).show()
                        //DB저장
                        val body = SaveChildrenRequest(
                            "홍길동",
                            birthDate,
                            gender,
                            height,
                            weight
                        )

                        RetrofitChildren(body, memberNumber).save()
                        setMainActivity()
                    }
                    else{
                        Toast.makeText(this, "성별, 키, 몸무게, 생년월일울 입력해주세요", Toast.LENGTH_LONG).show()
                    }

                }catch (e : NumberFormatException){
                    Toast.makeText(this, "올바른 형태로 입력하세요", Toast.LENGTH_LONG).show()

                }


            }
        }
    }
    //입력 부분 외에 바깥을 클릭할 경우 -> focus해제, 키보드 내려감
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        birthDateEdit.clearFocus()
        significantEdit.clearFocus()
        heightEdit.clearFocus()
        weightEdit.clearFocus()
        return super.dispatchTouchEvent(ev)
    }

    private fun setMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        //activity 쌓이지 않도록 activity 초기화
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


}