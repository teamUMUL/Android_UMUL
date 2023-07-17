package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName

data class SaveMemberRequest(

    @SerializedName("member_number")
    var memberNumber: String,

    @SerializedName("nickname")
    var nickname: String
)
