package com.creepersan.bingimage.application

import android.app.Application
import android.arch.persistence.room.Room
import com.creepersan.bingimage.database.BingImageDatabase
import com.creepersan.bingimage.database.DB_BINGIMAGE

class BingImageApplication : Application() {

    private val mBindImageDatabase by lazy { Room.databaseBuilder(applicationContext, BingImageDatabase::class.java, DB_BINGIMAGE).build() }

    override fun onCreate() {
        super.onCreate()
    }

    fun getBingImageDatabase():BingImageDatabase{
        return mBindImageDatabase
    }

}