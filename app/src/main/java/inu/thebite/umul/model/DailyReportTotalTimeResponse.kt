package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DailyReportTotalTimeResponse (

    @SerializedName("date")
    var date: String,

    @SerializedName("totalTime")
    var totalTime: Float,

    @SerializedName("feedback")
    var feedback: String
)