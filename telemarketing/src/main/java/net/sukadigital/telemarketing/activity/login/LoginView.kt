package net.sukadigital.telemarketing.activity.login

import net.sukadigital.telemarketing.model.BaseResponse
import retrofit2.Response

interface LoginView {
    fun loginResponse(response: Response<BaseResponse>)
    fun loginFailed(error:String)
}
