package com.creepersan.bingimage.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.creepersan.bingimage.R

class BootActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_boot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (fileManager.initFolderPath()){
            checkPermission()
        }else{
            toast("初始化文件结构失败")
            finish()
        }

        //
    }

    private fun checkPermission(){
        // 请求权限
        if (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName)){
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else{
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            checkPermission()
        }else{
            finish()
        }
    }

}