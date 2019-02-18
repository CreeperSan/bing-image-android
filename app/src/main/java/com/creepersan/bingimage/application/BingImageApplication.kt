package com.creepersan.bingimage.application

import android.app.Application
import android.arch.persistence.room.Room
import com.creepersan.bingimage.database.BingImageDatabase
import com.creepersan.bingimage.database.DB_BINGIMAGE
import com.creepersan.bingimage.file.FileManager

class BingImageApplication : Application() {

    private val mBindImageDatabase by lazy { Room.databaseBuilder(applicationContext, BingImageDatabase::class.java, DB_BINGIMAGE).build() }
    private val mFileManager by lazy { FileManager() }

    override fun onCreate() {
        super.onCreate()
    }

    fun getFileManager():FileManager{
        return mFileManager
    }

    fun getBingImageDatabase():BingImageDatabase{
        return mBindImageDatabase
    }

}