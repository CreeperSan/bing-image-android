package com.creepersan.bingimage.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.creepersan.bingimage.application.BingImageApplication

@GlideModule
class DiskCacheModule : AppGlideModule(){


    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val cacheSize = 1024L*1024*(BingImageApplication.getInstacne().config.getCacheSize())
        val cacheFolder = BingImageApplication.getInstacne().getFileManager().cacheFolder
        builder.setDiskCache(DiskLruCacheFactory(cacheFolder.path, cacheSize))
    }

}