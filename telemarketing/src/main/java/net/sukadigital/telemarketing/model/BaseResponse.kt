package net.sukadigital.telemarketing.model

import com.google.gson.annotations.SerializedName

data class BaseResponse(@SerializedName("status")
                        val status: String,
                        @SerializedName("data")
                        val data: Any? = null)