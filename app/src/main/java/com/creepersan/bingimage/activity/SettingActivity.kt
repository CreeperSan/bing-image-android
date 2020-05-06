package com.creepersan.bingimage.activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage.Resolution
import com.creepersan.bingimage.utils.setGone
import com.creepersan.bingimage.utils.setVisible
import com.creepersan.bingimage.utils.toResolutionStringID
import com.creepersan.bingimage.view.dialog.ResolutionSelectorDialog
import com.creepersan.bingimage.view.dialog.SeekBarDialog
import com.creepersan.bingimage.view.holder.setting.SettingCatalogHolder
import com.creepersan.bingimage.view.holder.setting.SettingCheckBoxHolder
import com.creepersan.bingimage.view.holder.setting.SettingDiverHolder
import com.creepersan.bingimage.view.holder.setting.SettingSimpleHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.File
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.ArrayList

class SettingActivity : BaseActivity() {

    companion object {
        const val TYPE_CATALOG = 1
        const val TYPE_DIVER = 2
        const val TYPE_SIMPLE = 3
        const val TYPE_CHECKBOX = 4
        const val TYPE_UNDEFINE = -1

        const val DURATION_HINT_DIALOG = 1000L
    }

    private val mLongTaskDialog by lazy {
        val dialogView = layoutInflater.inflate(R.layout.dialog_setting_long_task, null)
        val textView = dialogView.findViewById<TextView>(R.id.dialogSettingLongTaskText)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.dialogSettingLongTaskProgressBar)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        return@lazy object {
            fun setCancelable(isCancelable:Boolean){
                dialog.setCancelable(isCancelable)
                dialog.setCanceledOnTouchOutside(isCancelable)
            }
            fun show(){
                dialog.show()
            }
            fun hide(){
                dialog.hide()
            }
            fun setText(text:String){
                textView.text = text
            }
            fun setTitle(title:String){
                dialog.setTitle(title)
            }
            fun setAsLoading(){
                progressBar.setVisible()
            }
            fun setAsLoadFinish(){
                progressBar.setGone()
            }
        }
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
        mSettingList.add(CheckBoxSettingItem(R.drawable.ic_image,R.string.settingBasicUseOldGalleryTitle.toResString(),R.string.settingBasicUseOldGalleryContent.toResString(), config.isUseOldGallery()) {
            item, checkBox, pos ->
            val newCheckState = !checkBox.isChecked
            checkBox.isChecked = newCheckState
            item.isCheck = newCheckState
            config.setUseOldGallery(newCheckState)
            mAdapter.notifyItemChanged(pos)
        })
        mSettingList.add(CheckBoxSettingItem(R.drawable.ic_exit_to_app,R.string.settingBasicDoubleClickTitle.toResString(),R.string.settingBasicDoubleClickContent.toResString(), config.isDoubleClickExit()) {
            item, checkBox, pos ->
            val newCheckState = !checkBox.isChecked
            checkBox.isChecked = newCheckState
            item.isCheck = newCheckState
            config.setDoubleClickExit(newCheckState)
            mAdapter.notifyItemChanged(pos)
        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_cached,R.string.settingOtherClearCacheSizeTitle.toResString(),"${config.getCacheSize()} M"){ item, pos ->
            SeekBarDialog(
                this,
                "设置最大缓存大小",
                hint = item.content,
                min = 0,
                progress = config.getCacheSize(),
                max = 1024,
                onSeek = { dialog, fromUser ->
                    if (!fromUser) return@SeekBarDialog
                    dialog.setHint("${dialog.getProgress()} M")
                },
                onConfirm = { dialog ->
                    val progress = dialog.getProgress()
                    config.setCacheSize(dialog.getProgress())   // 更新配置
                    item.content = "$progress M"                // 更新UI
                    mAdapter.notifyItemChanged(pos)
                    return@SeekBarDialog false
                }
            ).show()
        })
        /* 预览设置 */
        mSettingList.add(DiverSettingItem())
        mSettingList.add(CatalogSettingItem(R.string.settingViewSettingCatalog.toResString()))
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewResolutionTitle.toResString(),config.getListResolution().toResolutionStringID().toResString()){ item, pos ->
            ResolutionSelectorDialog(this, "设置首页分辨率",
                isSingleSelection = true,
                resolutionList = listOf(config.getListResolution()),
                pinnedResolutionList = listOf(Resolution.L_640_480, Resolution.L_400_240, Resolution.L_320_240),
                onResolutionSelected = { resultList ->
                    if (resultList.isEmpty()){
                        toast("请至少选择一个分辨率")
                        return@ResolutionSelectorDialog false
                    }else{
                        val selectedResolution = resultList[0]
                        config.setListResolution(selectedResolution) // 进行设置
                        item.content = selectedResolution.toResolutionStringID().toResString() // 修改列表数据
                        mAdapter.notifyItemChanged(pos) // 刷新列表
                        return@ResolutionSelectorDialog true
                    }
                }
            ).show()
        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewPreviewTitle.toResString(),config.getPreviewResolution().toResolutionStringID().toResString()){ item, pos ->
            ResolutionSelectorDialog(this, "设置预览页分辨率",
                isSingleSelection = true,
                resolutionList = listOf(config.getPreviewResolution()),
                pinnedResolutionList = listOf(Resolution.L_1920_1080, Resolution.L_1366_768, Resolution.L_1280_720, Resolution.P_1080_1920, Resolution.P_768_1366, Resolution.P_720_1280),
                onResolutionSelected = { resultList ->
                    if (resultList.isEmpty()){
                        toast("请至少选择一个分辨率")
                        return@ResolutionSelectorDialog false
                    }else{
                        val selectedResolution = resultList[0]
                        config.setPreviewResolution(selectedResolution) // 进行设置
                        item.content = selectedResolution.toResolutionStringID().toResString() // 修改列表数据
                        mAdapter.notifyItemChanged(pos) // 刷新列表
                        return@ResolutionSelectorDialog true
                    }
                }
            ).show()
        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewDownloadTitle.toResString(),config.getDownloadResolutions().toFormatString()){ item, pos ->
            ResolutionSelectorDialog(this, "设置下载分辨率",
                isSingleSelection = false,
                resolutionList = config.getDownloadResolutions(),
                pinnedResolutionList = listOf(Resolution.L_1920_1080, Resolution.L_1366_768, Resolution.L_1280_720, Resolution.P_1080_1920, Resolution.P_768_1366, Resolution.P_720_1280),
                onResolutionSelected = { resultList ->
                    if (resultList.isEmpty()){
                        toast("请至少选择一个分辨率")
                        return@ResolutionSelectorDialog false
                    }else{
                        config.setDownloadResolutions(resultList) // 进行设置
                        item.content = resultList.toFormatString() // 修改列表数据
                        mAdapter.notifyItemChanged(pos) // 刷新列表
                        return@ResolutionSelectorDialog true
                    }
                }
            ).show()
        })
        mSettingList.add(CheckBoxSettingItem(R.drawable.ic_download_dialog,R.string.settingViewDownloadHideDownloadDialogTitle.toResString(),R.string.settingViewDownloadHideDownloadDialogContent.toResString(), config.isDialogDefaultNotDisplay()) {
                item, checkBox, pos ->
            val newCheckState = !checkBox.isChecked
            checkBox.isChecked = newCheckState
            item.isCheck = newCheckState
            config.setDialogDefaultNotDisplay(newCheckState)
            mAdapter.notifyItemChanged(pos)
        })
        /* 其他设置 */
        mSettingList.add(DiverSettingItem())
        mSettingList.add(CatalogSettingItem(R.string.settingOtherCatalog.toResString()))
        val cacheItem = SimpleSettingItem( R.drawable.ic_clean, R.string.settingOtherClearCacheTitle.toResString(), R.string.settingOtherClearCacheContentPrepare.toResString()) {
                item, _ ->
            mLongTaskDialog.setCancelable(false)
            mLongTaskDialog.setText(R.string.settingOtherClearCacheHint.toResString())
            mLongTaskDialog.setAsLoading()
            mLongTaskDialog.show()
            Async({
                clearCache()
                Thread.sleep(300)
                runOnUiThread {
                    item.content = String.format(R.string.settingOtherClearCacheContent.toResString(), getCacheSize())
                    mLongTaskDialog.setAsLoadFinish()
                    mLongTaskDialog.setText(R.string.settingOtherClearCacheDialogCleanComplete.toResString())
                }
                Thread.sleep(DURATION_HINT_DIALOG)
                val content = String.format(R.string.settingOtherClearCacheContent.toResString(), getCacheSize())
                runOnUiThread { item.content = content }
            },{
                mAdapter.notifyChange(item)
                mLongTaskDialog.hide()
            }).execute()
        }
        Async({
            return@Async getCacheSize()
        },{
            cacheItem.content = String.format(R.string.settingOtherClearCacheContent.toResString(), getCacheSize())
            mAdapter.notifyChange(cacheItem)
        }).execute()
        mSettingList.add(cacheItem)
        mSettingList.add(SimpleSettingItem(R.drawable.ic_lock,R.string.settingOtherPermissionDescriptionTitle.toResString(),R.string.settingOtherPermissionDescriptionContent.toResString()){
                _, _ ->
            toActivity(PermissionDescriptionActivity::class.java)
        })
    }
    private fun initRecyclerView(){
        settingRecyclerView.layoutManager =
            LinearLayoutManager(this)
        settingRecyclerView.adapter = mAdapter
    }

    /* Action */
    fun getCacheSize():Int{
        val folder = fileManager.cacheFolder
        return if (folder.exists() && folder.isDirectory){
            var tmpSize = 0L
            folder.listFiles().forEach {
                tmpSize += it.length()
            }
            return (tmpSize / (1024*1024)).toInt()
        }else{
            0
        }
    }
    fun clearCache(){
        val folder = fileManager.cacheFolder
        if (folder.exists() && folder.isDirectory){
            val iterator = folder.listFiles().iterator()
            while (iterator.hasNext()){
                iterator.next().delete()
            }
        }
    }

    /* 内部类 */
    private class Async<T>(var backgroundAction:()->T, var foregroundAction:(T)->Unit) : AsyncTask<Any,Unit,T>(){
        override fun doInBackground(vararg params: Any?):T {
            return backgroundAction.invoke()
        }

        override fun onPostExecute(result: T) {
            foregroundAction.invoke(result)
        }
    }


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

        fun notifyChange(item:BaseSettingItem){
            mSettingList.forEachIndexed { index, baseSettingItem ->
                if (item == baseSettingItem){
                    notifyItemChanged(index)
                    return
                }
            }
        }

    }

    private fun Collection<Resolution>.toFormatString(): String{
        val stringBuilder = StringBuilder()
        val iterator = this.iterator()
        while (iterator.hasNext()){
            val resolution = iterator.next()
            stringBuilder.append(resolution.toResolutionStringID().toResString())
            if (iterator.hasNext()){
                stringBuilder.append(", ")
            }
        }
        return stringBuilder.toString()
    }

    open class BaseSettingItem
    class DiverSettingItem : BaseSettingItem()
    class CatalogSettingItem(var title:String) : BaseSettingItem()
    class SimpleSettingItem(var icon:Int, var title:String, var content:String, var clickAction:((SimpleSettingItem,Int)->Unit)?):BaseSettingItem()
    class CheckBoxSettingItem(var icon:Int, var title:String, var content:String, var isCheck:Boolean,var clickAction:((CheckBoxSettingItem,CheckBox,Int)->Unit)?):BaseSettingItem()

}