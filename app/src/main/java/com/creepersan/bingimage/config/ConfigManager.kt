package com.creepersan.bingimage.config

import android.content.Context
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.database.bean.BingImage.Resolution
import com.creepersan.bingimage.utils.toResolution

class ConfigManager(context: Context){

    private val mAppConfig = context.getSharedPreferences(NAME_APP, Context.MODE_PRIVATE)
    private val mPreviewConfig = context.getSharedPreferences(NAME_PREVIEW, Context.MODE_PRIVATE)

    companion object {
        private const val NAME_APP = "BingImage.pref"


        private const val NAME_PREVIEW = "Preview.pref"
        private const val KEY_PREVIEW_RESOLUTION = "preview_resolution"
        private const val KEY_DOWNLOAD_RESOLUTION = "download_resolution"
        private const val KEY_LIST_RESOLUTION = "list_resolution"
        private const val KEY_DOWNLOAD_DIALOG_DEFAULT_NOT_DISPLAY = "download_dialog_default_not_display"
    }

    /* Preview */
    fun getPreviewResolution():Resolution{
        return mPreviewConfig.getInt(KEY_PREVIEW_RESOLUTION, Resolution.UNDEFINE.value).toResolution()
    }
    fun setPreviewResolution(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_PREVIEW_RESOLUTION, resolution.value).apply()
    }
    fun getDownloadResolution():Resolution{
        return mPreviewConfig.getInt(KEY_DOWNLOAD_RESOLUTION, Resolution.UNDEFINE.value).toResolution()
    }
    fun setDownloadResolution(resolution:Resolution){
        mPreviewConfig.edit().putInt(KEY_DOWNLOAD_RESOLUTION, resolution.value).apply()
    }
    fun getListResolution():Resolution{
        return mPreviewConfig.getInt(KEY_LIST_RESOLUTION, Resolution.UNDEFINE.value).toResolution()
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