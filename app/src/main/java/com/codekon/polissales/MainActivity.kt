package com.codekon.polissales

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var errorLayout: LinearLayout
    private lateinit var btnRefresh: Button
    private val url = "https://polissales.ru"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        errorLayout = findViewById(R.id.errorLayout)
        btnRefresh = findViewById(R.id.btnRefresh)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    showError()
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (!isNetworkAvailable()) {
                    showError()
                }
            }
        }

        btnRefresh.setOnClickListener {
            if (isNetworkAvailable()) {
                hideError()
                webView.reload()
            }
        }

        if (isNetworkAvailable()) {
            webView.loadUrl(url)
        } else {
            showError()
        }
    }

    private fun showError() {
        webView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    private fun hideError() {
        webView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onBackPressed() {
        if (webView.canGoBack() && webView.visibility == View.VISIBLE) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
