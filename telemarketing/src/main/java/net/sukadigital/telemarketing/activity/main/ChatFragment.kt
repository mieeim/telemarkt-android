package net.sukadigital.telemarketing.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import net.sukadigital.telemarketing.PrefManager
import net.sukadigital.telemarketing.R
import net.sukadigital.telemarketing.activity.chat.ChatWebviewActivity
import net.sukadigital.telemarketing.model.BaseResponse
import net.sukadigital.telemarketing.model.UserItem
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import org.json.JSONObject
import retrofit2.Response

class ChatFragment : Fragment(),UserChatView, UserListListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var listUser: ArrayList<UserItem>
    private lateinit var presenter: ChatPresenter
    private lateinit var prefManager: PrefManager
    private lateinit var parentView: View
    private val gson = Gson()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_chat, container, false)
        prefManager = PrefManager(activity!!)
        presenter = ChatPresenter(this)
        listUser = ArrayList()
        recyclerView = parentView.chatRv
        recyclerView.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        adapter = UserAdapter(listUser, this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        return parentView
    }

    override fun onStart() {
        super.onStart()
        presenter.getCustomerChatList(prefManager.accessToken.toString())
        swipeChatUser.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                presenter.getCustomerChatList(prefManager.accessToken.toString())
            }
        })
    }
    override fun getCustomerChatListResponse(response: Response<BaseResponse>) {
        listUser.clear()
        swipeChatUser.isRefreshing = false
        val status = JSONObject(gson.toJson(response.body()!!)).getString("status")
        if(status.equals("ok")) {
            val data = JSONObject(gson.toJson(response.body()!!)).getJSONArray("data")
            for (i in 0 until data.length()) {
                val temp: UserItem =
                    gson.fromJson(data.get(i).toString(), UserItem::class.java)
                listUser.add(temp)
            }
            listUser.sortBy { it.name }
            adapter.notifyDataSetChanged()
        }else{
//            val intent = Intent(activity, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//            activity!!.finish()
        }
    }

    override fun getUrlWebviewResponse(response: Response<BaseResponse>) {
        val data = JSONObject(gson.toJson(response.body()!!)).getJSONObject("data")
        val url = data.getString("url")
        val intent = Intent(activity,ChatWebviewActivity::class.java)
        intent.putExtra("url",url)
        startActivity(intent)

    }

    override fun showError(error: String) {
        Log.d("error", error)
    }

    override fun showLoading(active: Boolean) {
        if (active) {
            progress_loader_chat.visibility = View.VISIBLE
        } else {
            progress_loader_chat.visibility = View.GONE
        }
    }

    override fun onClickUser(item: UserItem) {
        presenter.getUrlWebView(prefManager.accessToken.toString(),item.call_id!!)
    }

}