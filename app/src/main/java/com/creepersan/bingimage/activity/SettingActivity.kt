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
import com.creepersan.bingimage.view.holder.setting.SettingCatalogHolder
import com.creepersan.bingimage.view.holder.setting.SettingCheckBoxHolder
import com.creepersan.bingimage.view.holder.setting.SettingDiverHolder
import com.creepersan.bingimage.view.holder.setting.SettingSimpleHolder
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.File
import java.lang.IllegalStateException
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


    private val mResolutionList by lazy {
        return@lazy ArrayList<Pair<String, Resolution>>().apply {
            fun add(resolution: Resolution){
                add(Pair(resolution.toResolutionStringID().toResString(), resolution))
            }
            add(Resolution.L_1920_1200)
            add(Resolution.L_1920_1080)
            add(Resolution.L_1366_768)
            add(Resolution.L_1280_720)
            add(Resolution.L_1024_768)
            add(Resolution.L_800_600)
            add(Resolution.L_800_480)
            add(Resolution.L_640_480)
            add(Resolution.L_400_240)
            add(Resolution.L_320_240)
            add(Resolution.P_1080_1920)
            add(Resolution.P_768_1366)
            add(Resolution.P_768_1280)
            add(Resolution.P_720_1280)
            add(Resolution.P_480_800)
            add(Resolution.P_480_640)
            add(Resolution.P_240_400)
            add(Resolution.P_240_320)
        }
    }

    private val mProgressBarDialog by lazy {
        var seekbarChangeAction : ((SeekBar, Dialog)->Unit)? = null
        var posButtonClickAction : (()->Unit)? = null
        val progressBarDialogView = layoutInflater.inflate(R.layout.dialog_setting_progressbar, null)
        val seekBar = progressBarDialogView.findViewById<SeekBar>(R.id.dialogDownloadProgressSeekBar)
        val textView = progressBarDialogView.findViewById<TextView>(R.id.dialogDownloadProgressText)
        val dialog = AlertDialog.Builder(this)
            .setPositiveButton(R.string.settingDialogConfirm.toResString()) { dialog, which ->
                posButtonClickAction?.invoke()
            }
            .setNegativeButton(R.string.settingDialogCancel.toResString()) { dialog, which ->
                dialog.dismiss()
            }
            .setView(progressBarDialogView)
            .create()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    seekbarChangeAction?.invoke(seekBar, dialog)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        return@lazy object {
            fun setTitle(title: String){
                dialog.setTitle(title)
            }
            fun setProgressMax(progress:Int){
                seekBar.max = progress
            }
            fun getProgressMax():Int{
                return seekBar.max
            }
            fun setProgress(progress:Int){
                seekBar.progress = progress
            }
            fun getProgress():Int{
                return seekBar.progress
            }
            fun setHint(hint:String){
                textView.text = hint
            }
            fun onSeekBarChangeAction(action:((SeekBar, Dialog)->Unit)?){
                seekbarChangeAction = action
            }
            fun onPosButtonClickActon(action:(()->Unit)?){
                posButtonClickAction = action
            }
            fun dismiss(){
                dialog.dismiss()
            }
            fun show(){
                dialog.show()
            }
        }
    }
    private val mSingleSelectDialog by lazy {
        var onClickAction:(()->Unit)? = null
        val adapter = DialogSingleListAdapter()
        val layoutManager =
            LinearLayoutManager(this)
        val recyclerView = layoutInflater.inflate(R.layout.dialog_setting_single_list, null) as RecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val dialog = AlertDialog.Builder(this)
            .setPositiveButton(R.string.settingDialogConfirm){ dialog, _ ->
                onClickAction?.invoke()
            }
            .setNegativeButton(R.string.settingDialogCancel){ dialog, _ ->
                dialog.dismiss()
            }
            .setView(recyclerView)
            .create()
        return@lazy object {
            fun setData(list:List<Pair<String, Any>>){
                adapter.list = list
                adapter.notifyDataSetChanged()
            }
            fun getAdapter():DialogSingleListAdapter{
                return adapter
            }
            fun show(){
                dialog.show()
            }
            fun hide(){
                dialog.hide()
            }
            fun getSelection():Any?{
                return adapter.selection
            }
            fun setSelection(selection:Any?){
                adapter.selection = selection
                adapter.notifyDataSetChanged()
            }
            fun setTitle(title:String){
                dialog.setTitle(title)
            }
            fun setOnConfirmAction(action:(()->Unit)?){
                onClickAction = action
            }
        }
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
        mSettingList.add(CheckBoxSettingItem(R.drawable.ic_exit_to_app,R.string.settingBasicDoubleClickTitle.toResString(),R.string.settingBasicDoubleClickContent.toResString(), config.isDoubleClickExit()) {
            item, checkBox, pos ->
            val newCheckState = !checkBox.isChecked
            checkBox.isChecked = newCheckState
            item.isCheck = newCheckState
            config.setDoubleClickExit(newCheckState)
            mAdapter.notifyItemChanged(pos)
        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_cached,R.string.settingOtherClearCacheSizeTitle.toResString(),"${config.getCacheSize()} M"){
                item, pos ->
            mProgressBarDialog.setProgressMax(1024)
            mProgressBarDialog.setProgress(config.getCacheSize())
            mProgressBarDialog.setTitle(item.title)
            mProgressBarDialog.setHint(item.content)
            mProgressBarDialog.onSeekBarChangeAction { seekBar, dialog ->
                val progress = seekBar.progress
                mProgressBarDialog.setHint("$progress M")
            }
            mProgressBarDialog.onPosButtonClickActon {
                val progress = mProgressBarDialog.getProgress()
                config.setCacheSize(progress)
                item.content = "$progress M"
                mAdapter.notifyItemChanged(pos)
            }
            mProgressBarDialog.show()
        })
        /* 预览设置 */
        mSettingList.add(DiverSettingItem())
        mSettingList.add(CatalogSettingItem(R.string.settingViewSettingCatalog.toResString()))
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewResolutionTitle.toResString(),config.getListResolution().toResolutionStringID().toResString()){
            item, pos ->
            mSingleSelectDialog.setData(mResolutionList)
            mSingleSelectDialog.setSelection(config.getListResolution()) //
            mSingleSelectDialog.setTitle(item.title)
            mSingleSelectDialog.setOnConfirmAction{
                val selection = mSingleSelectDialog.getSelection()
                if (selection!=null && selection is Resolution){
                    config.setListResolution(selection) //
                    item.content = config.getListResolution().toResolutionStringID().toResString() //
                    mAdapter.notifyItemChanged(pos)
                }else{
                    toast(R.string.settingErrorToast.toResString())
                }
            }
            mAdapter.notifyItemChanged(pos)
            mSingleSelectDialog.show()
        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewPreviewTitle.toResString(),config.getPreviewResolution().toResolutionStringID().toResString()){
            item, pos ->
            mSingleSelectDialog.setData(mResolutionList)
            mSingleSelectDialog.setSelection(config.getPreviewResolution()) //
            mSingleSelectDialog.setTitle(item.title)
            mSingleSelectDialog.setOnConfirmAction{
                val selection = mSingleSelectDialog.getSelection()
                if (selection!=null && selection is Resolution){
                    config.setPreviewResolution(selection) //
                    item.content = config.getPreviewResolution().toResolutionStringID().toResString() //
                    mAdapter.notifyItemChanged(pos)
                }else{
                    toast(R.string.settingErrorToast.toResString())
                }
            }
            mAdapter.notifyItemChanged(pos)
            mSingleSelectDialog.show()
        })
        mSettingList.add(SimpleSettingItem(R.drawable.ic_screen_resolution,R.string.settingViewSettingViewDownloadTitle.toResString(),config.getDownloadResolution().toResolutionStringID().toResString()){
            item, pos ->
            mSingleSelectDialog.setData(mResolutionList)
            mSingleSelectDialog.setSelection(config.getDownloadResolution()) //
            mSingleSelectDialog.setTitle(item.title)
            mSingleSelectDialog.setOnConfirmAction{
                val selection = mSingleSelectDialog.getSelection()
                if (selection!=null && selection is Resolution){
                    config.setDownloadResolution(selection) //
                    item.content = config.getDownloadResolution().toResolutionStringID().toResString() //
                    mAdapter.notifyItemChanged(pos)
                }else{
                    toast(R.string.settingErrorToast.toResString())
                }
            }
            mAdapter.notifyItemChanged(pos)
            mSingleSelectDialog.show()
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

    private inner class DialogSingleListAdapter : RecyclerView.Adapter<DialogSingleListViewHolder>(){
        var list:List<Pair<String, Any>>? = null
        var selection:Any? = null

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DialogSingleListViewHolder {
            return DialogSingleListViewHolder(p0)
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        override fun onBindViewHolder(holder: DialogSingleListViewHolder, p1: Int) {
            if (list == null){
                return
            }
            val pos = holder.adapterPosition
            val item = list!![pos]
            holder.setCheck(item.second == selection)
            holder.setText(item.first)
            holder.setOnClickListener(View.OnClickListener {
                var index = 0
                for(tmp in list!!){
                    if (tmp.second == selection){
                        notifyItemChanged(index)
                        break
                    }
                    index++
                }
                selection = item.second
                notifyItemChanged(pos)
            })
        }

    }
    private inner class DialogSingleListViewHolder(group: ViewGroup) : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_setting_dialog_single_list,group,false)){
        val checkbox = itemView as RadioButton
        fun setText(content: String){
            checkbox.text = content
        }
        fun setCheck(isCheck: Boolean){
            checkbox.isChecked = isCheck
        }
        fun isCheck():Boolean{
            return checkbox.isChecked
        }
        fun setOnClickListener(listener: View.OnClickListener){
            checkbox.setOnClickListener(listener)
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

    open class BaseSettingItem
    class DiverSettingItem : BaseSettingItem()
    class CatalogSettingItem(var title:String) : BaseSettingItem()
    class SimpleSettingItem(var icon:Int, var title:String, var content:String, var clickAction:((SimpleSettingItem,Int)->Unit)?):BaseSettingItem()
    class CheckBoxSettingItem(var icon:Int, var title:String, var content:String, var isCheck:Boolean,var clickAction:((CheckBoxSettingItem,CheckBox,Int)->Unit)?):BaseSettingItem()

}