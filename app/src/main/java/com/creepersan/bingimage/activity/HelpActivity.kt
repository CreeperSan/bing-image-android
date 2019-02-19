package com.creepersan.bingimage.activity

import android.graphics.Color
import android.os.Bundle
import com.creepersan.bingimage.R
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_help

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initWebView()
    }

    private fun initToolbar(){
        helpToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initWebView(){
        val settings = helpWebView.settings
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true

        helpWebView.setBackgroundColor(Color.TRANSPARENT)
        helpWebView.loadUrl("file:///android_asset/web/help.html")
    }

}