package com.creepersan.bingimage.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(){

    open val layoutID : Int = Int.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(layoutID != Int.MIN_VALUE){
            setContentView(layoutID)
        }
    }

}