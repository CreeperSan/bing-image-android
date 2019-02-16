package com.creepersan.bingimage.database.bean

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
class BingImage {

    companion object {
        const val STATE_NONE = 0
        const val STATE_LIKE = 1
    }

    @PrimaryKey
    var date : Int = 0

    @ColumnInfo(name="year")
    var year:Int = 0

    @ColumnInfo(name="month")
    var month:Int = 0

    @ColumnInfo(name="day")
    var day:Int = 0

    @ColumnInfo(name="title")
    var title:String = ""

    @ColumnInfo(name="location")
    var location:String = ""

    @ColumnInfo(name="author")
    var author:String = ""

    @ColumnInfo(name="img_url")
    var img_url:String = ""

    @Ignore
    var state:Int = STATE_NONE

    fun getImageUrl(size:Resolution):String{
        return "http://bing.creepersan.com/api/v1/download/$date.jpg?size=${size.value}"
    }

    enum class Resolution(var value:Int){
        L_1920_1200(0),
        L_1920_1080(1),
        L_1366_768(2),
        L_1280_720(3),
        L_1024_768(4),
        L_800_600(5),
        L_800_480(6),
        L_640_480(7),
        L_400_240(8),
        L_320_240(9),
        P_1080_1920(10),
        P_768_1366(11),
        P_768_1280(12),
        P_720_1280(13),
        P_480_800(14),
        P_480_640(15),
        P_240_400(16),
        P_240_320(17)

    }
}

