package inu.thebite.umul.retrofit

import inu.thebite.umul.model.BmiResponse
import inu.thebite.umul.model.DailyReportBiteCountByMouthResponse
import inu.thebite.umul.model.DailyReportTotalCountResponse
import inu.thebite.umul.model.DailyReportTotalTimeResponse
import inu.thebite.umul.model.SaveChildrenRequest
import inu.thebite.umul.model.SaveChildrenResponse
import inu.thebite.umul.model.SaveRecordRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitService {

    // 자녀 등록
    @Headers("Content-Type: application/json")
    @POST("{memberNumber}/children")
    fun saveChildren(@Path("memberNumber") memberNumber: String,
                     @Body saveChildrenRequest: SaveChildrenRequest) : Call<SaveChildrenResponse>

    // 자녀 정보 수정
    @Headers("Content-Type: application/json")
    @PATCH("{childrenId}/children")
    fun updateChildren(@Path("childrenId") childrenId: Int,
                     @Body saveChildrenRequest: SaveChildrenRequest) : Call<SaveChildrenResponse>

    // 자녀 정보 삭제
    @Headers("Content-Type: application/json")
    @DELETE("{childrenId}/children/{memberNumber}")
    fun deleteChildren(@Path("childrenId") childrenId: Int,
                        @Path("memberNumber") memberNumber: String) : Call<Void>

    // 저장되어 있는 정보로 BMI 계산
    @Headers("Content-Type: application/json")
    @GET("{childrenId}/children/bmi")
    fun getChildrenBmi(@Path("childrenId") childrenId: Int) : Call<BmiResponse>

    // 식습관 기록 저장
    @Headers("Content-Type: application/json")
    @POST("{memberNumber}/{childrenId}/save")
    fun saveRecord(@Path("memberNumber") memberNumber: String,
                    @Path("childrenId") childrenId: Int,
                    @Body saveRecordRequest: SaveRecordRequest) : Call<SaveRecordRequest>

    // 일일레포트 총 저작횟수 정보 가져오기
    @Headers("Content-Type: application/json")
    @GET("{childrenId}/dailyReport/totalCount")
    fun getDailyReportWithTotalCount(@Path("childrenId") childrenId: Int) : Call<DailyReportTotalCountResponse>

    // 일일레포트 총 식사시간 정보 가져오기
    @Headers("Content-Type: application/json")
    @GET("{childrenId}/dailyReport/totalTime")
    fun getDailyReportWithTotalTime(@Path("childrenId") childrenId: Int) : Call<DailyReportTotalTimeResponse>

    // 일일레포트 한 입당 저작횟수 정보 가져오기
    @Headers("Content-Type: application/json")
    @GET("{childrenId}/dailyReport/biteCountByMouth")
    fun getDailyReportBiteCountByMouth(@Path("childrenId") childrenId: Int) : Call<DailyReportBiteCountByMouthResponse>

    // 저장되어 있는 자녀 리스트 가져오기
    @Headers("Content-Type: application/json")
    @GET("{memberNumber}/children/list")
    fun getChildrenList(@Path("memberNumber") memberNumber: String) : Call<DailyReportBiteCountByMouthResponse>


}