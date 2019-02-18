package com.creepersan.bingimage.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.widget.ImageView
import com.creepersan.bingimage.database.bean.BingImage

class PreviewModel : ViewModel() {

    var isInit = MutableLiveData<Boolean>()
    var bingImage = MutableLiveData<BingImage?>()
    var img = MutableLiveData<Bitmap?>()
    var previewResolution = MutableLiveData<BingImage.Resolution>()
    var downloadResolution = MutableLiveData<BingImage.Resolution>()
    var imageScale = MutableLiveData<ImageView.ScaleType>()
    var isDownloadDialogDefaultNotDisplay = MutableLiveData<Boolean>()

    init {
        isInit.value = false
        imageScale.value = ImageView.ScaleType.CENTER_CROP
        isDownloadDialogDefaultNotDisplay.value = false
    }

    fun getPreviewResolution():BingImage.Resolution{
        return previewResolution.value ?: BingImage.Resolution.P_1080_1920
    }

    fun getDownloadResolution():BingImage.Resolution{
        return downloadResolution.value ?: BingImage.Resolution.UNDEFINE
    }

    fun getImageScaleType():ImageView.ScaleType{
        return imageScale.value ?: ImageView.ScaleType.CENTER_CROP
    }

    fun getPreviewImageUrl():String{
        return bingImage.value?.getImageUrl(previewResolution.value ?: BingImage.Resolution.P_1080_1920) ?: ""
    }


}