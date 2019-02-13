package com.creepersan.bingimage.network.response

class BingUrlImageItemBean{
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
}

class BingUrlDataBean{
    var itemCount:Int = 0
    var imgList: ArrayList<BingUrlImageItemBean>? = null
}

class BingUrlResponse{
    var flag:Int = 0
    var data : BingUrlDataBean? = null
}