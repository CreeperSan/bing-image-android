package com.creepersan.bingimage.activity

import android.content.Intent
import android.os.Bundle
import com.creepersan.bingimage.R

class BootActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_boot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

}