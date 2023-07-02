package inu.thebite.umul.retrofit

import android.util.Log
import inu.thebite.umul.model.BmiResponse
import inu.thebite.umul.model.SaveChildrenResponse
import retrofit2.Call
import retrofit2.Response

class RetrofitBMI(private val childrenId: Int) {
    private val service = RetrofitAPI.emgMedService

    fun calculateBmi() {
        service.getChildrenBmi(childrenId)
            .enqueue(object : retrofit2.Callback<BmiResponse> {
                override fun onResponse(
                    call: Call<BmiResponse>,
                    response: Response<BmiResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("bmi 정보 가져와서 계산하기 성공", "$result")
                    }
                }

                override fun onFailure(call: Call<BmiResponse>, t: Throwable) {
                    Log.d("bmi 정보 가져와서 계산하기 실패", t.message.toString())
                }
            })

    }
}