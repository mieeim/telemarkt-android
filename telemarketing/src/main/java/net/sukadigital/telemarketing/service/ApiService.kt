package net.sukadigital.telemarketing.service

import net.sukadigital.telemarketing.model.BaseResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/login")
    fun login(
        @Body body: HashMap<String, Any>
    ): Observable<Response<BaseResponse>>

    @POST("api/change-password")
    fun changePassword(
        @Header("Authorization") accessToken: String,
        @Body body: HashMap<String, Any>
    ): Observable<Response<BaseResponse>>

    @GET("api/customer")
    fun customer(@Header("Authorization") accessToken: String): Observable<Response<BaseResponse>>

    @GET("api/customer/{uid}/number")
    fun getNumber(
        @Header("Authorization") accessToken: String,
        @Path("uid") uid: String
    ): Observable<Response<BaseResponse>>

    @POST("api/customer/{call_id}/confirm")
    fun confirmCustomer(
        @Header("Authorization") accessToken: String,
        @Path("call_id") call_id: String,
        @Body body: HashMap<String, Any>
    ): Observable<Response<BaseResponse>>

    //CHAT
    @GET("api/customer-chat")
    fun getCustomerChat(@Header("Authorization") accessToken: String): Observable<Response<BaseResponse>>

    @GET("api/customer/{call_id}/chat")
    fun getChatUrl(
        @Header("Authorization") accessToken: String,
        @Path("call_id") call_id: String,
    ): Observable<Response<BaseResponse>>
}