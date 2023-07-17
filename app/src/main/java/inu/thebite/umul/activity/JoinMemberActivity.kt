package inu.thebite.umul.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import inu.thebite.umul.databinding.ActivityJoinMemberBinding
import inu.thebite.umul.model.SaveMemberRequest
import inu.thebite.umul.retrofit.RetrofitMember

class JoinMemberActivity : AppCompatActivity() {

    private lateinit var binding : ActivityJoinMemberBinding
    private lateinit var memberNumber : String
    private lateinit var nickname : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinMemberBinding.inflate(layoutInflater)
        var button = binding.buttonMain
        setContentView(binding.root)

        memberNumber = binding.editTextPhone.text.toString()
        nickname = binding.inputnickname.text.toString()

        val member = SaveMemberRequest(memberNumber, nickname)

        button.setOnClickListener{

            RetrofitMember().save(member)

            val intent = Intent(this, InsertChildInformationActivity::class.java)
            intent.putExtra("memberNumber", memberNumber)
            startActivity(intent)
        }
    }

}