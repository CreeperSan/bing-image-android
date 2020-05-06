package com.creepersan.bingimage.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.creepersan.bingimage.R
import com.creepersan.bingimage.utils.setGone
import com.creepersan.bingimage.utils.setVisible
import kotlinx.android.synthetic.main.activity_boot.*

class BootActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_boot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInnerData()
        if (checkPermission()){
            checkFolderAndStart()
        }else{
            requestPermission()
        }
    }

    /**
     * 初始数据的兼容
     */
    private fun initInnerData(){
        // v1.0.0 - 下载分辨率现在已经可以多选，因此要删除旧的sp，并将旧的sp值写入到新的sp里面
        if (config.hasDownloadResolutions()){
            config.setDownloadResolutions(listOf(config.getDownloadResolution()))
        }


    }

    private fun showAsFileError(){
        bootImage.setVisible()
        bootText.setVisible()
        bootButton.setGone()
        bootImage.setImageResource(R.drawable.ic_attach_file)
        bootText.text = R.string.bootFileErrorText.toResString()
    }
    private fun showAsPermissionDenied(){
        bootImage.setVisible()
        bootText.setVisible()
        bootButton.setVisible()
        bootImage.setImageResource(R.drawable.ic_lock)
        bootText.text = R.string.bootPermissionErrorText.toResString()
        bootButton.text = R.string.bootPermissionErrorButton.toResString()
        bootButton.setOnClickListener {
            toActivity(PermissionDescriptionActivity::class.java)
        }
    }

    private fun checkPermission():Boolean{
        // 请求权限
        return PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName)
    }
    private fun requestPermission(){
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }
    private fun checkFolderAndStart(){
        if (fileManager.initFolderPath()){
            finish()
            toActivity(MainActivity::class.java)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        }else{
            showAsFileError()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            checkFolderAndStart()
        }else{
            showAsPermissionDenied()
        }
    }

}