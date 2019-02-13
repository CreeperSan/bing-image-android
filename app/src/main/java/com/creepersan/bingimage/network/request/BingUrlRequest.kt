package com.creepersan.bingimage.network.request

import com.creepersan.bingimage.network.response.BingUrlResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File

interface BingUrlRequest{

    @GET("/api/v1/url")
    fun getBingUrl(@Query("count") count:Int=12):Call<BingUrlResponse>



}