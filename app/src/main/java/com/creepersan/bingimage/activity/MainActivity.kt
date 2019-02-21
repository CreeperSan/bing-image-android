package com.creepersan.bingimage.activity

import android.arch.lifecycle.*
import android.arch.persistence.room.Room
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.BingImageDatabase
import com.creepersan.bingimage.database.DB_BINGIMAGE
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.model.MainModel
import com.creepersan.bingimage.utils.isReachBottom
import com.creepersan.bingimage.view.holder.BingImageHolder
import com.creepersan.bingimage.view.holder.MainDrawerDiverHolder
import com.creepersan.bingimage.view.holder.MainDrawerHeaderHolder
import com.creepersan.bingimage.view.holder.MainDrawerSelectionHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity() {

    companion object {
        const val DRAWER_TYPE_HEADER = 0
        const val DRAWER_TYPE_SELECTION = 1
        const val DRAWER_TYPE_DIVER = 2
        const val DRAWER_TYPE_UNDEFINE = -1

        const val EXIT_CONFIRM_TIME = 1500
    }

    override val layoutID: Int = R.layout.activity_main

    private val mViewModel by lazy { ViewModelProviders.of(this).get(MainModel::class.java) }
    private val mRoom by lazy { Room.databaseBuilder(applicationContext, BingImageDatabase::class.java, DB_BINGIMAGE).allowMainThreadQueries().build() } // 暂时不需要，后面再考虑功能
    private val mLayoutManager = LinearLayoutManager(this)
    private val mAdapter = ImageAdapter()
    private val mDrawerList = ArrayList<BaseDrawerItem>()
    private lateinit var mListResolution : BingImage.Resolution
    private var mIsExitConfirm = true
    private var mPrevBackTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initResolution()
        initRecyclerView()
        initDrawerButton()
        initSwipeRefreshView()
        initDrawer()
        initRecyclerViewScrollListener()

        initLoadDataFromNetwork()

    }
    override fun onResume() {
        super.onResume()
        mListResolution = config.getListResolution()
        mIsExitConfirm = config.isDoubleClickExit()
    }

    private fun initResolution(){
        mListResolution = config.getListResolution()
        mIsExitConfirm = config.isDoubleClickExit()
    }
    private fun initRecyclerView(){
        mainRecyclerView.layoutManager = mLayoutManager
        mainRecyclerView.adapter = mAdapter
    }
    private fun initDrawerButton(){
        mainLeftIcon.setOnClickListener {
            mainDrawerLayout.openDrawer(Gravity.START)
        }
    }
    private fun initSwipeRefreshView(){
        if (!mViewModel.isAlreadyInitNetwork()){
            mViewModel.setAlreadyInitNetwork()
            loadNewest()
        }
        mainRefreshLayout.setOnRefreshListener {
            loadNewest()
        }
    }
    private fun initDrawer(){
        mDrawerList.clear()
        mDrawerList.add(HeaderDrawerItem())
        // 图库
        mDrawerList.add(SelectionDrawerItem(R.drawable.ic_image, R.string.mainDrawerSelectionGallery.toResString(),{
            toActivity(GalleryActivity::class.java)
            closeDrawer()
        }))
        // 设置
        mDrawerList.add(SelectionDrawerItem(R.drawable.ic_settings, R.string.mainDrawerSelectionSetting.toResString(),{
            toActivity(SettingActivity::class.java)
            closeDrawer()
        }))
        mDrawerList.add(DiverDrawerItem())
        // 关于
        mDrawerList.add(SelectionDrawerItem(R.drawable.ic_info, R.string.mainDrawerSelectionInfo.toResString(),{
            toActivity(InfoActivity::class.java)
            closeDrawer()
        }))
        // 帮助
        mDrawerList.add(SelectionDrawerItem(R.drawable.ic_help, R.string.mainDrawerSelectionHelp.toResString(),{
            toActivity(HelpActivity::class.java)
            closeDrawer()
        }))
        // 反馈
        mDrawerList.add(SelectionDrawerItem(R.drawable.ic_feedback, R.string.mainDrawerSelectionFeedback.toResString(),{
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:admin@creepersan.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.mainFeedbackMailTitle.toResString())
            intent.putExtra(Intent.EXTRA_TEXT, R.string.mainFeedbackMailContentTemple.toResString())
            startActivity(intent)
            closeDrawer()
        }, R.drawable.ic_email))
        mDrawerList.add(DiverDrawerItem())
        // 网页版
        mDrawerList.add(SelectionDrawerItem(R.drawable.ic_website, R.string.mainDrawerSelectionWebsite.toResString(),{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://bing.creepersan.com")))
            closeDrawer()
        }, R.drawable.ic_open_in_browser))
        mDrawerList.add(DiverDrawerItem())
        // 退出
        mDrawerList.add(SelectionDrawerItem(R.drawable.ic_exit, R.string.mainDrawerSelectionExit.toResString(),{
            closeDrawer()
            System.exit(0)
        }))
        // 设置
        mainDrawerRecyclerView.layoutManager = LinearLayoutManager(this)
        mainDrawerRecyclerView.adapter = DrawerAdapter()
    }
    private fun initRecyclerViewScrollListener(){
        mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.isReachBottom() && !mViewModel.isLoadAllDataFinish()){
                    loadMore()
                }
            }
        })
    }
    // 决定是否要从互联网加载数据
    private fun initLoadDataFromNetwork(){
        if (!mViewModel.isAlreadyInitNetwork()){
            mViewModel.setAlreadyInitNetwork()
            loadNewest()
        }
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (mIsExitConfirm){
            if (currentTime-mPrevBackTime >= EXIT_CONFIRM_TIME){
                mPrevBackTime = currentTime
                snackPressAgainToExit()
            }else{
                super.onBackPressed()
            }
        }else{
            super.onBackPressed()
        }
    }

    /* Event */
    private fun loadNewest(){
        snackLoading()
        mViewModel.getImage(retrofit, mViewModel.getPageCount(), 1,{ _, response -> /////////////////////// 网络请求成功
            mainRefreshLayout.isRefreshing = false
            if (response.isSuccessful && response.body()?.flag == 200){
                snackHide()
                val dataList = response.body()?.data?.imgList ?: ArrayList()
                val tmpList = ArrayList<BingImage>()
                dataList.forEach {
                    val tmpImage = it.toBingImage()
                    tmpList.add(tmpImage)
                }
                mRoom.bingImageDao().inserts(*tmpList.toTypedArray()) // 暂时用不上
                tmpList.forEach {
                    mViewModel.addOrReplaceBingImage(it)
                }
                mAdapter.notifyDataSetChanged()
            }else{
                snackServerError()
            }
        }, { _, _ -> /////////////////////////////////////////////// 网络请求失败
            mainRefreshLayout.isRefreshing = false
            snackNetworkError()
        })
    }
    private fun loadMore(){
        mainRefreshLayout.isRefreshing = true
        snackLoading()
        mViewModel.getImage(retrofit, mViewModel.getPageCount(), mViewModel.getPage()+1,{ call, response -> /////////////////////// 网络请求成功
            mainRefreshLayout.isRefreshing = false
            if (response.isSuccessful && response.body()?.flag == 200){ // 获取成功
                if (0 == response.body()?.data?.imgList?.size ?: 0){ // 数据已经加载完毕
                    mViewModel.isLoadFinish.value = true
                    snackLoadAllFinish()
                }else{ // 加载出了数据
                    snackHide()
                    mViewModel.setPage(mViewModel.getPage() + 1)
                    val dataList = response.body()?.data?.imgList ?: ArrayList()
                    val tmpList = ArrayList<BingImage>()
                    dataList.forEach {
                        val tmpImage = it.toBingImage()
                        tmpList.add(tmpImage)
                    }
                    mRoom.bingImageDao().inserts(*tmpList.toTypedArray()) // 暂时用不上
                    tmpList.forEach {
                        mViewModel.addOrReplaceBingImage(it)
                    }
                    mAdapter.notifyDataSetChanged()
                }
            }else{
                snackServerError()
            }
        }, { call, throwable -> /////////////////////////////////////////////// 网络请求失败
            mainRefreshLayout.isRefreshing = false
            snackNetworkError()
        })
    }

    /* Action */
    private fun snackLoading(){
        snack(R.string.mainSnackLoading.toResString(), Snackbar.LENGTH_INDEFINITE)
    }
    private fun snackNetworkError(){
        snack(R.string.mainSnackNetworkError.toResString())
    }
    private fun snackServerError(){
        snack(R.string.mainSnackRequestFail.toResString())
    }
    private fun snackLoadAllFinish(){
        snack(R.string.mainSnackLoadAllFinish.toResString())
    }
    private fun snackPressAgainToExit(){
        snack(R.string.mainSnackPressAgainToExit.toResString())
    }
    private fun closeDrawer(){
        mainDrawerLayout.closeDrawer(Gravity.START)
    }

    /* Observer */


    /* ViewModel */

    private inner class ImageAdapter : RecyclerView.Adapter<BingImageHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BingImageHolder {
            return BingImageHolder(this@MainActivity, parent)
        }

        override fun getItemCount(): Int {
            return mViewModel.imageList.value?.size ?: 0
        }

        override fun onBindViewHolder(holder: BingImageHolder, pos: Int) {
            mViewModel.imageList.value?.get(pos)?.apply {
                holder.setImageByUrl(this.getImageUrl(mListResolution))
                holder.setTitle(this.title)
                holder.setTime(this.getTimeString())
                holder.setOnClickListener(View.OnClickListener {
                    val intent = Intent(this@MainActivity, PreviewActivity::class.java)
                    intent.putExtra(PreviewActivity.INTENT_BING_IMAGE, this)
                    val pairImage = android.support.v4.util.Pair<View, String>(holder.image, "image")
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, pairImage).toBundle())
                })
            }
        }

    }

    private abstract class BaseDrawerItem
    private class HeaderDrawerItem : BaseDrawerItem()
    private class DiverDrawerItem : BaseDrawerItem()
    private class SelectionDrawerItem(var icon:Int, var title:String, var clickAction:()->Unit, var hint:Int=0) : BaseDrawerItem()
    private inner class DrawerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun getItemViewType(position: Int): Int {
            return when(mDrawerList[position]){
                is HeaderDrawerItem -> DRAWER_TYPE_HEADER
                is SelectionDrawerItem -> DRAWER_TYPE_SELECTION
                is DiverDrawerItem -> DRAWER_TYPE_DIVER
                else -> DRAWER_TYPE_UNDEFINE
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            return when(p1){
                DRAWER_TYPE_HEADER -> { MainDrawerHeaderHolder(this@MainActivity, p0) }
                DRAWER_TYPE_SELECTION -> { MainDrawerSelectionHolder(this@MainActivity, p0) }
                DRAWER_TYPE_DIVER -> { MainDrawerDiverHolder(this@MainActivity, p0) }
                else -> { throw UnknownError("Main - Drawer : DrawerHolder 类型错误") }
            }
        }

        override fun getItemCount(): Int {
            return mDrawerList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
            when(holder){
                is MainDrawerSelectionHolder -> {
                    val item = mDrawerList[pos] as SelectionDrawerItem
                    holder.setIcon(item.icon)
                    holder.setTitle(item.title)
                    holder.setHint(item.hint)
                    holder.setOnClickListener(item.clickAction)
                }
                is MainDrawerHeaderHolder -> {

                }
            }
        }

    }

}