package net.sukadigital.telemarketing.model

import com.google.gson.annotations.SerializedName

data class UserItem(
    @SerializedName("uid")
    val uid: String?="",
    @SerializedName("name")
    val name: String,
    @SerializedName("call_id")
    val call_id:String?=""
)