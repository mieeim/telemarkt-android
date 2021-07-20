package net.sukadigital.telemarketing.activity.call

import net.sukadigital.telemarketing.model.BaseResponse
import retrofit2.Response

interface CallView {
    fun showLoading()
    fun hideLoading()
    fun showError(error:String)
    fun confirmResponse(response:Response<BaseResponse>)
}
