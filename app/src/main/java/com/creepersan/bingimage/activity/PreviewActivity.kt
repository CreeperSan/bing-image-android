package com.creepersan.bingimage.activity

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.model.PreviewModel
import com.creepersan.bingimage.utils.*
import com.creepersan.bingimage.view.holder.PreviewDownloadResolutionItemHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_preview.*

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
        val dialogCheckBox = dialogView.findViewById<CheckBox>(R.id.dialogPreviewDownloadCheckBox)
        dialogCheckBox.setOnClickListener {
            mViewModel.setDownloadDialogDefaultNotDisplay(dialogCheckBox.isChecked)
        }
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)
        val bottomSheetDialogBehavior = BottomSheetBehavior.from(dialogView.parent as View)
        bottomSheetDialogBehavior.peekHeight = resources.displayMetrics.heightPixels
        bottomSheetDialogBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset.toInt() == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetDialog.dismiss()
                    bottomSheetDialogBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

        })
        return@lazy object {
            fun showDialog(){
                dialogCheckBox.isChecked = mViewModel.isDownloadDialogDefaultNotDisplay()
                bottomSheetDialog.show()
            }
        }
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