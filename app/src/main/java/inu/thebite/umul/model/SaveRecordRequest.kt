package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class SaveRecordRequest (

    @SerializedName("date")
    var date: LocalDate,

    @SerializedName("slot")
    var slot: String,

    @SerializedName("totalTime")
    var totalTime: Int,

    @SerializedName("totalCount")
    var totalCount: Int,

    @SerializedName("biteCountByMouth")
    var biteCountByMouth: Int,

    @SerializedName("successCount")
    var successCount: Int
    )