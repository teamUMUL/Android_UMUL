package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DailyReportBiteCountByMouthResponse (

    @SerializedName("date")
    var date: String,

    @SerializedName("biteCountByMouth")
    var biteCountByMouth: Float,

    @SerializedName("feedback")
    var feedback: String
)