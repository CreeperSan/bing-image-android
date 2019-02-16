package com.creepersan.bingimage.activity

import android.app.Activity
import android.arch.lifecycle.*
import android.os.Bundle
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


    }


}
