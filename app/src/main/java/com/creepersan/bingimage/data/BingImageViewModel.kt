package com.creepersan.bingimage.data

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.creepersan.bingimage.database.bean.BingImage

class BingImageViewModel : ViewModel(){

    var bingImageList = MutableLiveData<ArrayList<BingImage>>()

    init {
        // 获取数据
        val imageList = ArrayList<BingImage>()
        bingImageList.value = imageList
    }


}