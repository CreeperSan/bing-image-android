package com.creepersan.bingimage.activity

import android.app.Activity
import android.arch.lifecycle.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.creepersan.bingimage.R
import com.creepersan.bingimage.network.request.BingUrlRequest
import com.creepersan.bingimage.network.response.BingUrlResponse
import kotlinx.android.synthetic.main.activity_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InfoActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initWebView()
    }

    private fun initToolbar(){
        infoToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initWebView(){
        val settings = infoWebView.settings
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.javaScriptEnabled = true

        infoWebView.setBackgroundColor(Color.TRANSPARENT)
        infoWebView.loadUrl("file:///android_asset/web/info.html")

        infoWebView.webViewClient = object :WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val appName = R.string.app_name.toResString()
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val appVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    "${packageInfo.versionName} ( ${packageInfo.longVersionCode} )"
                }else{
                    "${packageInfo.versionName} ( ${packageInfo.versionCode} )"
                }
                infoWebView.evaluateJavascript("setAppName('$appName')", null)
                infoWebView.evaluateJavascript("setAppVersion('$appVersion')", null)
            }
        }


    }

}
