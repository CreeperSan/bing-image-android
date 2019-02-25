package com.creepersan.bingimage.activity

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.model.PreviewModel
import com.creepersan.bingimage.network.request.BingUrlRequest
import com.creepersan.bingimage.network.response.BingUrlResponse
import com.creepersan.bingimage.utils.*
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
    private val mImagePreviewResolutionObserver by lazy { ImagePreviewObserver() }
    private val mResolutionMenu by lazy {
        val popupMenu = PopupMenu(this, previewToolbar.findViewById(R.id.menuPreviewScreenResolution))
        popupMenu.menuInflater.inflate(R.menu.preview_resolution ,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            mViewModel.setPreviewResolution(when(it.itemId){
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
            })
            return@setOnMenuItemClickListener true
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
            mViewModel.setDownloadDialogDefaultNotDisplay(dialogCheckBox.isChecked)
        }
        val alertDialog = AlertDialog.Builder(this)
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
            fun showDialog(){
                dialogCheckBox.isChecked = mViewModel.isDownloadDialogDefaultNotDisplay()
                alertDialog.show()
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
        mViewModel.observerPreviewResolution(this, mImagePreviewResolutionObserver)
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
            if (mViewModel.isDownloadDialogDefaultNotDisplay()){ // 默认不显示
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
        }
        return true
    }

    /* Action */
    private fun downloadImage(){
        val bingimage = mViewModel.bingImage.value!!
        val resolution = mViewModel.getDownloadResolution()
        toast(R.string.previewToastDownload.toResString())

        application.downloadImage(bingimage, resolution)

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

                mViewModel.setDownloadResolution(item)

                notifyItemChanged(prevPos)
                notifyItemChanged(newPos)
            })
        }

    }

    /* Action */
    private fun refreshImageResolution(){
//        val previewResolution = mViewModel.getPreviewResolution()
        Glide
            .with(this)
            .load(mViewModel.getPreviewImageUrl())
            .placeholder(R.drawable.image_main_default)
            .error(R.drawable.image_main_fail)
            .transition(DrawableTransitionOptions.withCrossFade())
//            .override(previewResolution.getWidth(), previewResolution.getHeight())
            .into(previewImageView)

    }

    /* Observer */
    private inner class ImagePreviewObserver : Observer<BingImage.Resolution>{
        override fun onChanged(t: BingImage.Resolution?) {
            refreshImageResolution()
        }
    }
    private inner class BingImageObserver : Observer<BingImage?>{
        override fun onChanged(bingImage: BingImage?) {
            if(bingImage != null){
//                previewImageView.setImageGlide(bingImage.getImageUrl(mViewModel.getPreviewResolution()))
                refreshImageResolution()
                previewTitle.setTextOrDisappear(bingImage.title)
                previewLocation.setTextOrDisappear(bingImage.location)
                previewAuthor.setTextOrDisappear(bingImage.author)
            }
        }
    }

}

//int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, mContext.getResources().getDisplayMetrics());
//int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, mContext.getResources().getDisplayMetrics());