package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName

data class SaveChildrenResponse(

    @SerializedName("birth")
    var birth: String,

    @SerializedName("gender")
    var gender: String,

    @SerializedName("height")
    var height: Float,

    @SerializedName("weight")
    var weight: Float
)