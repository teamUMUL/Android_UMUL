package inu.thebite.umul.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import inu.thebite.umul.databinding.ActivityJoinMemberBinding
import inu.thebite.umul.model.SaveMemberRequest
import inu.thebite.umul.retrofit.RetrofitMember

class JoinMemberActivity : AppCompatActivity() {

    private lateinit var binding : ActivityJoinMemberBinding
    private var memberNumber = ""
    private var nickname =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinMemberBinding.inflate(layoutInflater)
        var button = binding.buttonMain
        setContentView(binding.root)


        button.setOnClickListener{

            memberNumber = binding.editTextPhone.text.toString()
            nickname = binding.inputnickname.text.toString()

            Log.d("member", "name : $memberNumber, nickname: $nickname")
            val member = SaveMemberRequest(memberNumber, nickname)

            RetrofitMember(member).save()

            val intent = Intent(this, InsertChildInformationActivity::class.java)
            intent.putExtra("memberNumber", memberNumber)
            startActivity(intent)
        }
    }

}