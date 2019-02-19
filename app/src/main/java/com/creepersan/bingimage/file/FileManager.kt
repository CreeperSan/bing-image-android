package com.creepersan.bingimage.file

import android.os.Environment
import com.creepersan.bingimage.database.bean.BingImage
import java.io.File

class FileManager {

    val rootFolder by lazy { File("${Environment.getExternalStorageDirectory().path}/BingImage") }
    val cacheFolder by lazy { File("${rootFolder.path}/cache") }
    val downloadFolder by lazy { File("${rootFolder.path}/download") }

    fun initFolderPath():Boolean{
        return rootFolder.initFolder() && cacheFolder.initFolder() && downloadFolder.initFolder()
    }

    private fun File.initFolder():Boolean{
        return if (this.exists()){
            this.isDirectory
        }else{
            this.mkdirs()
        }
    }

    private fun getCacheImageFile(date:Int, resolution:BingImage.Resolution):File{
        return File("${cacheFolder.path}/$date/${resolution.value}")
    }

}