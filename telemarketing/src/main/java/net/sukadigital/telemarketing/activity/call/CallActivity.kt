package net.sukadigital.telemarketing.activity.call

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.provider.CallLog
import android.telecom.Call
import android.telecom.TelecomManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import net.sukadigital.telemarketing.PrefManager
import net.sukadigital.telemarketing.R
import net.sukadigital.telemarketing.model.BaseResponse
import net.sukadigital.telemarketing.util.snackbarColor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_call.*
import kotlinx.android.synthetic.main.dialog_select_sim.*
import retrofit2.Response
import java.util.concurrent.TimeUnit

class CallActivity : AppCompatActivity(), CallView {
    private lateinit var number: String
    private val disposables = CompositeDisposable()
    private lateinit var prefManager: PrefManager
    private lateinit var loading: Dialog
    private lateinit var presenter: CallPresenter
    private var incomingCall = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        number = intent.data!!.schemeSpecificPart
        prefManager = PrefManager(this)
        presenter = CallPresenter(this)
        callActivity_name.text = prefManager.customerName.toString()
    }

    override fun onStart() {
        super.onStart()
        callActivity_callEnd.setOnClickListener {
            OngoingCall.hangup()
        }
        callActivity_callAnswer.setOnClickListener {
            OngoingCall.answer()
            incomingCall = true
        }
        callActivity_notAnswered.setOnClickListener {
            presenter.confirmCustomer(
                prefManager.accessToken.toString(), prefManager.call_id.toString(),
                NOT_ANSWERED
            )
        }
        callActivity_accepted.setOnClickListener {
            presenter.confirmCustomer(
                prefManager.accessToken.toString(), prefManager.call_id.toString(),
                ACCEPTED
            )
        }
        callActivity_denied.setOnClickListener {
            presenter.confirmCustomer(
                prefManager.accessToken.toString(), prefManager.call_id.toString(),
                DENIED
            )
        }
        callActivity_notValid.setOnClickListener {
            presenter.confirmCustomer(
                prefManager.accessToken.toString(), prefManager.call_id.toString(),
                NOT_VALID
            )
        }
        callActivity_byChat.setOnClickListener {
            presenter.confirmCustomer(
                prefManager.accessToken.toString(), prefManager.call_id.toString(),
                CONTINUE_CHAT
            )
        }


        OngoingCall.state
            .subscribe(::updateUi)
            .addTo(disposables)
        OngoingCall.state
            .filter { it == Call.STATE_SELECT_PHONE_ACCOUNT }
            .firstElement()
            .subscribe {
                runOnUiThread {
                    dialogChangeDefaultSim()
                }
            }
            .addTo(disposables)
        OngoingCall.state
            .filter { it == Call.STATE_DISCONNECTED }
            .delay(1, TimeUnit.SECONDS)
            .firstElement()
            .subscribe {
                //call ended
//                deleteCallLogByNumber(number)
                runOnUiThread {
                    if (!incomingCall && !prefManager.customerName.toString().equals("")) {
                        confirmCustomer()
                    } else {
                        Handler().postDelayed({ this.finish() }, 1000)
                    }
                }
                deleteLastCallLog(this, number)
            }
            .addTo(disposables)

    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {
        callActivity_callInfo.text = "${state.asString().toLowerCase().capitalize()}\n"
        callActivity_callEndLayout.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )
        callActivity_callAnswerLayout.isVisible = state == Call.STATE_RINGING
    }

    fun confirmCustomer() {
        callActivity_callEndLayout.visibility = View.GONE
        callActivity_confirmLayout.visibility = View.VISIBLE
    }

    fun dialogChangeDefaultSim() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_select_sim)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.dialogSelectSim_ok.setOnClickListener {
            startActivityForResult(
                Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS),
                REQUEST_CHANGE_SIM
            )
        }
        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    fun deleteCallLogByNumber(number: String) {
        try {
            val queryString = "NUMBER=$number"
            this.contentResolver.delete(CallLog.Calls.CONTENT_URI, queryString, null)
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }

    }

    fun deleteLastCallLog(context: Context, phoneNumber: String) {
        try {
            //Thread.sleep(4000);
            val strNumberOne = arrayOf(phoneNumber)
            val cursor: Cursor? = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.NUMBER + " = ? ", strNumberOne, CallLog.Calls.DATE + " DESC"
            )
            if (cursor!!.moveToFirst()) {
                val idOfRowToDelete: Int = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID))
                val foo = context.contentResolver.delete(
                    CallLog.Calls.CONTENT_URI,
                    CallLog.Calls._ID + " = ? ", arrayOf(idOfRowToDelete.toString())
                )
            }
        } catch (ex: java.lang.Exception) {
            Log.v(
                "deleteNumber", "Exception, unable to remove # from call log: "
                        + ex.toString()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHANGE_SIM) {
            finish()
        }
    }


    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }

        const val REQUEST_CHANGE_SIM = 21
        const val NOT_ANSWERED = "NOT_ANSWERED"
        const val ACCEPTED = "ACCEPTED"
        const val DENIED = "DENIED"
        const val NOT_VALID = "NUMBER_NOT_VALID"
        const val CONTINUE_CHAT = "CONTINUE_BY_CHAT"
    }

    override fun showLoading() {
        loading = Dialog(this)
        loading.setContentView(R.layout.dialog_loading)
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)
        loading.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        loading.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        loading.show()
        disabledButton(false)
    }

    override fun hideLoading() {
        if (loading.isShowing) {
            loading.dismiss()
        }
    }

    private fun disabledButton(isEnable: Boolean) {
        callActivity_notValid.isEnabled = isEnable
        callActivity_byChat.isEnabled = isEnable
        callActivity_accepted.isEnabled = isEnable
        callActivity_denied.isEnabled = isEnable
        callActivity_notAnswered.isEnabled = isEnable
    }

    override fun showError(error: String) {
        Log.e("error", error)
    }

    override fun confirmResponse(response: Response<BaseResponse>) {
        Log.e("confirm response", response.toString())
        snackbarColor("success confirm", callActivity_parentView, this, R.color.cyan)
        prefManager.call_id = ""
        prefManager.customerName = ""
        prefManager.number = ""
        Handler().postDelayed({
            disabledButton(true)
            finish()
        }, 1000)
    }
}