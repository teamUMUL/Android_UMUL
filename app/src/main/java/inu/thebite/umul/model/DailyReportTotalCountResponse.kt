package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DailyReportTotalCountResponse (

    @SerializedName("date")
    var date: LocalDate,

    @SerializedName("totalCount")
    var totalCount: Int,

    @SerializedName("feedback")
    var feedback: String
    )