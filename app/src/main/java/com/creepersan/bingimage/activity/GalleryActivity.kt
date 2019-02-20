package com.creepersan.bingimage.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.creepersan.bingimage.R
import com.creepersan.bingimage.model.GalleryModel
import com.creepersan.bingimage.utils.isInt
import com.creepersan.bingimage.utils.setGone
import com.creepersan.bingimage.utils.setVisible
import com.creepersan.bingimage.view.holder.GalleryImageHolder
import com.liyi.viewer.ImageLoader
import com.liyi.viewer.ImageViewerState
import com.liyi.viewer.ViewData
import kotlinx.android.synthetic.main.activity_gallery.*
import java.io.File
import java.util.*
import java.util.ArrayList

class GalleryActivity : BaseActivity(), ImageLoader<GalleryActivity.GalleryItem> {

    companion object {
        const val DURAITON_ANIM = 300L
    }

    private val mViewModel by lazy { ViewModelProviders.of(this).get(GalleryModel::class.java) }
    private val mAdapter by lazy { GalleryAdapter() }
    private val mFileManager by lazy { application.getFileManager() }
    private val mGalleryListObserver by lazy { GalleryItemObserver() }
    private val mWidth by lazy { resources.displayMetrics.widthPixels.toFloat() }
    private val mHeight by lazy { resources.displayMetrics.heightPixels.toFloat() }
    private val mDefaultViewData by lazy { ViewData(0f,mHeight, mWidth, mHeight) }

    override val layoutID: Int = R.layout.activity_gallery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initGalleryToolbar()
        initRecyclerView()
        initImageViewer()
        initData()
        initLiveData()
    }

    private fun initToolbar(){
        galleryToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initGalleryToolbar(){
        galleryWatchToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        galleryWatchToolbar.inflateMenu(R.menu.gallery_watching)
        galleryWatchToolbar.setOnMenuItemClickListener {
            val item = mViewModel.getGalleryList()[galleryImageViewer.currentPosition]
            val file = item.file
            if(!file.exists()){
                toast(R.string.galleryHintFileNotExist.toResString())
                return@setOnMenuItemClickListener true
            }
            when(it.itemId){
                R.id.menuGalleryWatchingDelete -> {
                    hideGallery()
                    galleryImageViewer.postDelayed({
                        mViewModel.deleteGalleryItem(item)
                    }, DURAITON_ANIM*2)
                }
                R.id.menuGalleryWatchingShare -> {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.addCategory("android.intent.category.DEFAULT");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, if (Build.VERSION.SDK_INT >= 24){
                        FileProvider.getUriForFile(this, "${packageName}.provider", file)
                    }else{
                        Uri.fromFile(mViewModel.getGalleryList()[galleryImageViewer.currentPosition].file)
                    })
                    shareIntent.type = "image/*"
                    startActivity(Intent.createChooser(shareIntent,R.string.galleryShareTo.toResString()))
                }
                R.id.menuGalleryWatchingSetAsWallpaper -> {
                    val intent = Intent(Intent.ACTION_ATTACH_DATA)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra("mineType","image/*")
                    val hintText = R.string.gallerySetWallpaper.toResString()
                    val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, BitmapFactory.decodeFile(file.path), hintText, hintText))
                    intent.data = uri
                    startActivity(intent)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }
    private fun initRecyclerView(){
        galleryRecyclerView.layoutManager = LinearLayoutManager(this)
        galleryRecyclerView.adapter = mAdapter
    }
    private fun initImageViewer(){
        galleryImageViewer
            .doDrag(false)
            .setDuration(DURAITON_ANIM.toInt())
            .showIndex(false)
            .setImageMinScale(0.5f)
            .setImageMaxScale(5f)
            .setImageLoader(this)
            .setOnItemClickListener { position, view ->
                hideGallery()
                return@setOnItemClickListener true
            }
            .setOnImageChangedListener { position, _ ->
                refreshWatchToolbarName()
            }
    }
    private fun initData(){
        if (!mViewModel.isInitData()){
            val itemList = ArrayList<GalleryItem>()
            fileManager.downloadFolder.listFiles().forEach { file ->
                var fileName = file.name
                // 是否为jpg结尾
                if (!fileName.endsWith(".jpg")){
                    return@forEach
                }
                // 去除冗余以及重复的
                if (fileName.contains("@")){
                    return@forEach
                }
                // 解析数据
                fileName = fileName.substring(0, fileName.indexOf("."))
                val item = GalleryItem(file)
                fileName.split("-").forEach {
                    if (it.isInt() && it.length==8 && it.toInt()>20180101 && it.toInt()<21000000){ // 日期
                        item.time = it
                    }else if (it.contains("x") && it.split("x").size==2 && it.substring(0,it.indexOf("x")).isInt() && it.substring(it.indexOf("x")+1, it.length).isInt()){
                        item.resolution = it
                    }else if (!it.isInt()){
                        item.title = it
                    }
                }
                itemList.add(item)
            }
            mViewModel.setGalleryList(itemList)
            mViewModel.setInitData()
        }
        refreshData()
    }
    private fun initLiveData(){
        mViewModel.observerFileList(this, mGalleryListObserver)
    }


    override fun onBackPressed() {
        when(galleryImageViewer.viewState){
            ImageViewerState.STATE_SILENCE -> super.onBackPressed()
            ImageViewerState.STATE_WATCHING -> hideGallery()
        }
    }

    /* Action */
    private fun showGallery(){
        if (galleryImageViewer.viewState != ImageViewerState.STATE_SILENCE) return
        refreshWatchToolbarName()
        galleryWatchToolbar.setVisible()
        galleryImageViewer.setVisible()
        galleryImageViewer.watch()
    }
    private fun hideGallery(){
        if (galleryImageViewer.viewState != ImageViewerState.STATE_WATCHING) return
        galleryWatchToolbar.setGone()
        galleryImageViewer.close()
        galleryImageViewer.postDelayed({
            if (!isFinishing){
                galleryImageViewer.setGone()
            }
        }, DURAITON_ANIM)
    }
    private fun refreshWatchToolbarName(){
        galleryWatchToolbar.title = "${galleryImageViewer.currentPosition+1}/${mViewModel.getGalleryList().size}"
    }
    private fun refreshData(){
        // 设置图片列表
        mAdapter.notifyDataSetChanged()
        // 设置滚动预览信息
        val dataList = mViewModel.getGalleryList()
        if (dataList.isEmpty()){
            galleryImageViewer.clear()
        }else{
            // 这里为什么复制一遍？因为这个开源库有个坑，他在销毁时会执行clear()方法，导致LiveData里面的数据被删除
            val galleryList = ArrayList<GalleryItem>()
            dataList.forEach { // TODO : 这样复制效率不高，需要优化
                galleryList.add(it)
            }
            galleryImageViewer
                .setImageData(galleryList)
                .setViewData(Array(galleryList.size) {mDefaultViewData}.toMutableList())
        }
    }

    /* Override */
    override fun displayImage(position: Int, source: GalleryActivity.GalleryItem, imageView: ImageView) {
        imageView.setImageBitmap(BitmapFactory.decodeFile(source.file.path))
    }


    /* Observer */
    private inner class GalleryItemObserver : Observer<ArrayList<GalleryItem>>{
        override fun onChanged(t: ArrayList<GalleryItem>?) {
            refreshData()
        }
    }

    /* Inner Class */
    class GalleryItem(var file:File){
        var title:String = ""
        var time:String = ""
        var resolution:String = ""
    }
    private inner class GalleryAdapter : RecyclerView.Adapter<GalleryImageHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GalleryImageHolder {
            return GalleryImageHolder(this@GalleryActivity, p0)
        }

        override fun getItemCount(): Int {
            return mViewModel.getGalleryList().size
        }

        override fun onBindViewHolder(holder: GalleryImageHolder, position: Int) {
            val item = mViewModel.getGalleryList()[position]
            val pos = holder.adapterPosition
            Glide
                .with(this@GalleryActivity)
                .load(item.file)
                .into(holder.image)

            holder.setOnClickListener(View.OnClickListener {
                galleryImageViewer.setStartPosition(pos)
                showGallery()
            })
        }

    }

}