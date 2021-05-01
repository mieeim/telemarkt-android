package net.sukadigital.telemarketing.activity.main

import net.sukadigital.telemarketing.model.BaseResponse
import retrofit2.Response

interface UserView {
    fun getUserResponse(response:Response<BaseResponse>)
    fun showError(error:String)
    fun getNumberResponse(response:Response<BaseResponse>)
    fun showLoading(loading:Boolean)
    fun failedGetNumber(error:String)
}
