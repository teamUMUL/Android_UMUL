package inu.thebite.umul.retrofit

import android.util.Log
import inu.thebite.umul.model.SaveChildrenResponse
import inu.thebite.umul.model.SaveMemberRequest
import inu.thebite.umul.model.SaveMemberResponse
import retrofit2.Call
import retrofit2.Response

class RetrofitMember(private val saveMemberRequest: SaveMemberRequest) {

    private val service = RetrofitAPI.emgMedService

    fun save() {
        service.saveMember(saveMemberRequest)
            .enqueue(object : retrofit2.Callback<SaveMemberResponse> {
                override fun onResponse(
                    call: Call<SaveMemberResponse>,
                    response: Response<SaveMemberResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("회원 정보 추가 성공", "$result")
                    }
                }

                override fun onFailure(call: Call<SaveMemberResponse>, t: Throwable) {
                    Log.d("회원 정보 추가 실패", t.message.toString())
                }
            })

    }
}