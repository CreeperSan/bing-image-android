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

class MainModel : ViewModel() {

    var imageList : MutableLiveData<ArrayList<BingImage>> = MutableLiveData()
    private var isAlreadyInitNetwork = MutableLiveData<Boolean>()
    private var maxItemCount = MutableLiveData<Int>()
    private var page = MutableLiveData<Int>()
    private var pageCount = MutableLiveData<Int>()
    var isLoadFinish = MutableLiveData<Boolean>()

    init {
        isAlreadyInitNetwork.value = false
        maxItemCount.value = Int.MAX_VALUE
        page.value = 1
        pageCount.value = 12
        isLoadFinish.value = false
    }

    fun getImage(retrofit: Retrofit, imageCount:Int=12, page:Int=1, onResponse:(Call<BingUrlResponse>,Response<BingUrlResponse>)->Unit, onFailure:(Call<BingUrlResponse>,Throwable)->Unit){
        retrofit.create(BingUrlRequest::class.java)
            .getBingUrl(imageCount, page)
            .enqueue(object : Callback<BingUrlResponse>{
                override fun onFailure(call: Call<BingUrlResponse>, t: Throwable) {
                    onFailure.invoke(call, t)
                }

                override fun onResponse(call: Call<BingUrlResponse>, response: Response<BingUrlResponse>) {
                    onResponse.invoke(call, response)
                }

            })
    }

    fun isAlreadyInitNetwork():Boolean{
        return true == isAlreadyInitNetwork.value
    }

    fun setAlreadyInitNetwork(){
        isAlreadyInitNetwork.value = true
    }

    fun setMaxItemCount(int:Int){
        maxItemCount.value = int
    }

    fun getMaxItemCount():Int{
        return maxItemCount.value ?: Int.MAX_VALUE
    }

    fun setPage(int:Int){
        page.value = int
    }

    fun getPage():Int{
        return page.value ?: 1
    }

    fun setPageCount(int:Int){
        pageCount.value = int
    }

    fun getPageCount():Int{
        return pageCount.value ?: 12
    }

    fun addOrReplaceBingImage(bingImage: BingImage){
        if (null == imageList.value){ /////////////////////
            val tmpList = ArrayList<BingImage>()
            tmpList.add(bingImage)
            imageList.value = tmpList
        }else{ ////////////////////////////////////////////
            imageList.value?.forEachIndexed { index, item -> // 尝试寻找是否为重复的
                if (item.date == bingImage.date){
                    imageList.value?.set(index, bingImage)
                    return
                }
            }
            // 没找到重复的，那么就插入
            imageList.value?.forEachIndexed { index, item ->
                if (index+1 >= imageList.value?.size ?: 0){ //
                    imageList.value?.add(bingImage)
                    return
                }else{
                    val currentDate = item.date
                    val nextDate = imageList.value?.get(index+1)?.date ?: Int.MAX_VALUE
                    if (bingImage.date in (currentDate + 1)..(nextDate - 1)){
                        imageList.value?.add(index+1, bingImage)
                    }
                }
            }
        }
    }

    fun isLoadAllDataFinish():Boolean{
        return isLoadFinish.value ?: false
    }

}