package inu.thebite.umul.retrofit

import android.util.Log
import inu.thebite.umul.model.DailyReportBiteCountByMouthResponse
import inu.thebite.umul.model.DailyReportTotalCountResponse
import inu.thebite.umul.model.DailyReportTotalTimeResponse
import retrofit2.Call
import retrofit2.Response

class RetrofitReport(private val childrenId: Int) {

    private val service = RetrofitAPI.emgMedService

    fun getDailyReportTotalCount() {
        service.getDailyReportWithTotalCount(childrenId)
            .enqueue(object : retrofit2.Callback<DailyReportTotalCountResponse> {
                override fun onResponse(
                    call: Call<DailyReportTotalCountResponse>,
                    response: Response<DailyReportTotalCountResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("총 저작횟수 가져오기 성공", "$result")
                    }
                }

                override fun onFailure(call: Call<DailyReportTotalCountResponse>, t: Throwable) {
                    Log.d("총 저작횟수 가져오기 실패", t.message.toString())
                }
            })
    }

    fun getDailyReportTotalTime() {
        service.getDailyReportWithTotalTime(childrenId)
            .enqueue(object : retrofit2.Callback<DailyReportTotalTimeResponse> {
                override fun onResponse(
                    call: Call<DailyReportTotalTimeResponse>,
                    response: Response<DailyReportTotalTimeResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("총 식사시간 가져오기 성공", "$result")
                    }
                }

                override fun onFailure(call: Call<DailyReportTotalTimeResponse>, t: Throwable) {
                    Log.d("총 식사시간 가져오기 실패", t.message.toString())
                }
            })
    }

    fun getDailyReportBiteCountByMouth() {
        service.getDailyReportBiteCountByMouth(childrenId)
            .enqueue(object : retrofit2.Callback<DailyReportBiteCountByMouthResponse> {
                override fun onResponse(
                    call: Call<DailyReportBiteCountByMouthResponse>,
                    response: Response<DailyReportBiteCountByMouthResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("한 입당 저작횟수 가져오기 성공", "$result")
                    }
                }

                override fun onFailure(
                    call: Call<DailyReportBiteCountByMouthResponse>,
                    t: Throwable
                ) {
                    Log.d("한 입당 저작횟수 가져오기 실패", t.message.toString())
                }
            })
    }
}