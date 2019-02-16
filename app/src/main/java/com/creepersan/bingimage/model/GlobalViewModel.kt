package com.creepersan.bingimage.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.network.request.BingUrlRequest
import com.creepersan.bingimage.network.response.BingUrlResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.ArrayList

class GlobalViewModel : ViewModel() {

    var imageList : MutableLiveData<ArrayList<BingImage>> = MutableLiveData()

    fun getImage(retrofit: Retrofit, onResponse:(Call<BingUrlResponse>,Response<BingUrlResponse>)->Unit, onFailure:(Call<BingUrlResponse>,Throwable)->Unit){
        retrofit.create(BingUrlRequest::class.java)
            .getBingUrl()
            .enqueue(object : Callback<BingUrlResponse>{
                override fun onFailure(call: Call<BingUrlResponse>, t: Throwable) {
                    onFailure.invoke(call, t)
                }

                override fun onResponse(call: Call<BingUrlResponse>, response: Response<BingUrlResponse>) {
                    onResponse.invoke(call, response)
                }

            })
    }


}