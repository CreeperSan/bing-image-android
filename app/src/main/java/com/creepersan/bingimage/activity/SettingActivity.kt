package com.creepersan.bingimage.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.CheckBox
import com.creepersan.bingimage.R
import com.creepersan.bingimage.utils.toResolutionStringID
import com.creepersan.bingimage.view.holder.setting.SettingCatalogHolder
import com.creepersan.bingimage.view.holder.setting.SettingCheckBoxHolder
import com.creepersan.bingimage.view.holder.setting.SettingDiverHolder
import com.creepersan.bingimage.view.holder.setting.SettingSimpleHolder
import kotlinx.android.synthetic.main.activity_setting.*
import java.lang.IllegalStateException

class SettingActivity : BaseActivity() {

    companion object {
        const val TYPE_CATALOG = 1
        const val TYPE_DIVER = 2
        const val TYPE_SIMPLE = 3
        const val TYPE_CHECKBOX = 4
        const val TYPE_UNDEFINE = -1
    }

    private val mSettingList = ArrayList<BaseSettingItem>()
    private val mAdapter by lazy { SettingAdapter() }

    override val layoutID: Int = R.layout.activity_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initData()
        initRecyclerView()
    }

    private fun initToolbar(){
        settingToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun initData(){
        mSettingList.add(CatalogSettingItem(R.string.settingBasicCatalog.toResString()))
        mSettingList.add(CheckBoxSettingItem(R.drawable.ic_exit_to_app,R.string.settingBasicDoubleClickTitle.toResString(),R.string.settingBasicDoubleClickContent.toResString(), config.isDoubleClickExit()) {
            item, checkBox, pos ->
            val newCheckState = !checkBox.isChecked
            checkBox.isChecked = newCheckState
            item.isCheck = newCheckState
            config.setDoubleClickExit(newCheckState)
            mAdapter.notifyItemChanged(pos)
        })
        /* 预览设置 */
        mSettingList.add(DiverSettingItem())
        mSettingList.add(CatalogSettingItem(R.string.settingViewSettingCatalog.toResString()))
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewResolutionTitle.toResString(),config.getListResolution().toResolutionStringID().toResString()){
            item, pos ->

        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewPreviewTitle.toResString(),config.getPreviewResolution().toResolutionStringID().toResString()){
            item, pos ->

        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewDownloadTitle.toResString(),config.getDownloadResolution().toResolutionStringID().toResString()){
            item, pos ->

        })
        /* 其他设置 */
        mSettingList.add(DiverSettingItem())
        mSettingList.add(CatalogSettingItem(R.string.settingOtherCatalog.toResString()))
        mSettingList.add(SimpleSettingItem( R.drawable.ic_clean, R.string.settingOtherClearCacheTitle.toResString(), R.string.settingOtherClearCacheContent.toResString()) {
            item, pos ->

        })
    }
    private fun initRecyclerView(){
        settingRecyclerView.layoutManager = LinearLayoutManager(this)
        settingRecyclerView.adapter = mAdapter
    }



    /* 设置项 */
    private inner class SettingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun getItemViewType(position: Int): Int {
            return when(mSettingList[position]){
                is DiverSettingItem -> TYPE_DIVER
                is CatalogSettingItem -> TYPE_CATALOG
                is SimpleSettingItem -> TYPE_SIMPLE
                is CheckBoxSettingItem -> TYPE_CHECKBOX
                else -> TYPE_UNDEFINE
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            return when(p1){
                TYPE_DIVER -> SettingDiverHolder(this@SettingActivity, p0)
                TYPE_CATALOG -> SettingCatalogHolder(this@SettingActivity, p0)
                TYPE_SIMPLE -> SettingSimpleHolder(this@SettingActivity, p0)
                TYPE_CHECKBOX -> SettingCheckBoxHolder(this@SettingActivity, p0)
                else -> throw IllegalStateException()
            }
        }

        override fun getItemCount(): Int {
            return mSettingList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val pos = holder.adapterPosition
            when(holder){
                is SettingDiverHolder -> {
                    // 没事做
                }
                is SettingCatalogHolder -> {
                    val item = mSettingList[pos] as CatalogSettingItem
                    holder.initHolder(item)
                }
                is SettingSimpleHolder -> {
                    val item = mSettingList[pos] as SimpleSettingItem
                    holder.initHolder(item)
                }
                is SettingCheckBoxHolder -> {
                    val item = mSettingList[pos] as CheckBoxSettingItem
                    holder.initHolder(item)
                }
            }
        }

    }

    open class BaseSettingItem
    class DiverSettingItem : BaseSettingItem()
    class CatalogSettingItem(var title:String) : BaseSettingItem()
    class SimpleSettingItem(var icon:Int, var title:String, var content:String, var clickAction:((SimpleSettingItem,Int)->Unit)?):BaseSettingItem()
    class CheckBoxSettingItem(var icon:Int, var title:String, var content:String, var isCheck:Boolean,var clickAction:((CheckBoxSettingItem,CheckBox,Int)->Unit)?):BaseSettingItem()

}