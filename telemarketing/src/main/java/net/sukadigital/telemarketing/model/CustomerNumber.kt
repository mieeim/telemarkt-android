package net.sukadigital.telemarketing.model

import com.google.gson.annotations.SerializedName

data class CustomerNumber(
    @SerializedName("call_id")
    val call_id:String,
    @SerializedName("number")
    val number:String)