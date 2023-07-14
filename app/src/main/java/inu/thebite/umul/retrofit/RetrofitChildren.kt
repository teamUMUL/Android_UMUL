package inu.thebite.umul.retrofit

import android.util.Log
import inu.thebite.umul.model.SaveChildrenRequest
import inu.thebite.umul.model.SaveChildrenResponse
import retrofit2.Call
import retrofit2.Response

class RetrofitChildren(
    private val childInfo: SaveChildrenRequest,
    private val memberNumber: String,
) {

    private val service = RetrofitAPI.emgMedService

    fun save() {
        // call 작업은 두 가지로 실행됨
        // execute를 사용하면 request를 보내고 response를 받는 행위를 동기적으로 수행한다.
        // enqueue 작업을 실행하면 request는 비동기적으로 보내고, response는 콜백으로 받게 된다.
        service.saveChildren(memberNumber, childInfo)
            .enqueue(object : retrofit2.Callback<SaveChildrenResponse> {
                override fun onResponse(
                    call: Call<SaveChildrenResponse>,
                    response: Response<SaveChildrenResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("자녀 정보 추가 성공", "$result")
                    }
                }

                override fun onFailure(call: Call<SaveChildrenResponse>, t: Throwable) {
                    Log.d("자녀 정보 추가 실패", t.message.toString())
                }
            })

    }

    fun update() {
        service.updateChildren("김아무개", childInfo)
            .enqueue(object : retrofit2.Callback<SaveChildrenResponse> {
                override fun onResponse(
                    call: Call<SaveChildrenResponse>,
                    response: Response<SaveChildrenResponse>
                ) {
                    val result = response.body()
                    Log.d("자녀 정보 수정 성공", "$result")
                }

                override fun onFailure(call: Call<SaveChildrenResponse>, t: Throwable) {
                    Log.d("자녀 정보 수정 실패", t.message.toString())
                }
            })
    }

    fun delete() {
        service.deleteChildren("김아무개", memberNumber)
            .enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    val result = response.body()
                    Log.d("자녀 정보 삭제 성공", "$result")
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("자녀 정보 삭제 실패", t.message.toString())
                }
            })
    }
}
