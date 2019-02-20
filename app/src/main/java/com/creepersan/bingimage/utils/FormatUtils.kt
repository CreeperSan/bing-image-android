package com.creepersan.bingimage.utils

import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage
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

fun Int.toResolution():Resolution{
    return when(this){
        Resolution.L_1920_1200.value -> Resolution.L_1920_1200
        Resolution.L_1920_1080.value -> Resolution.L_1920_1080
        Resolution.L_1366_768 .value -> Resolution.L_1366_768
        Resolution.L_1280_720 .value -> Resolution.L_1280_720
        Resolution.L_1024_768 .value -> Resolution.L_1024_768
        Resolution.L_800_600  .value -> Resolution.L_800_600
        Resolution.L_800_480  .value -> Resolution.L_800_480
        Resolution.L_640_480  .value -> Resolution.L_640_480
        Resolution.L_400_240  .value -> Resolution.L_400_240
        Resolution.L_320_240  .value -> Resolution.L_320_240
        Resolution.P_1080_1920.value -> Resolution.P_1080_1920
        Resolution.P_768_1366 .value -> Resolution.P_768_1366
        Resolution.P_768_1280 .value -> Resolution.P_768_1280
        Resolution.P_720_1280 .value -> Resolution.P_720_1280
        Resolution.P_480_800  .value -> Resolution.P_480_800
        Resolution.P_480_640  .value -> Resolution.P_480_640
        Resolution.P_240_400  .value -> Resolution.P_240_400
        Resolution.P_240_320  .value -> Resolution.P_240_320
        else -> Resolution.UNDEFINE
    }
}

fun Resolution.getWidth():Int{
    return when(this){
        Resolution.L_1920_1200 -> 1920
        Resolution.L_1920_1080 -> 1920
        Resolution.L_1366_768  -> 1366
        Resolution.L_1280_720  -> 1280
        Resolution.L_1024_768  -> 1024
        Resolution.L_800_600   -> 800
        Resolution.L_800_480   -> 800
        Resolution.L_640_480   -> 640
        Resolution.L_400_240   -> 400
        Resolution.L_320_240   -> 320
        Resolution.P_1080_1920 -> 1080
        Resolution.P_768_1366  -> 768
        Resolution.P_768_1280  -> 768
        Resolution.P_720_1280  -> 720
        Resolution.P_480_800   -> 480
        Resolution.P_480_640   -> 480
        Resolution.P_240_400   -> 240
        Resolution.P_240_320   -> 240
        else -> 100
    }
}

fun Resolution.getHeight():Int{
    return when(this){
        Resolution.L_1920_1200 -> 1200
        Resolution.L_1920_1080 -> 1080
        Resolution.L_1366_768  -> 768
        Resolution.L_1280_720  -> 720
        Resolution.L_1024_768  -> 768
        Resolution.L_800_600   -> 600
        Resolution.L_800_480   -> 480
        Resolution.L_640_480   -> 480
        Resolution.L_400_240   -> 240
        Resolution.L_320_240   -> 240
        Resolution.P_1080_1920 -> 1920
        Resolution.P_768_1366  -> 1366
        Resolution.P_768_1280  -> 1280
        Resolution.P_720_1280  -> 1280
        Resolution.P_480_800   -> 800
        Resolution.P_480_640   -> 640
        Resolution.P_240_400   -> 400
        Resolution.P_240_320   -> 320
        else -> 100
    }
}

fun String.isInt():Boolean{
    return try {
        this.toInt()
        true
    }catch (e:NumberFormatException){
        false
    }
}


















