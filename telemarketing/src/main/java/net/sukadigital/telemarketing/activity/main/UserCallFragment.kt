package net.sukadigital.telemarketing.activity.main

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.telecom.TelecomManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import net.sukadigital.telemarketing.PrefManager
import net.sukadigital.telemarketing.R
import net.sukadigital.telemarketing.activity.login.LoginActivity
import net.sukadigital.telemarketing.model.BaseResponse
import net.sukadigital.telemarketing.model.UserItem
import net.sukadigital.telemarketing.util.snackbarColor
import kotlinx.android.synthetic.main.fragment_user_call.*
import kotlinx.android.synthetic.main.fragment_user_call.view.*
import org.json.JSONObject
import retrofit2.Response

class UserCallFragment : Fragment(), UserView, UserListListener {
    companion object {
        const val REQUEST_PERMISSION = 11
        const val CHANGE_DEFAULT_CALL = 22
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var listUser: ArrayList<UserItem>
    private lateinit var presenter: UserPresenter
    private lateinit var prefManager: PrefManager
    private lateinit var number: String
    private lateinit var callId: String
    private lateinit var parentView: View
    private val gson = Gson()
    private val simSlotName = arrayOf(
        "extra_asus_dial_use_dualsim",
        "com.android.phone.extra.slot",
        "slot",
        "simslot",
        "sim_slot",
        "subscription",
        "Subscription",
        "phone",
        "com.android.phone.DialingMode",
        "simSlot",
        "slot_id",
        "simId",
        "simnum",
        "phone_type",
        "slotId",
        "slotIdx"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_user_call, container, false)
        listUser = ArrayList()
        recyclerView = parentView.mainRv
        recyclerView.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        adapter = UserAdapter(listUser, this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        prefManager = PrefManager(activity!!)
        presenter = UserPresenter(this)
        return parentView
    }

    override fun onStart() {
        super.onStart()
        presenter.getUser(prefManager.accessToken.toString())
        swipeRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                presenter.getUser(prefManager.accessToken.toString())
            }

        })
    }

    override fun onClickUser(item: UserItem) {
        Log.d("getNumber user", "user ${item.name} id ${item.uid}")
        if (checkIsDefaultCaller()) {
            prefManager.customerName = item.name
            presenter.getPhoneNumber(prefManager.accessToken.toString(), item.uid!!)
        } else {
            offerReplacingDefaultDialer()
        }
    }

    override fun getUserResponse(response: Response<BaseResponse>) {
        if(!checkIsDefaultCaller()){
            offerReplacingDefaultDialer()
        }
        listUser.clear()
        swipeRefresh.isRefreshing = false
        val status = JSONObject(gson.toJson(response.body()!!)).getString("status")
        if (status.equals("ok")) {
            val data = JSONObject(gson.toJson(response.body()!!)).getJSONArray("data")
            for (i in 0 until data.length()) {
                val temp: UserItem =
                    gson.fromJson(data.get(i).toString(), UserItem::class.java)
                listUser.add(temp)
            }
            listUser.sortBy { it.name }
            adapter.notifyDataSetChanged()
        } else {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            activity!!.finish()
        }
    }

    override fun showError(error: String) {
        Log.d("error", error)
    }

    override fun getNumberResponse(response: Response<BaseResponse>) {
        val data = JSONObject(gson.toJson(response.body()!!)).getJSONObject("data")
        number = data.getString("number")
        callId = data.getString("call_id")
        Log.d("getNumber success", "number $number call_id $callId")
        prefManager.call_id = callId
        Handler().postDelayed({
            activity!!.runOnUiThread {
                progress_loader.visibility = View.GONE
                makeCall()
            }
        }, 1000)
    }

    override fun showLoading(loading: Boolean) {
        if (loading) {
            progress_loader.visibility = View.VISIBLE
        } else {
            progress_loader.visibility = View.GONE
        }
    }

    override fun failedGetNumber(error: String) {
        snackbarColor("error", parentView, activity!!, R.color.red)
    }

    fun checkIsDefaultCaller(): Boolean {
        Log.d(
            "dialer package default",
            activity!!.getSystemService(TelecomManager::class.java).defaultDialerPackage
        )
        return activity!!.getSystemService(TelecomManager::class.java).defaultDialerPackage == activity!!.packageName
    }

    private fun offerReplacingDefaultDialer() {
        if (!checkIsDefaultCaller()) {
            Log.d("default phone call", "false")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val rm = activity!!.getSystemService(Context.ROLE_SERVICE) as RoleManager
                startActivityForResult(
                    rm.createRequestRoleIntent(RoleManager.ROLE_DIALER),
                    CHANGE_DEFAULT_CALL
                )
            } else {
                Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(
                        TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                        activity!!.packageName
                    )
                    .let(::startActivity)
            }
        }
    }

    private fun makeCall() {
        if (PermissionChecker.checkSelfPermission(
                activity!!,
                Manifest.permission.CALL_PHONE
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_CALL_LOG
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_CALL_LOG
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_PHONE_STATE
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            Log.d("startActivity call", number)
            val uri = "tel:${number}".toUri()
            val intent = Intent(Intent.ACTION_CALL, uri)
            startActivity(intent)
        } else {
            Log.d("startActivity call", "ask permission")
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE
                ),
                REQUEST_PERMISSION
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION && PermissionChecker.PERMISSION_GRANTED in grantResults) {
            makeCall()
        }

    }


}