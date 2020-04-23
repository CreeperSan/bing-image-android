package com.creepersan.bingimage.application

import android.app.Application
import androidx.room.Room
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.creepersan.bingimage.config.ConfigManager
import com.creepersan.bingimage.database.BingImageDatabase
import com.creepersan.bingimage.database.DB_BINGIMAGE
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.download.DownloadService
import com.creepersan.bingimage.file.FileManager
import com.creepersan.bingimage.utils.toResolutionStringID
import java.util.*

class BingImageApplication : Application(),ServiceConnection {

    override fun onServiceDisconnected(name: ComponentName?) {}

    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
        mServiceConnection = service as DownloadService.DownloadServiceController
        attachState = Downloader.STATE_ATTACH
        downloaderAttachCallback()
    }

    companion object {
        fun getInstacne():BingImageApplication{
            return instance!!
        }

        private var instance:BingImageApplication? = null
    }

    private val mBindImageDatabase by lazy { Room.databaseBuilder(applicationContext, BingImageDatabase::class.java, DB_BINGIMAGE).build() }
    private val mFileManager by lazy { FileManager() }
    private lateinit var mServiceConnection : DownloadService.DownloadServiceController
    private var attachState = Downloader.STATE_DISATTACH

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    fun getFileManager():FileManager{
        return mFileManager
    }

    fun getBingImageDatabase():BingImageDatabase{
        return mBindImageDatabase
    }


    /***
     *
     *  Config
     *
     */
    val config by lazy { ConfigManager(this) }

    /**
     *
     *  Downloader Service
     *
     */
    private object Downloader{
        const val STATE_DISATTACH = 0
        const val STATE_ATTACHING = 1
        const val STATE_ATTACH = 2

    }

    private data class BufferDownloadInfo(val bingImage: BingImage, val resolution: BingImage.Resolution)

    private val mBufferDownloadInfo = LinkedList<BufferDownloadInfo>()

    private fun downloaderAttachCallback(){
        mBufferDownloadInfo.forEach {
            downloadBingImage(it.bingImage, it.resolution)
        }
        mBufferDownloadInfo.clear()
    }

    private fun downloadBingImage(bingImage: BingImage, resolution: BingImage.Resolution){
        val url = bingImage.getImageUrl(resolution)
        val mFolder = mFileManager.downloadFolder.path
        mServiceConnection.downloadImage(url, mFolder, "${bingImage.title}-${bingImage.date}-${getString(resolution.toResolutionStringID())}")
    }

    fun downloadImage(bingImage:BingImage, resolution:BingImage.Resolution){
        if (attachState == Downloader.STATE_DISATTACH){ //////////////////////////// 未绑定
            bindService(Intent(applicationContext, DownloadService::class.java), this, Context.BIND_AUTO_CREATE)
            attachState = Downloader.STATE_ATTACHING
            mBufferDownloadInfo.add(BufferDownloadInfo(bingImage, resolution))
        }else if (attachState == Downloader.STATE_ATTACHING){//////////////////// 绑定中
            mBufferDownloadInfo.add(BufferDownloadInfo(bingImage, resolution))
        }else if (attachState == Downloader.STATE_ATTACH){/////////////////////// 已绑定
            downloadBingImage(bingImage, resolution)
        }
    }





}