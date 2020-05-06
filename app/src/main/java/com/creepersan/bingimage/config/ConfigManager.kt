package com.creepersan.bingimage.config

import android.annotation.SuppressLint
import android.content.Context
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.database.bean.BingImage.Resolution
import com.creepersan.bingimage.utils.toResolution
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.ArrayList

class ConfigManager(context: Context){

    private val mAppConfig = context.getSharedPreferences(NAME_APP, Context.MODE_PRIVATE)
    private val mPreviewConfig = context.getSharedPreferences(NAME_PREVIEW, Context.MODE_PRIVATE)

    companion object {
        private const val NAME_APP = "BingImage.pref"
        private const val KEY_DOUBLE_CLICK_EXIT = "double_click_exit"
        private const val KEY_CACHE_SIZE = "cache_size"
        private const val KEY_USE_OLD_GALLERY = "use_old_gallery"

        private const val NAME_PREVIEW = "Preview.pref"
        private const val KEY_PREVIEW_RESOLUTION = "preview_resolution"
        @Deprecated("v1.0.0弃用，使用 KEY_DOWNLOAD_RESOLUTION_ARRAY 代替")
        private const val KEY_DOWNLOAD_RESOLUTION = "download_resolution"
        private const val KEY_DOWNLOAD_RESOLUTION_ARRAY = "download_resolution_array"
        private const val KEY_LIST_RESOLUTION = "list_resolution"
        private const val KEY_DOWNLOAD_DIALOG_DEFAULT_NOT_DISPLAY = "download_dialog_default_not_display"
    }

    /* Basic */
    fun isDoubleClickExit():Boolean{
        return mAppConfig.getBoolean(KEY_DOUBLE_CLICK_EXIT, true)
    }
    fun setDoubleClickExit(state:Boolean){
        mAppConfig.edit().putBoolean(KEY_DOUBLE_CLICK_EXIT, state).apply()
    }
    fun getCacheSize():Int{
        return mAppConfig.getInt(KEY_CACHE_SIZE, 512)
    }
    fun setCacheSize(size:Int){ 
        mAppConfig.edit().putInt(KEY_CACHE_SIZE, size).apply()
    }
    fun isUseOldGallery():Boolean{
        return mAppConfig.getBoolean(KEY_USE_OLD_GALLERY, false)
    }
    fun setUseOldGallery(isUseOldGallery: Boolean){
        mAppConfig.edit().putBoolean(KEY_USE_OLD_GALLERY, isUseOldGallery).apply()
    }

    /* Preview */
    fun getPreviewResolution():Resolution{
        return mPreviewConfig.getInt(KEY_PREVIEW_RESOLUTION, Resolution.L_1920_1080.value).toResolution()
    }
    fun setPreviewResolution(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_PREVIEW_RESOLUTION, resolution.value).apply()
    }
    fun setPreviewResolutionSync(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_PREVIEW_RESOLUTION, resolution.value).commit()
    }
    @Deprecated("已弃用，需要支持多选下载")
    fun getDownloadResolution():Resolution{
        return mPreviewConfig.getInt(KEY_DOWNLOAD_RESOLUTION, Resolution.L_1920_1080.value).toResolution()
    }
    @Deprecated("已弃用，需要支持多选")
    fun setDownloadResolution(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_DOWNLOAD_RESOLUTION, resolution.value).apply()
    }
    @Deprecated("已弃用，需要支持多选")
    fun setDownloadResolutionSync(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_DOWNLOAD_RESOLUTION, resolution.value).commit()
    }
    fun getDownloadResolutions():List<Resolution>{
        val resolutionList = ArrayList<Resolution>()
        val resolutionString = mPreviewConfig.getString(KEY_DOWNLOAD_RESOLUTION_ARRAY, Resolution.L_1920_1080.value.toString())
        if (resolutionString!=null && resolutionString.isNotEmpty() ){
            // 如果下载分辨率不为空
            try {
                if (resolutionString.contains(",")){
                    // 如果有多个下载分辨率
                    resolutionString.split(",").forEach { resolutionItemString ->
                        resolutionList.add(resolutionItemString.toInt().toResolution())
                    }
                }else{
                    // 如果只有一个下载分辨率
                    resolutionList.add(resolutionString.toInt().toResolution())
                }
            } catch (e: Exception) {
                resolutionList.add(Resolution.L_1920_1080)
            }
        }else{
            // 如果下载分辨率为空
            resolutionList.add(Resolution.L_1920_1080)
        }
        return resolutionList
    }
    fun setDownloadResolutions(resolutions: List<Resolution>){
        val iterator = resolutions.iterator()
        val stringBuilder = StringBuilder()
        while (iterator.hasNext()){
            val resolutionItem = iterator.next()
            stringBuilder.append(resolutionItem.value.toString())
            if (iterator.hasNext()){
                stringBuilder.append(",")
            }
        }

        mPreviewConfig.edit().putString(KEY_DOWNLOAD_RESOLUTION_ARRAY, stringBuilder.toString()).apply()
    }
    fun hasDownloadResolutions():Boolean{
        return mPreviewConfig.contains(KEY_DOWNLOAD_RESOLUTION_ARRAY)
    }
    fun getListResolution():Resolution{
        return mPreviewConfig.getInt(KEY_LIST_RESOLUTION, Resolution.L_400_240.value).toResolution()
    }
    fun setListResolution(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_LIST_RESOLUTION, resolution.value).apply()
    }
    fun isDialogDefaultNotDisplay():Boolean{
        return mPreviewConfig.getBoolean(KEY_DOWNLOAD_DIALOG_DEFAULT_NOT_DISPLAY, false)
    }
    fun setDialogDefaultNotDisplay(defaultNotDisplay:Boolean){
        mPreviewConfig.edit().putBoolean(KEY_DOWNLOAD_DIALOG_DEFAULT_NOT_DISPLAY, defaultNotDisplay).apply()
    }



}