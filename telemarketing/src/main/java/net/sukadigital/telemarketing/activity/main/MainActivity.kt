package net.sukadigital.telemarketing.activity.main

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telecom.TelecomManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
//import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
//import com.google.firebase.messaging.FirebaseMessaging
import net.sukadigital.telemarketing.R
import net.sukadigital.telemarketing.activity.change_password.ChangePasswordActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.sukadigital.telemarketing.PrefManager
import net.sukadigital.telemarketing.activity.login.LoginActivity


class MainActivity : AppCompatActivity() {
    companion object{
        const val FINISHING_ACTIVITY = 9
    }
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: TabAdapter
    private lateinit var prefManager: PrefManager
    private var isFirstBackPressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Telemarketing"
        tabLayout = tablayout_main
        viewPager = viewpager_main
        prefManager = PrefManager(this);

        if (prefManager.accessToken == "") {
            startActivity(Intent(this, LoginActivity::class.java));
            this.finish();
            return;
        }

//        initFirebase()
        initViewPager()
    }

//    private fun initFirebase() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.d("firebase", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
//            Log.d("firebase", token)
////            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
//        })
//
//    }

    fun initViewPager() {
        tabLayout.addTab(tabLayout.newTab().setText("Call"))
        tabLayout.addTab(tabLayout.newTab().setText("Chat"))
        adapter = TabAdapter(supportFragmentManager)
        adapter.addFragment(UserCallFragment(), "Call")
        adapter.addFragment(ChatFragment(), "Chat")
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_main, menu)
        if (menu != null) {
            val delete = menu.findItem(R.id.optionMain_changePass)
            delete.icon.setColorFilter(
                resources.getColor(R.color.cyan),
                PorterDuff.Mode.SRC_ATOP
            )
            val spanString = SpannableString(delete.title.toString())
            spanString.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.black)),
                0,
                spanString.length,
                0
            ) //fix the color to white
            delete.setTitle(spanString)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.optionMain_changePass) {
            startActivity(Intent(this, ChangePasswordActivity::class.java));
        }

        if (item.itemId == R.id.optionMain_logout) {
            prefManager.logout();
            startActivity(Intent(this, LoginActivity::class.java));
            this.finish();
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val mainIntent = Intent(Intent.ACTION_DIAL, null)
        mainIntent.addCategory(Intent.CATEGORY_DEFAULT)
        val pkgAppsList = packageManager.queryIntentActivities(mainIntent, 0)
        val info = pkgAppsList[0].activityInfo
        if (supportFragmentManager.backStackEntryCount != 0) {
            super.onBackPressed()
        } else {
            if (getSystemService(TelecomManager::class.java).defaultDialerPackage == info.packageName) {
                this.finish()
            } else {
                Toast.makeText(this, "please restore default phone apps before exit", Toast.LENGTH_LONG).show()
                Log.d("dial", info.packageName)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    Handler().postDelayed({
                        startActivityForResult(
                            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
                            FINISHING_ACTIVITY
                        )
                    }, 2000)
                } else {
                    val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    intent.putExtra(
                        TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                        info.packageName
                    )
                    startActivity(intent)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == FINISHING_ACTIVITY){
            val mainIntent = Intent(Intent.ACTION_DIAL, null)
            mainIntent.addCategory(Intent.CATEGORY_DEFAULT)
            val pkgAppsList = packageManager.queryIntentActivities(mainIntent, 0)
            val info = pkgAppsList[0].activityInfo
            if (getSystemService(TelecomManager::class.java).defaultDialerPackage == info.packageName) {
                this.finish()
            }
        }
    }

}