package com.creepersan.bingimage.utils

import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage.Resolution
import java.util.ArrayList

fun <T> List<T>.toArrayList():ArrayList<T>{
    return ArrayList(this)
}

fun Resolution.toResolutionStringID():Int{
    return when(this){
        Resolution.L_1920_1200 -> R.string.previewResolution1920x1200
        Resolution.L_1920_1080 -> R.string.previewResolution1920x1080
        Resolution.L_1366_768 -> R.string.previewResolution1366x768
        Resolution.L_1280_720 -> R.string.previewResolution1280x720
        Resolution.L_1024_768 -> R.string.previewResolution1024x768
        Resolution.L_800_600 -> R.string.previewResolution800x600
        Resolution.L_800_480 -> R.string.previewResolution800x480
        Resolution.L_640_480 -> R.string.previewResolution640x480
        Resolution.L_400_240 -> R.string.previewResolution400x240
        Resolution.L_320_240 -> R.string.previewResolution320x240
        Resolution.P_1080_1920 -> R.string.previewResolution1080x1920
        Resolution.P_768_1366 -> R.string.previewResolution768x1366
        Resolution.P_768_1280 -> R.string.previewResolution768x1280
        Resolution.P_720_1280 -> R.string.previewResolution720x1280
        Resolution.P_480_800 -> R.string.previewResolution480x800
        Resolution.P_480_640 -> R.string.previewResolution480x640
        Resolution.P_240_400 -> R.string.previewResolution240x400
        Resolution.P_240_320 -> R.string.previewResolution240x320
        else -> R.string.previewResolutionUnknown
    }
}


















