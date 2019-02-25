package com.creepersan.bingimage.model

import android.arch.lifecycle.*
import com.creepersan.bingimage.database.bean.BingImage
import java.lang.Exception
import java.util.ArrayList

class RandomModel : ViewModel(){

    private var mImageData = MutableLiveData<ArrayList<BingImage>>()
    private var mIsInitData = MutableLiveData<Boolean>()

    init {
        mImageData.value = ArrayList()
        mIsInitData.value = false
    }

    fun getDataSize():Int{
        return mImageData.value?.size ?: 0
    }

    fun getData(pos:Int):BingImage?{
        return try {
            mImageData.value?.get(pos)
        }catch (e:Exception){
            null
        }
    }

    fun setImageData(data:ArrayList<BingImage>){
        mImageData.value = data
    }

    fun setInitData(isInit:Boolean){
        mIsInitData.value = isInit
    }

    fun isInitData():Boolean{
        return mIsInitData.value ?: false
    }

    fun observeImageData(owner:LifecycleOwner, observer: Observer<ArrayList<BingImage>>){
        mImageData.observe(owner, observer)
    }

}