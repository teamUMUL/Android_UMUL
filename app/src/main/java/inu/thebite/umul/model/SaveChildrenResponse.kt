package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName

data class SaveChildrenResponse(

    @SerializedName("child_id")
    var child_id: Int,

    @SerializedName("name")
    var name: String,

    @SerializedName("birth")
    var birth: String,

    @SerializedName("gender")
    var gender: String,

    @SerializedName("height")
    var height: Float,

    @SerializedName("weight")
    var weight: Float,

    @SerializedName("memo")
    var memo: String
)