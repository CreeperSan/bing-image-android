package com.creepersan.bingimage.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.creepersan.bingimage.R
import com.creepersan.bingimage.utils.getWebsiteLocalePostfix
import kotlinx.android.synthetic.main.activity_permission_description.*

class PermissionDescriptionActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_permission_description

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initWebView()
    }

    private fun initToolbar(){
        permissionDescriptionToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initWebView(){
        val settings = permissionDescriptionWebView.settings
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.javaScriptEnabled = true

        permissionDescriptionWebView.setBackgroundColor(Color.TRANSPARENT)
        permissionDescriptionWebView.loadUrl("file:///android_asset/web/permission${getWebsiteLocalePostfix()}.html")

    }

}