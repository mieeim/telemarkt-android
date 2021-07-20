package net.sukadigital.telemarketing.activity.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import net.sukadigital.telemarketing.R
import kotlinx.android.synthetic.main.activity_chat_webview.*


class ChatWebviewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_webview)
        var url = "https://ssdi.co.id/"
        intent.getStringExtra("url")?.let {
            url = it
        }
        Log.d("url webview", url)
        webView = chatWebview
        webView.clearCache(true)
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.domStorageEnabled = true
        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
    }
}