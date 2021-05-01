package net.sukadigital.telemarketing.activity.change_password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.material.textfield.TextInputEditText
import net.sukadigital.telemarketing.PrefManager
import net.sukadigital.telemarketing.R
import net.sukadigital.telemarketing.model.BaseResponse
import net.sukadigital.telemarketing.util.snackbarColor
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity(), ChangePasswordView {
//    private lateinit var oldPass : TextInputEditText
    private lateinit var newPass : TextInputEditText
    private lateinit var rePass : TextInputEditText
    private lateinit var prefManager: PrefManager
    private lateinit var presenter: ChangePasswordPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        prefManager = PrefManager(this)
        presenter = ChangePasswordPresenter(this)
//        oldPass = changePassword_oldPass
        newPass = changePassword_newPass
        rePass = changePassword_reNewPass
        changePassword_newPass.setOnClickListener {
            changePassword_newPassLayout.error = null
        }
        changePassword_reNewPass.setOnClickListener {
            changePassword_reNewPassLayout.error = null
        }
        changePassword_submit.setOnClickListener {
            checkChangePassword()
        }

    }
    fun checkChangePassword(){
//        val oldPassword = oldPass.text.toString().trim()
        val newPassword = newPass.text.toString().trim()
        val reNewPassword = rePass.text.toString().trim()
//        if(oldPassword.isEmpty()){
//            changePassword_oldPassLayout.error = "input old password"
//            oldPass.requestFocus()
//            return
//        }else {
//            changePassword_oldPassLayout.error = null
//        }
        if(newPassword.isEmpty()){
            changePassword_newPassLayout.error = "input new password"
            newPass.requestFocus()
            return
        }else if(newPassword.length<6){
            changePassword_newPassLayout.error = "password min 6 character"
            newPass.requestFocus()
            return
        }else{
            changePassword_newPassLayout.error = null
            if(reNewPassword.equals(newPassword)){
                changePassword_reNewPassLayout.error = null
                presenter.changePassword(prefManager.accessToken.toString(),newPassword,reNewPassword)
            }else{
                changePassword_reNewPassLayout.error = "password not match"
                rePass.requestFocus()
            }

        }
    }

    override fun changePasswordResponse(response: Response<BaseResponse>) {
        snackbarColor("Change password success",changePassword_parentView,this,R.color.cyan)
        Handler().postDelayed({this.finish()},1500)
    }

    override fun showError(error: String) {
        Log.e("exception","change password failed")
    }
}