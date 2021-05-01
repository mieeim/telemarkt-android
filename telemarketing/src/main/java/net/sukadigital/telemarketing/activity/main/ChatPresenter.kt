package net.sukadigital.telemarketing.activity.main

import android.annotation.SuppressLint
import net.sukadigital.telemarketing.service.ApiService
import net.sukadigital.telemarketing.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChatPresenter(val view:UserChatView) {
    @SuppressLint("CheckResult")
    fun getCustomerChatList(accessToken:String){
        RetrofitService.telemarketing.create(ApiService::class.java).getCustomerChat(accessToken)
            .subscribeOn(
                Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if(it.isSuccessful){
                    view.getCustomerChatListResponse(it)
                }else{
                    view.showError(it.toString())
                }
            },{
                view.showError(it.toString())
            })
    }
    @SuppressLint("CheckResult")
    fun getUrlWebView(accessToken: String, call_id:String){
        view.showLoading(true)
        RetrofitService.telemarketing.create(ApiService::class.java).getChatUrl(accessToken,call_id)
            .subscribeOn(
                Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                view.showLoading(false)
                if(it.isSuccessful){
                    view.getUrlWebviewResponse(it)
                }else{
                    view.showError(it.toString())
                }
            },{
                view.showLoading(false)
                view.showError(it.toString())
            })

    }
}
