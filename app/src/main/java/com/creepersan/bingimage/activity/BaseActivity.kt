package com.creepersan.bingimage.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.creepersan.bingimage.application.BingImageApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseActivity : AppCompatActivity(){

    open val layoutID : Int = Int.MIN_VALUE

    val application by lazy { getApplication() as BingImageApplication }
    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://bing.creepersan.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(layoutID != Int.MIN_VALUE){
            setContentView(layoutID)
        }
    }

    fun toast(content:String){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

}