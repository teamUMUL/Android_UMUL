package inu.thebite.umul.retrofit

import android.util.Log
import inu.thebite.umul.model.SaveMemberRequest
import inu.thebite.umul.model.SaveMemberResponse
import inu.thebite.umul.model.SaveRecordRequest
import retrofit2.Call
import retrofit2.Response

class RetrofitSaveRecord(private val memberNumber: String, private val childName: String) {

    private val service = RetrofitAPI.emgMedService

    fun save(saveRecordRequest: SaveRecordRequest) {
        service.saveRecord(memberNumber, childName, saveRecordRequest)
            .enqueue(object : retrofit2.Callback<SaveRecordRequest> {
                override fun onResponse(
                    call: Call<SaveRecordRequest>,
                    response: Response<SaveRecordRequest>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("식습관 정보 추가 성공", "$result")
                    }
                }

                override fun onFailure(call: Call<SaveRecordRequest>, t: Throwable) {
                    Log.d("식습관 정보 추가 실패", t.message.toString())
                }
            })

    }
}