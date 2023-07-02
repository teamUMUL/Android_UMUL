package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DailyReportTotalTimeResponse (

    @SerializedName("date")
    var date: LocalDate,

    @SerializedName("totalTime")
    var totalTime: Int,

    @SerializedName("feedback")
    var feedback: String
)