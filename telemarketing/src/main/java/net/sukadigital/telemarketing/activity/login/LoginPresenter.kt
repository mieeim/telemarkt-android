package net.sukadigital.telemarketing.activity.login

import android.annotation.SuppressLint
import android.util.Log
import net.sukadigital.telemarketing.service.ApiService
import net.sukadigital.telemarketing.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginPresenter(val view: LoginView) {
    @SuppressLint("CheckResult")
    fun login(email:String, password:String, device:String){
        val hashMap = HashMap<String, Any>()
        hashMap.put("email",email)
        hashMap.put("password",password)
        hashMap.put("device",device)
        RetrofitService.telemarketing.create(ApiService::class.java).login(hashMap).subscribeOn(
            Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if(it.isSuccessful){
                    view.loginResponse(it)
                }else{
                    view.loginFailed("Login failed!")
                }
            },{
                Log.e("error",it.toString())
                view.loginFailed("Login failed!")
            })
    }
}