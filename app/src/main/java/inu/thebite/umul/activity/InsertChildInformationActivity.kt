package inu.thebite.umul.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import inu.thebite.umul.R
import inu.thebite.umul.databinding.ActivityInsertChildInfomationBinding
import inu.thebite.umul.model.SaveChildrenRequest
import inu.thebite.umul.retrofit.RetrofitChildren
import java.text.SimpleDateFormat

class InsertChildInformationActivity : AppCompatActivity(),  View.OnClickListener {

    private lateinit var binding : ActivityInsertChildInfomationBinding
    private lateinit var heightEdit : EditText
    private lateinit var weightEdit : EditText
    private lateinit var birthDateEdit : EditText
    private lateinit var significantEdit : EditText
    private lateinit var nameEdit: EditText
    private lateinit var buttonAddInfor : Button
    private lateinit var radioButtonM : Button
    private lateinit var radioButtonF : Button
    private var height = 0f
    private var weight = 0f
    private var birthDate = ""
    private var significant = ""
    private var gender = ""
    private var name = ""
    private lateinit var memberNumber: String   // 추후 번호 입력 activity에서 정보 전달 값으로 대체
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertChildInfomationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberNumber = intent.getStringExtra("memberNumber").toString()
        heightEdit = binding.editHeight
        weightEdit = binding.editWeight
        birthDateEdit = binding.editBirthDate
        significantEdit = binding.editSignificant
        buttonAddInfor = binding.buttonAddInfor
        nameEdit = binding.editName
        name = binding.editName.text.toString()
        radioButtonM = binding.radioButtonM
        radioButtonF = binding.radioButtonF

        deleteHintOnFocus(birthDateEdit, "생년월일을 입력해주세요(ex : 2018-01-01)")
        deleteHintOnFocus(significantEdit, "특이사항을 입력해주세요 ()")
        deleteHintOnFocus(heightEdit, "110")
        deleteHintOnFocus(weightEdit, "20")
        deleteHintOnFocus(nameEdit, "이름을 입력해주세요")
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
                    name = binding.editName.text.toString()
                    height = heightEdit.text.toString().toFloat() //값을 스트링으로 플로트로 저장
                    weight = weightEdit.text.toString().toFloat()
                    // birthDate formatter 설정 필요 "yyyy-MM-dd"
                    birthDate = birthDateEdit.text.toString()
                    significant = significantEdit.text.toString()
                    if(gender != "" && height != 0.0f && weight!=0.0f && birthDate != "" && checkDate(birthDate)){
                        val body = SaveChildrenRequest(
                            name,
                            birthDate,
                            gender,
                            height,
                            weight
                        )
                        setMainActivity()
                        RetrofitChildren(body, memberNumber).save()
                    }
                    else{
                        Toast.makeText(applicationContext, "이름, 성별, 키, 몸무게, 생년월일울 올바르게 입력해주세요", Toast.LENGTH_LONG).show()
                    }

                }catch (e : NumberFormatException){
                    Toast.makeText(applicationContext, "올바른 형태로 입력하세요", Toast.LENGTH_LONG).show()

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
        nameEdit.clearFocus()
        return super.dispatchTouchEvent(ev)
    }

    private fun setMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        //activity 쌓이지 않도록 activity 초기화
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun deleteHintOnFocus(editText: EditText,defaultHint : String){
        editText.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if(b){
                editText.hint = ""
            }else{
                editText.hint = defaultHint
            }
        }
    }

    fun checkDate(checkDate: String?): Boolean {
        return try {
            val dateFormatParser = SimpleDateFormat("yyyy-MM-dd") //검증할 날짜 포맷 설정
            dateFormatParser.isLenient = false //false일경우 처리시 입력한 값이 잘못된 형식일 시 오류가 발생
            dateFormatParser.parse(checkDate) //대상 값 포맷에 적용되는지 확인
            true
        } catch (e: Exception) {
            false
        }
    }


}