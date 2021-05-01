package net.sukadigital.telemarketing.activity.call

import android.annotation.SuppressLint
import net.sukadigital.telemarketing.service.ApiService
import net.sukadigital.telemarketing.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CallPresenter(val callView: CallView) {
    @SuppressLint("CheckResult")
    fun confirmCustomer(accessToken: String, callId: String, status: String) {
        callView.showLoading()
        val hashMap = HashMap<String, Any>()
        hashMap.put("call_status", status)
        RetrofitService.telemarketing.create(ApiService::class.java)
            .confirmCustomer(accessToken, callId, hashMap).subscribeOn(
                Schedulers.io()
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callView.hideLoading()
                if (it.isSuccessful) {
                    callView.confirmResponse(it)
                } else {
                    callView.showError(it.toString())
                }
            }, {
                callView.hideLoading()
                callView.showError(it.toString())
            })

    }
}
