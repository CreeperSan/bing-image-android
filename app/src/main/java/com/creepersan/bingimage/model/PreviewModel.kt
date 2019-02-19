package com.creepersan.bingimage.model

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.widget.ImageView
import com.creepersan.bingimage.application.BingImageApplication
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.database.bean.BingImage.Resolution

class PreviewModel : ViewModel() {

    var isInit = MutableLiveData<Boolean>()
    var bingImage = MutableLiveData<BingImage?>()
    var img = MutableLiveData<Bitmap?>()
    private var previewResolution = MutableLiveData<Resolution>()
    private var downloadResolution = MutableLiveData<Resolution>()
    var imageScale = MutableLiveData<ImageView.ScaleType>()
    private var isDownloadDialogDefaultNotDisplay = MutableLiveData<Boolean>()

     private val config = BingImageApplication.getInstacne().config

    init {

        isInit.value = false
        imageScale.value = ImageView.ScaleType.CENTER_CROP
        previewResolution.value = config.getPreviewResolution()
        downloadResolution.value = config.getDownloadResolution()
        isDownloadDialogDefaultNotDisplay.value = config.isDialogDefaultNotDisplay()
    }

    fun getPreviewResolution():Resolution{
        return previewResolution.value!!
    }
    fun setPreviewResolution(resolution: Resolution){
        previewResolution.value = resolution
        config.setPreviewResolution(resolution)
    }
    fun observerPreviewResolution(owner:LifecycleOwner, observer: Observer<Resolution>){
        previewResolution.observe(owner, observer)
    }

    fun getDownloadResolution():Resolution{
        return downloadResolution.value!!
    }
    fun setDownloadResolution(resolution: Resolution){
        downloadResolution.value = resolution
        config.setDownloadResolution(resolution)
    }

    fun getImageScaleType():ImageView.ScaleType{
        return imageScale.value ?: ImageView.ScaleType.CENTER_CROP
    }
    fun getPreviewImageUrl():String{
        return bingImage.value?.getImageUrl(previewResolution.value!!) ?: ""
    }

    fun isDownloadDialogDefaultNotDisplay():Boolean{
        return isDownloadDialogDefaultNotDisplay.value ?: config.isDialogDefaultNotDisplay()
    }
    fun setDownloadDialogDefaultNotDisplay(state:Boolean){
        isDownloadDialogDefaultNotDisplay.value = state
        config.setDialogDefaultNotDisplay(state)
    }
}