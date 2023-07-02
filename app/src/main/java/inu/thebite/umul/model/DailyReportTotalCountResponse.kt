package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DailyReportTotalCountResponse (

    @SerializedName("date")
    var date: String,

    @SerializedName("totalCount")
    var totalCount: Float,

    @SerializedName("feedback")
    var feedback: String
    )