package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DailyReportBiteCountByMouthResponse (

    @SerializedName("date")
    var date: LocalDate,

    @SerializedName("biteCountByMouth")
    var biteCountByMouth: Int,

    @SerializedName("feedback")
    var feedback: String
)