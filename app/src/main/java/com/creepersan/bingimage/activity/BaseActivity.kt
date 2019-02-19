package com.creepersan.bingimage.activity

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.creepersan.bingimage.application.BingImageApplication
import com.creepersan.bingimage.network.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

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
    val config by lazy { application.config }

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


    /* start Activity */
    fun <T:BaseActivity> toActivity(clazz:Class<T>, isFinish:Boolean = false, vararg pair: Pair<String, Any>){
        val intent = Intent(this, clazz)
        pair.forEach {
            when(it.second){
                is Boolean      -> intent.putExtra(it.first, it.second as Boolean      )
                is Byte         -> intent.putExtra(it.first, it.second as Byte         )
                is Char         -> intent.putExtra(it.first, it.second as Char         )
                is Short        -> intent.putExtra(it.first, it.second as Short        )
                is Int          -> intent.putExtra(it.first, it.second as Int          )
                is Long         -> intent.putExtra(it.first, it.second as Long         )
                is Float        -> intent.putExtra(it.first, it.second as Float        )
                is Double       -> intent.putExtra(it.first, it.second as Double       )
                is String       -> intent.putExtra(it.first, it.second as String       )
                is CharSequence -> intent.putExtra(it.first, it.second as CharSequence )
                is Parcelable   -> intent.putExtra(it.first, it.second as Parcelable   )
                is Serializable -> intent.putExtra(it.first, it.second as Serializable )
                is BooleanArray -> intent.putExtra(it.first, it.second as BooleanArray )
                is ByteArray    -> intent.putExtra(it.first, it.second as ByteArray    )
                is ShortArray   -> intent.putExtra(it.first, it.second as ShortArray   )
                is CharArray    -> intent.putExtra(it.first, it.second as CharArray    )
                is IntArray     -> intent.putExtra(it.first, it.second as IntArray     )
                is LongArray    -> intent.putExtra(it.first, it.second as LongArray    )
                is FloatArray   -> intent.putExtra(it.first, it.second as FloatArray   )
                is DoubleArray  -> intent.putExtra(it.first, it.second as DoubleArray  )
                is Bundle       -> intent.putExtra(it.first, it.second as Bundle       )
            }
        }
        startActivity(intent)
        if (isFinish){
            finish()
        }
    }


    protected fun Int.toResString():String{
        return getString(this)
    }

}