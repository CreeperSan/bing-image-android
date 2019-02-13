package com.creepersan.bingimage.activity

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
        initTest()
    }

    private fun initTest(){
        Retrofit.Builder()
            .baseUrl("http://bing.creepersan.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BingUrlRequest::class.java)
            .getBingUrl(3)
            .enqueue(object : Callback<BingUrlResponse> {
                override fun onFailure(call: Call<BingUrlResponse>, t: Throwable) {
                    infoText.text = "连接失败"
                }
                override fun onResponse(call: Call<BingUrlResponse>, response: Response<BingUrlResponse>) {
                    if (response.isSuccessful){
                        val strBuilder = StringBuilder()
                        response.body()?.apply {
                            this.data?.imgList?.forEach {
                                strBuilder
                                    .append(it.title).appendln()
                                    .append(it.location).appendln()
                                    .append(it.author).appendln()
                                    .appendln()
                            }
                        }
                        infoText.text = strBuilder.toString()
                    }else{
                        infoText.text = "获取失败"
                    }
                }
            })
    }

}