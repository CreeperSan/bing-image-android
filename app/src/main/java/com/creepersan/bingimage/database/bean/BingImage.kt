package com.creepersan.bingimage.database.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.creepersan.bingimage.database.TB_BINGIMAGE
import com.creepersan.bingimage.network.BASE_URL
import java.io.Serializable

@Entity(tableName = TB_BINGIMAGE)
class BingImage : Serializable{

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
        return "$BASE_URL/api/v1/download/$date.jpg?size=${size.value}"
    }

    fun getTimeString():String{
        val dateStr = date.toString()
        if (dateStr.length == 8){
            return "${dateStr.substring(0,4)} - ${dateStr.substring(4,6)} - ${dateStr.substring(6,8)}"
        }else{
            return dateStr
        }
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
        P_240_320(17),
        UNDEFINE(Int.MAX_VALUE)
    }
}

