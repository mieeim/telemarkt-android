package net.sukadigital.telemarketing.activity.main

import android.annotation.SuppressLint
import net.sukadigital.telemarketing.service.ApiService
import net.sukadigital.telemarketing.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserPresenter(val view:UserView) {
    @SuppressLint("CheckResult")
    fun getUser(accessToken:String){
        RetrofitService.telemarketing.create(ApiService::class.java).customer(accessToken)
            .subscribeOn(
            Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if(it.isSuccessful){
                    view.getUserResponse(it)
                }else{
                    view.showError(it.toString())
                }
            },{
                view.showError(it.toString())
            })
    }

    @SuppressLint("CheckResult")
    fun getPhoneNumber(accessToken: String, uid:String){
        view.showLoading(true)
        RetrofitService.telemarketing.create(ApiService::class.java).getNumber(accessToken,uid)
            .subscribeOn(
                Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                view.showLoading(false)
                if(it.isSuccessful){
                    view.getNumberResponse(it)
                }else{
                    view.failedGetNumber("Failed to get number")
                }
            },{
                view.showLoading(false)
                view.failedGetNumber("Failed to get number")
            })
    }

}
