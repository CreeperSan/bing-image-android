package com.creepersan.bingimage.config

import android.annotation.SuppressLint
import android.content.Context
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.database.bean.BingImage.Resolution
import com.creepersan.bingimage.utils.toResolution

class ConfigManager(context: Context){

    private val mAppConfig = context.getSharedPreferences(NAME_APP, Context.MODE_PRIVATE)
    private val mPreviewConfig = context.getSharedPreferences(NAME_PREVIEW, Context.MODE_PRIVATE)

    companion object {
        private const val NAME_APP = "BingImage.pref"
        private const val KEY_DOUBLE_CLICK_EXIT = "double_click_exit"
        private const val KEY_CACHE_SIZE = "cache_size"

        private const val NAME_PREVIEW = "Preview.pref"
        private const val KEY_PREVIEW_RESOLUTION = "preview_resolution"
        private const val KEY_DOWNLOAD_RESOLUTION = "download_resolution"
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
    fun getDownloadResolution():Resolution{
        return mPreviewConfig.getInt(KEY_DOWNLOAD_RESOLUTION, Resolution.L_1920_1080.value).toResolution()
    }
    fun setDownloadResolution(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_DOWNLOAD_RESOLUTION, resolution.value).apply()
    }
    fun setDownloadResolutionSync(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_DOWNLOAD_RESOLUTION, resolution.value).commit()
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