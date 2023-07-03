package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName

data class BmiResponse (

    @SerializedName("bmi")
    var bmi: Double,

    @SerializedName("result")
    var result: String
)