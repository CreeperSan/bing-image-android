package com.creepersan.bingimage.activity

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.model.PreviewModel
import com.creepersan.bingimage.network.request.BingUrlRequest
import com.creepersan.bingimage.network.response.BingUrlResponse
import com.creepersan.bingimage.utils.setImageGlide
import com.creepersan.bingimage.utils.setTextOrDisappear
import com.creepersan.bingimage.utils.toResolutionStringID
import com.creepersan.bingimage.view.holder.PreviewDownloadResolutionItemHolder
import kotlinx.android.synthetic.main.activity_preview.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PreviewActivity : BaseActivity(), Toolbar.OnMenuItemClickListener {

    companion object {
        const val INTENT_BING_IMAGE = "image"
    }

    private val mViewModel by lazy { ViewModelProviders.of(this).get(PreviewModel::class.java) }
    private val mBingImageObserver by lazy { BingImageObserver() }
    private val mImageScaleObserver by lazy { ImageScaleObserver() }
    private val mImagePreviewResolutionObserver by lazy { ImagePreviewObserver() }
    private val mResolutionMenu by lazy {
        val popupMenu = PopupMenu(this, previewToolbar.findViewById(R.id.menuPreviewScreenResolution))
        popupMenu.menuInflater.inflate(R.menu.preview_resolution ,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            mViewModel.previewResolution.value = when(it.itemId){
                R.id.menuPreviewResolution1920x1200 -> BingImage.Resolution.L_1920_1200
                R.id.menuPreviewResolution1920x1080 -> BingImage.Resolution.L_1920_1080
                R.id.menuPreviewResolution1366x768 -> BingImage.Resolution.L_1366_768
                R.id.menuPreviewResolution1280x720 -> BingImage.Resolution.L_1280_720
                R.id.menuPreviewResolution1024x768 -> BingImage.Resolution.L_1024_768
                R.id.menuPreviewResolution800x600 -> BingImage.Resolution.L_800_600
                R.id.menuPreviewResolution800x480 -> BingImage.Resolution.L_800_480
                R.id.menuPreviewResolution640x480 -> BingImage.Resolution.L_640_480
                R.id.menuPreviewResolution400x240 -> BingImage.Resolution.L_400_240
                R.id.menuPreviewResolution320x240 -> BingImage.Resolution.L_320_240
                R.id.menuPreviewResolution1080x1920 -> BingImage.Resolution.P_1080_1920
                R.id.menuPreviewResolution768x1366 -> BingImage.Resolution.P_768_1366
                R.id.menuPreviewResolution768x1200 -> BingImage.Resolution.P_768_1280
                R.id.menuPreviewResolution720x1280 -> BingImage.Resolution.P_720_1280
                R.id.menuPreviewResolution480x800 -> BingImage.Resolution.P_480_800
                R.id.menuPreviewResolution480x640 -> BingImage.Resolution.P_480_640
                R.id.menuPreviewResolution240x400 -> BingImage.Resolution.P_240_400
                R.id.menuPreviewResolution240x320 -> BingImage.Resolution.P_240_320
                else -> BingImage.Resolution.L_1920_1080
            }
            true
        }
        popupMenu
    }
    private val mImageScaleMenu by lazy {
        val popupMenu = PopupMenu(this, previewToolbar.findViewById(R.id.menuPreviewImageScale))
        popupMenu.menuInflater.inflate(R.menu.preview_scale ,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            mViewModel.imageScale.value = when(it.itemId){
                R.id.menuPreviewScaleCenter -> ImageView.ScaleType.CENTER
                R.id.menuPreviewScaleCenterCrop -> ImageView.ScaleType.CENTER_CROP
                R.id.menuPreviewScaleCenterInside -> ImageView.ScaleType.CENTER_INSIDE
                R.id.menuPreviewScaleFitCenter -> ImageView.ScaleType.FIT_CENTER
                R.id.menuPreviewScaleFitEnd -> ImageView.ScaleType.FIT_END
                R.id.menuPreviewScaleFitStart -> ImageView.ScaleType.FIT_START
                R.id.menuPreviewScaleFitXY -> ImageView.ScaleType.FIT_XY
                R.id.menuPreviewScaleMatrix -> ImageView.ScaleType.MATRIX
                else -> ImageView.ScaleType.CENTER_CROP
            }
            true
        }
        popupMenu
    }
    private val mDownloadDialog by lazy {
        val dialogView = layoutInflater.inflate(R.layout.dialog_preview_download, null)
        val dialogRecyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogPreviewDownloadRecyclerView)
        val dialogCheckBox = dialogView.findViewById<CheckBox>(R.id.dialogPreviewDownloadCheckBox)
        dialogRecyclerView.layoutManager = LinearLayoutManager(this@PreviewActivity)
        dialogRecyclerView.adapter = DownloadResolutionAdapter(arrayListOf(
            BingImage.Resolution.P_1080_1920,
            BingImage.Resolution.P_768_1366,
            BingImage.Resolution.P_768_1280,
            BingImage.Resolution.P_720_1280,
            BingImage.Resolution.P_480_800,
            BingImage.Resolution.P_480_640,
            BingImage.Resolution.P_240_400,
            BingImage.Resolution.L_1920_1200,
            BingImage.Resolution.L_1920_1080,
            BingImage.Resolution.L_1366_768,
            BingImage.Resolution.L_1280_720,
            BingImage.Resolution.L_1024_768,
            BingImage.Resolution.L_800_600,
            BingImage.Resolution.L_800_480,
            BingImage.Resolution.L_640_480,
            BingImage.Resolution.L_400_240,
            BingImage.Resolution.L_320_240
        ))
        dialogCheckBox.setOnClickListener {
            mViewModel.isDownloadDialogDefaultNotDisplay.value = dialogCheckBox.isChecked
        }
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.previewDownloadDialogTitle)
            .setView(dialogView)
            .setPositiveButton(R.string.previewDownloadDialogPosButtonText.toResString()) { dialog, _ ->
                downloadImage()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.previewDownloadDialogNegButtonText.toResString()) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        val obj = object {
            fun setNotDisplayDialog(state:Boolean){
                dialogCheckBox.isChecked = state
            }

            fun isNotDisplayDialog():Boolean{
                return dialogCheckBox.isChecked
            }

            fun showDialog(){
                dialogCheckBox.isChecked = mViewModel.isDownloadDialogDefaultNotDisplay.value ?: false
                alertDialog.show()
            }

            fun hideDialog(){
                alertDialog.dismiss()
            }
        }
        return@lazy obj
    }


    override val layoutID: Int = R.layout.activity_preview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLiveData()
        initData()
        initToolbar()
        initFloatingActionButton()
    }

    private fun initLiveData(){
        mViewModel.bingImage.observe(this, mBingImageObserver)
        mViewModel.imageScale.observe(this, mImageScaleObserver)
        mViewModel.previewResolution.observe(this, mImagePreviewResolutionObserver)
    }

    private fun initData(){
        if (false == mViewModel.isInit.value){
            mViewModel.bingImage.value = intent.getSerializableExtra(INTENT_BING_IMAGE) as BingImage
        }
    }

    private fun initToolbar(){
        previewToolbar.inflateMenu(R.menu.preview)
        previewToolbar.setOnMenuItemClickListener(this)
        previewToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initFloatingActionButton(){
        previewDownload.setOnClickListener {
            if (true == mViewModel.isDownloadDialogDefaultNotDisplay.value){ // 默认不显示
                downloadImage()
            }else{ // 默认显示
                mDownloadDialog.showDialog()
            }

        }
        previewDownload.setOnLongClickListener {
            mDownloadDialog.showDialog()
            return@setOnLongClickListener true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuPreviewScreenResolution -> {
                mResolutionMenu.show()
            }
            R.id.menuPreviewImageScale -> {
                mImageScaleMenu.show()
            }
        }
        return true
    }

    /* Action */
    private fun downloadImage(){
        val bingimage = mViewModel.bingImage.value!!
        val resolution = mViewModel.getDownloadResolution()
        val url = bingimage.getImageUrl(resolution)
        toast("下载图片分辨率${resolution.toResolutionStringID().toResString()} $url")


        retrofit.create(BingUrlRequest::class.java)
            .download(bingimage.date, mViewModel.getDownloadResolution().value)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    toast("失败了")
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful){

                        Thread(){
                            val file = File("${Environment.getExternalStorageDirectory().path}/${System.currentTimeMillis()}.jpg")

                            val inputSteam = response.body()!!.byteStream()
                            val outputSteam = file.outputStream()

                            val byteArray = ByteArray(4096) {0}
                            var len = 0

                            while ( true){
                                len = inputSteam.read(byteArray)
                                if (len != -1){
                                    outputSteam.write(byteArray, 0, len)
                                }else{
                                    break
                                }
                            }
                            runOnUiThread { toast("下载完成了") }
                        }.start()




                    }else{
                        toast("出错了")
                    }
                }


            })
    }

    /* Adapter */
    private inner class DownloadResolutionAdapter(var resolutionList:ArrayList<BingImage.Resolution>) : RecyclerView.Adapter<PreviewDownloadResolutionItemHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PreviewDownloadResolutionItemHolder {
            return PreviewDownloadResolutionItemHolder(this@PreviewActivity, p0)
        }

        override fun getItemCount(): Int {
            return resolutionList.size
        }

        override fun onBindViewHolder(holder: PreviewDownloadResolutionItemHolder, pos: Int) {
            val item = resolutionList[pos]
            holder.setTitle(item.toResolutionStringID().toResString())
            holder.setCheck(item == mViewModel.getDownloadResolution())
            holder.setOnClickListener(View.OnClickListener {
                var prevPos = 0
                val newPos = holder.adapterPosition
                resolutionList.forEachIndexed { index, item ->
                    if (item == mViewModel.getDownloadResolution()){
                        prevPos = index
                        return@forEachIndexed
                    }
                }

                mViewModel.downloadResolution.value = item

                notifyItemChanged(prevPos)
                notifyItemChanged(newPos)
            })
        }

    }

    /* Action */
    private fun refreshImageScale(){
        previewImageView.scaleType = mViewModel.getImageScaleType()
    }
    private fun refreshImageResolution(){
        previewImageView.setImageGlide(mViewModel.getPreviewImageUrl())
    }

    /* Observer */
    private inner class ImagePreviewObserver : Observer<BingImage.Resolution>{
        override fun onChanged(t: BingImage.Resolution?) {
            refreshImageResolution()
        }
    }
    private inner class ImageScaleObserver : Observer<ImageView.ScaleType>{
        override fun onChanged(t: ImageView.ScaleType?) {
            refreshImageScale()
        }
    }
    private inner class BingImageObserver : Observer<BingImage?>{
        override fun onChanged(bingImage: BingImage?) {
            if(bingImage != null){
                previewImageView.setImageGlide(bingImage.getImageUrl(mViewModel.getPreviewResolution()))
                previewTitle.setTextOrDisappear(bingImage.title)
                previewLocation.setTextOrDisappear(bingImage.location)
                previewAuthor.setTextOrDisappear(bingImage.author)
            }
        }
    }

}