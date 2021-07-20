package net.sukadigital.telemarketing

import android.content.Context
import android.content.SharedPreferences

class PrefManager(val context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor
    //shared preference mode
    private val PRIVATE_MODE = Context.MODE_PRIVATE
    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
    companion object{
        private const val PREF_NAME = "telemarketing"
        private const val TOKEN = "accessToken"
        private const val NUMBER = "number"
        private const val CALL_ID = "callId"
        private const val CUSTOMER_NAME = "name"
    }
    var accessToken:String?
        get() = pref.getString(TOKEN,"")
        set(token){
            editor.putString(TOKEN,token)
            editor.apply()
        }

    var number:String?
        get() = pref.getString(NUMBER,"")
        set(number){
            editor.putString(NUMBER,number)
            editor.apply()
        }

    var call_id:String?
        get() = pref.getString(CALL_ID,"")
        set(id){
            editor.putString(CALL_ID,id)
            editor.apply()
        }
    var customerName:String?
        get() = pref.getString(CUSTOMER_NAME,"")
        set(name){
            editor.putString(CUSTOMER_NAME,name)
            editor.apply()
        }

    fun logout() {
        editor.clear();
        editor.commit();
    }
}