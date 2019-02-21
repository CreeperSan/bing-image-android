package com.creepersan.bingimage.utils

import android.content.Context
import java.util.*

fun getWebsiteLocalePostfix():String{
    return when(Locale.getDefault()){
        Locale.CHINA,
        Locale.CHINESE,
        Locale.SIMPLIFIED_CHINESE -> "_zh_cn"
        Locale.TAIWAN,
        Locale.TRADITIONAL_CHINESE -> "_zh_tw"
        Locale.JAPAN,
        Locale.JAPANESE,
        Locale.TRADITIONAL_CHINESE -> "_ja"
        else -> ""
    }
}

