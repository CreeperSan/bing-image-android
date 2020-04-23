package com.creepersan.bingimage.download

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.creepersan.bingimage.R
import com.creepersan.bingimage.activity.GalleryActivity
import com.creepersan.bingimage.activity.InfoActivity
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import java.io.File
import java.lang.Exception
import java.util.*

class DownloadService : Service(),DownloadListener{

    companion object {
        const val ID_DOWNLOAD_NOTIFICATION_DOWNLOADING = 12035
        const val ID_DOWNLOAD_NOTIFICATION_FINISH = 12036
        const val ID_MUSIC_NOTIFICATION_CHANNEL = "com.creepersan.bingimage.NOTIFICATION_CHANNEL_ID.DOWNLOADER"

        const val REQUEST_CODE_CHECK_GALLERY = 305
    }

    private val mNotification by lazy {
        val notificationBuilder = getNotificationBuilder()
        return@lazy notificationBuilder
            .setSmallIcon(R.drawable.ic_main_title)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.downloadServiceNotificationDownloading))
            .build()
    }
    private val mNotificationFinish by lazy {
        val builder = getNotificationBuilder()
        return@lazy builder
            .setSmallIcon(R.drawable.ic_main_title)
            .setContentTitle(getString(R.string.app_name))
            .setContentIntent(PendingIntent.getActivity(this, REQUEST_CODE_CHECK_GALLERY, Intent(this, GalleryActivity::class.java), PendingIntent.FLAG_ONE_SHOT))
            .setContentText(getString(R.string.downloadServiceNotificationDownloadFinish))
            .setAutoCancel(true)
            .build()
    }

    private val mNotificationChannel by lazy { getNotificationChannel() }

    private fun getNotificationBuilder():NotificationCompat.Builder{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationCompat.Builder(this, mNotificationChannel.id)
        }else{
            NotificationCompat.Builder(this)
        }
    }

    override fun connectTrialEnd(task: DownloadTask, responseCode: Int, responseHeaderFields: MutableMap<String, MutableList<String>>) {}
    override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {}
    override fun downloadFromBeginning(task: DownloadTask, info: BreakpointInfo, cause: ResumeFailedCause) {}
    override fun taskStart(task: DownloadTask) {}
    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
        setIdle()
    }
    override fun connectTrialStart(task: DownloadTask, requestHeaderFields: MutableMap<String, MutableList<String>>) {}
    override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {}
    override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {}
    override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {}
    override fun connectEnd(task: DownloadTask, blockIndex: Int, responseCode: Int, responseHeaderFields: MutableMap<String, MutableList<String>>) {}
    override fun connectStart(task: DownloadTask, blockIndex: Int, requestHeaderFields: MutableMap<String, MutableList<String>>) {}

    private val mBinder by lazy { DownloadServiceController() }
    override fun onBind(intent: Intent?): IBinder = mBinder


    override fun onCreate() {
        super.onCreate()
    }

    fun setProcessing(){
        startForeground(ID_DOWNLOAD_NOTIFICATION_DOWNLOADING, mNotification)
    }

    fun setIdle(){
        stopForeground(true)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(ID_DOWNLOAD_NOTIFICATION_FINISH, mNotificationFinish)
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel(): NotificationChannel {
        val notificationChannel = NotificationChannel(ID_MUSIC_NOTIFICATION_CHANNEL, applicationContext.getString(R.string.downloadServiceNotificationChannelName), NotificationManager.IMPORTANCE_NONE)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)
        return notificationChannel
    }




    inner class DownloadServiceController : Binder(){

        fun downloadImage(url:String, folder:String, fileName:String){
            setProcessing()
            var downloadFile = File("$folder/$fileName.jpg")
            if (downloadFile.exists()){
                downloadFile = File("$folder/$fileName@${System.currentTimeMillis()}.jpg")
            }
            val task = DownloadTask.Builder(url, downloadFile.parentFile)
                .setFilename(downloadFile.name)
                .setMinIntervalMillisCallbackProcess(30)
                .build()
            task.enqueue(this@DownloadService)
        }

    }
}