package net.sukadigital.telemarketing.activity.change_password

import android.annotation.SuppressLint
import net.sukadigital.telemarketing.service.ApiService
import net.sukadigital.telemarketing.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChangePasswordPresenter(val view:ChangePasswordView) {
    @SuppressLint("CheckResult")
    fun changePassword(accessToken:String, password:String, confirmPassword:String){
        val hashMap = HashMap<String, Any>()
        hashMap.put("password", password)
        hashMap.put("password_confirmation",confirmPassword)
        RetrofitService.telemarketing.create(ApiService::class.java)
            .changePassword(accessToken, hashMap).subscribeOn(
                Schedulers.io()
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isSuccessful) {
                    view.changePasswordResponse(it)
                } else {
                    view.showError(it.toString())
                }
            }, {
                view.showError(it.toString())
            })
    }
}
