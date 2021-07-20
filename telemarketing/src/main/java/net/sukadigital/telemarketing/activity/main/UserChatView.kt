package net.sukadigital.telemarketing.activity.main

import net.sukadigital.telemarketing.model.BaseResponse
import retrofit2.Response

interface UserChatView {
    fun getCustomerChatListResponse(response:Response<BaseResponse>)
    fun getUrlWebviewResponse(response: Response<BaseResponse>)
    fun showError(error:String)
    fun showLoading(active:Boolean)
}
