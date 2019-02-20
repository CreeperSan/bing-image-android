package com.creepersan.bingimage.model

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.creepersan.bingimage.activity.GalleryActivity
import java.io.File
import java.util.ArrayList
import com.creepersan.bingimage.activity.GalleryActivity.GalleryItem

class GalleryModel : ViewModel() {

    private var mGalleryList = MutableLiveData<ArrayList<GalleryItem>>()
    private var isInit = MutableLiveData<Boolean>()
    private var mViewPos = MutableLiveData<Int>()

    init {
        mGalleryList.value = ArrayList()
        isInit.value = false
        mViewPos.value = 0
    }

    fun getGalleryList():ArrayList<GalleryItem>{
        return mGalleryList.value!!
    }

    fun setGalleryList(list:ArrayList<GalleryItem>){
        mGalleryList.value = list
    }

    fun isInitData():Boolean{
        return  isInit.value ?: false
    }

    fun setInitData(){
        isInit.value = true
    }

    fun getViewPos():Int{
        return mViewPos.value ?: 0
    }

    fun setViewPos(pos:Int){
        mViewPos.value = pos
    }

    fun observerFileList(owner: LifecycleOwner, observer: Observer<ArrayList<GalleryItem>>){
        mGalleryList.observe(owner, observer)
    }



    fun deleteGalleryItem(item: GalleryActivity.GalleryItem){
        mGalleryList.value?.remove(item)
        mGalleryList.value = mGalleryList.value

        item.file.delete()
    }
}