package com.creepersan.bingimage.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.creepersan.bingimage.application.BingImageApplication
import com.creepersan.bingimage.network.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseActivity : AppCompatActivity(){

    open val layoutID : Int = Int.MIN_VALUE

    val rootView by lazy { findViewById<View>(android.R.id.content) }

    val application by lazy { getApplication() as BingImageApplication }
    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val fileManager by lazy { application.getFileManager() }
    private val mSnackBar by lazy { Snackbar.make(rootView,"",Snackbar.LENGTH_SHORT) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(layoutID != Int.MIN_VALUE){
            setContentView(layoutID)
        }
    }

    fun toast(content:String){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }
    fun snack(content:CharSequence, length:Int=Snackbar.LENGTH_SHORT, actionName:CharSequence="", action:View.OnClickListener?=null){
        mSnackBar.setText(content)
        mSnackBar.setAction(actionName, action)
        mSnackBar.duration = length
        mSnackBar.show()
    }
    fun snackHide(){
        mSnackBar.dismiss()
    }




    protected fun Int.toResString():String{
        return getString(this)
    }

}