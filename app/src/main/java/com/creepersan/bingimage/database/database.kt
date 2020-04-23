package com.creepersan.bingimage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.database.dao.BingImageDao


@Database(entities = [BingImage::class], version = 1)
abstract class BingImageDatabase : RoomDatabase(){

    abstract fun bingImageDao():BingImageDao

}

