package inu.thebite.umul.model

import com.google.gson.annotations.SerializedName

data class SaveMemberResponse(

    @SerializedName("member_number")
    var memberNumber: String,

    @SerializedName("nickname")
    var nickname: String

)
