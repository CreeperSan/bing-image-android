package com.creepersan.bingimage.activity

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.model.RandomModel
import com.creepersan.bingimage.network.request.BingUrlRequest
import com.creepersan.bingimage.network.response.BingRandomResponse
import com.creepersan.bingimage.view.holder.BingImageHolder
import kotlinx.android.synthetic.main.activity_random.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class RandomActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_random
    private val model by lazy { ViewModelProviders.of(this).get(RandomModel::class.java) }
    private val mAdapter by lazy { RandomAdapter() }
    private val mResolution by lazy { config.getListResolution() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initSwipeRefreshLayout()
        initRecyclerView()
        initObserver()
        initData()
    }

    private fun initToolbar(){
        randomToolbar.setNavigationOnClickListener { onBackPressed() }
    }
    private fun initSwipeRefreshLayout(){
        randomSwipeRefresh.setOnRefreshListener {
            getRandomImageFromNetwork()
        }
    }
    private fun initRecyclerView(){
        randomRecyclerView.layoutManager =
            LinearLayoutManager(this)
        randomRecyclerView.adapter = mAdapter
    }
    private fun initObserver(){
        model.observeImageData(this, ImageDataObserver())
    }
    private fun initData(){
        if (!model.isInitData()){
            getRandomImageFromNetwork()
        }
    }


    /* Action */
    private fun getRandomImageFromNetwork(){
        snackLoading()
        randomSwipeRefresh.isRefreshing = true
        retrofit.create(BingUrlRequest::class.java)
            .getBingRandom(6)
            .enqueue(object : Callback<BingRandomResponse>{
                override fun onFailure(call: Call<BingRandomResponse>, t: Throwable) {
                    randomSwipeRefresh.isRefreshing = false
                    snackNetworkError()
                }

                override fun onResponse(call: Call<BingRandomResponse>, response: Response<BingRandomResponse>) {
                    randomSwipeRefresh.isRefreshing = false
                    if (response.isSuccessful && 200==response.body()?.flag){
                        val data = response.body()!!.data!!
                        val dataList = ArrayList<BingImage>()
                        data.forEach {
                            dataList.add(it.toBingImage())
                        }
                        snackHide()
                        model.setImageData(dataList)
                        model.setInitData(true)
                    }else{
                        snackServerError()
                    }
                }

            })
    }


    /* inner class */
    private inner class ImageDataObserver : Observer<ArrayList<BingImage>>{
        override fun onChanged(t: ArrayList<BingImage>?) {
            mAdapter.notifyDataSetChanged()
        }
    }


    private inner class RandomAdapter : RecyclerView.Adapter<BingImageHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BingImageHolder {
            return BingImageHolder(this@RandomActivity, p0)
        }

        override fun getItemCount(): Int {
            return model.getDataSize()
        }

        override fun onBindViewHolder(holder: BingImageHolder, pos: Int) {
            val bingImage = model.getData(pos)
            bingImage?.apply {
                holder.setTime(this.getTimeString())
                holder.setTitle(this.title)
                holder.setImageByUrl(this.getImageUrl(mResolution))
                holder.setOnClickListener(View.OnClickListener {
                    val intent = Intent(this@RandomActivity, PreviewActivity::class.java)
                    intent.putExtra(PreviewActivity.INTENT_BING_IMAGE, this)
                    val pairImage = androidx.core.util.Pair<View, String>(holder.image, "image")
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@RandomActivity, pairImage).toBundle())
                })
            }
        }

    }

}