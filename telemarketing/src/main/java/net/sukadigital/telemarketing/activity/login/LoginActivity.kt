package net.sukadigital.telemarketing.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.jaredrummler.android.device.DeviceName
import net.sukadigital.telemarketing.PrefManager
import net.sukadigital.telemarketing.R
import net.sukadigital.telemarketing.activity.main.MainActivity
import net.sukadigital.telemarketing.model.BaseResponse
import net.sukadigital.telemarketing.util.snackbarColor
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Response

class LoginActivity : AppCompatActivity(), LoginView {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var deviceName: String
    private lateinit var presenter: LoginPresenter
    private lateinit var prefManager: PrefManager
    private var gson = Gson()
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        DeviceName.init(this)
        val dname = DeviceName.getDeviceName()
        val android_id = Settings.Secure.getString(
            getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        deviceName = "$dname - $android_id"
        prefManager = PrefManager(this)
        presenter = LoginPresenter(this)
        loginButton.setOnClickListener {
            userLogin()
        }
    }

    private fun userLogin() {
        email = edtLogUser.text.toString().trim()
        password = edtLogPass.text.toString().trim()

        if (email.isEmpty()) {
            input_layout_log_username.error = "input username/email"
            edtLogUser.requestFocus()
            return
        } else {
            input_layout_log_username.error = null
        }
        if (password.isEmpty()) {
            input_layout_log_pass.error = "input password"
            edtLogPass.requestFocus()
            return
        } else {
            input_layout_log_pass.error = null
        }
        presenter.login(email,password,deviceName)

    }

    override fun loginResponse(response: Response<BaseResponse>) {
        Log.d("login response", response.toString())
        snackbarColor("Login success", loginParentView, this, R.color.cyan)
        val data = JSONObject(gson.toJson(response.body()!!)).getString("data")
        prefManager.accessToken = "Bearer $data"
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            this.finish()
        }, 1000)
    }

    override fun loginFailed(error: String) {
        Log.d("login response", error)
        snackbarColor(error, loginParentView, this, R.color.red)
    }

}