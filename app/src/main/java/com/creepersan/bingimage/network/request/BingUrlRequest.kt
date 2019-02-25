package com.creepersan.bingimage.network.request

import com.creepersan.bingimage.network.response.BingRandomResponse
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
    fun getBingUrl(@Query("count") count:Int=12, @Query("page") page:Int=12):Call<BingUrlResponse>

    @GET("/api/v1/random")
    fun getBingRandom(@Query("count") count:Int=3):Call<BingRandomResponse>

    @Streaming
    @GET("api/v1/download/{date}.jpg")
    fun download(@Path("date") date:Int, @Query("size") size:Int):Call<ResponseBody>


}