package com.creepersan.bingimage.activity

import android.arch.lifecycle.*
import android.arch.persistence.room.Room
import android.content.Intent

import java.util.ArrayList

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
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_main

    private val mViewModel by lazy { ViewModelProviders.of(this).get(MainModel::class.java) }
    private val mRoom by lazy { Room.databaseBuilder(applicationContext, BingImageDatabase::class.java, DB_BINGIMAGE).allowMainThreadQueries().build() }
    private val mLayoutManager = LinearLayoutManager(this)
    private val mAdapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRecyclerView()
        initDrawerButton()
        initSwipeRefreshView()
        initRecyclerViewScrollListener()

        initLoadDataFromNetwork()

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
                mRoom.bingImageDao().inserts(*tmpList.toTypedArray())
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
                    mRoom.bingImageDao().inserts(*tmpList.toTypedArray())
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
                holder.setImageByUrl(this.getImageUrl(BingImage.Resolution.L_400_240))
                holder.setTitle(this.title)
                holder.setTime(this.getTimeString())
                holder.setOnClickListener(View.OnClickListener {
                    val intent = Intent(this@MainActivity, PreviewActivity::class.java)
                    intent.putExtra(PreviewActivity.INTENT_BING_IMAGE, this)
                    val pairImage = android.support.v4.util.Pair<View, String>(holder.image, "image")
                    val pairText  = android.support.v4.util.Pair<View, String>(holder.title, "title")
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, pairImage, pairText).toBundle())
                })
            }
        }

    }

}