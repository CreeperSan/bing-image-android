package com.creepersan.bingimage.network.response

import com.creepersan.bingimage.database.bean.BingImage
import java.util.ArrayList

class BingImageItemBean{
    var date:Int = 20000000
    var year:Int = 0
    var month:Int = 0
    var day:Int = 0
    var title:String = ""
    var location:String = ""
    var author:String = ""
    var img_url:String = ""
    var img_url_thumbnail:String = ""
    var img_url_base:String = ""

    fun toBingImage(state:Int=BingImage.STATE_NONE):BingImage{
        val item = BingImage()
        item.date = date
        item.year = year
        item.month = month
        item.day = day
        item.title = title
        item.location = location
        item.author = author
        item.img_url = img_url_base
        item.state = state
        return item
    }
}

class BingUrlDataBean{
    var itemCount:Int = 0
    var imgList: ArrayList<BingImageItemBean>? = null
}

/* 请求api/v1/url */

class BingUrlResponse{
    var flag:Int = 0
    var data : BingUrlDataBean? = null
}

/* 请求api/v1/random */

class BingRandomResponse{
    var flag:Int = 0
    var data:ArrayList<BingImageItemBean>? = null
}