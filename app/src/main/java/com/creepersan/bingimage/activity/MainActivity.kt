package com.creepersan.bingimage.activity

import android.arch.lifecycle.*
import android.content.Intent

import java.util.ArrayList

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.model.GlobalViewModel
import com.creepersan.bingimage.view.holder.BingImageHolder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override val layoutID: Int = R.layout.activity_main

    private val mViewModel by lazy { ViewModelProviders.of(this).get(GlobalViewModel::class.java) }
    private val mRecyclerViewObserver by lazy { RecyclerViewObserver() }
    private val mLayoutManager = LinearLayoutManager(this)
    private val mAdapter = ImageAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRecyclerView()

        mViewModel.imageList.observe(this, mRecyclerViewObserver)

        mViewModel.getImage(retrofit, { call, response ->
            if (response.isSuccessful && response.body()?.flag == 200){
                val dataList = response.body()?.data?.imgList ?: ArrayList()
                val tmpList = ArrayList<BingImage>()
                dataList.forEach {
                    tmpList.add(it.toBingImage())
                }
                mViewModel.imageList.value = tmpList
            }else{
                toast("请求失败")
            }
        }, { call, throwable ->
            toast("网络连接失败")
        })


//        val viewModel = ViewModelProviders.of(this).get(GlobalModel::class.java)
//
//        val mObserver = Observer<Int> {
//            mainAdd.text = viewModel.a.value.toString()
//        }
//
//        viewModel.a.observe(this, mObserver)






//        val txtObserver = Observer<ArrayList<Int>>{
//            val strBuilder = StringBuilder()
//            viewModel.list.value?.forEach {
//                strBuilder.append(it.toString()).appendln()
//            }
//            mainText.text = strBuilder.toString()
//        }
//        viewModel.list.observe(this, txtObserver)






//        mainAdd.setOnClickListener {
//            viewModel.a.value = viewModel.a.value?.plus(1) ?: 0
//            viewModel.list.value?.add(viewModel.a.value ?: 0)
//        }
    }

    private fun initRecyclerView(){
        mainRecyclerView.layoutManager = mLayoutManager
        mainRecyclerView.adapter = mAdapter
    }


//    class GlobalModel : ViewModel(){
//
//        var a = MutableLiveData<Int>()
//        var list = MutableLiveData<ArrayList<Int>>()
//
//        init {
//            a.value = 0
//            list.value = ArrayList()
//        }
//
//
//    }



    /* Observer */
    private inner class RecyclerViewObserver : Observer<ArrayList<BingImage>>{
        override fun onChanged(t: ArrayList<BingImage>?) {
            mAdapter.notifyDataSetChanged()
        }
    }



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
                holder.setImageByUrl("http://bing.creepersan.com${this.img_url}/400x240.jpg")
                holder.setOnClickListener(View.OnClickListener {
                    startActivity(Intent(this@MainActivity, PreviewActivity::class.java))
                })
            }
        }

    }

}