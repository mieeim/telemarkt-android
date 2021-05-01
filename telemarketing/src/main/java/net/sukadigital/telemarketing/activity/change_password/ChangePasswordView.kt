package net.sukadigital.telemarketing.activity.change_password

import net.sukadigital.telemarketing.model.BaseResponse
import retrofit2.Response

interface ChangePasswordView {
    fun changePasswordResponse(response:Response<BaseResponse>)
    fun showError(error:String)
}
