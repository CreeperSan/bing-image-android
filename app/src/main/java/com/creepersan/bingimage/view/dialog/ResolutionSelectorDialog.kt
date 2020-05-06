package com.creepersan.bingimage.view.dialog

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.creepersan.bingimage.R
import com.creepersan.bingimage.database.bean.BingImage
import com.creepersan.bingimage.utils.setGone
import com.creepersan.bingimage.utils.setVisible
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ResolutionSelectorDialog (
    context: Context,
    title: String,
    private var isSingleSelection: Boolean = true,
    private var resolutionList: List<BingImage.Resolution> = listOf(),
    private var pinnedResolutionList: List<BingImage.Resolution> = listOf(),
    private var onResolutionSelected: ((selectedResolutionList: List<BingImage.Resolution>)->Boolean)? = null,
    private var showDoNotShowNextCheckbox: Boolean = false,
    private var onCheckedChange: ((newState:Boolean)->Unit)? = null
) : BaseBottomSheetDialog(
    context,
    R.layout.dialog_resolution_selector,
    title = title,
    positiveButtonText = "确定"
){

    private lateinit var mHintTextView: TextView
    private lateinit var mShowAllResolutionTextView: TextView
    private lateinit var mDoNotShowDialogNextTimeCheckbox: CheckBox
    private lateinit var mResolutionGroupLayout: ChipGroup
    private lateinit var mResolution1920x1200Chip: Chip
    private lateinit var mResolution1920x1080Chip: Chip
    private lateinit var mResolution1366x768Chip: Chip
    private lateinit var mResolution1280x720Chip: Chip
    private lateinit var mResolution1024x768Chip: Chip
    private lateinit var mResolution800x600Chip: Chip
    private lateinit var mResolution800x480Chip: Chip
    private lateinit var mResolution640x480Chip: Chip
    private lateinit var mResolution400x240Chip: Chip
    private lateinit var mResolution320x240Chip: Chip
    private lateinit var mResolution1080x1920Chip: Chip
    private lateinit var mResolution768x1366Chip: Chip
    private lateinit var mResolution768x1280Chip: Chip
    private lateinit var mResolution720x1280Chip: Chip
    private lateinit var mResolution480x800Chip: Chip
    private lateinit var mResolution480x640Chip: Chip
    private lateinit var mResolution240x400Chip: Chip
    private lateinit var mResolution240x320Chip: Chip

    override fun onViewReady(view: View) {
        super.onViewReady(view)
        findViews(view)
        initHintTextView()
        initCheckbox()
        initChipGroup()
        initResolutionChipVisible()
        initDoNotShowCheckbox()
        initShowAllResolution()
    }

    private fun findViews(view: View){
        mHintTextView = view.findViewById(R.id.dialogResolutionSelectorMessageTextView)
        mShowAllResolutionTextView = view.findViewById(R.id.dialogResolutionSelectorShowAllResolution)
        mDoNotShowDialogNextTimeCheckbox = view.findViewById(R.id.dialogResolutionSelectorDoNotShowNextTime)
        mResolutionGroupLayout = view.findViewById(R.id.dialogResolutionSelectorChipGroup)
        mResolution1920x1200Chip = view.findViewById(R.id.dialogResolutionSelectorChip1920x1200)
        mResolution1920x1080Chip = view.findViewById(R.id.dialogResolutionSelectorChip1920x1080)
        mResolution1366x768Chip = view.findViewById(R.id.dialogResolutionSelectorChip1366x768)
        mResolution1280x720Chip = view.findViewById(R.id.dialogResolutionSelectorChip1280x720)
        mResolution1024x768Chip = view.findViewById(R.id.dialogResolutionSelectorChip1024x768)
        mResolution800x600Chip = view.findViewById(R.id.dialogResolutionSelectorChip800x600)
        mResolution800x480Chip = view.findViewById(R.id.dialogResolutionSelectorChip800x480)
        mResolution640x480Chip = view.findViewById(R.id.dialogResolutionSelectorChip640x480)
        mResolution400x240Chip = view.findViewById(R.id.dialogResolutionSelectorChip400x240)
        mResolution320x240Chip = view.findViewById(R.id.dialogResolutionSelectorChip320x240)
        mResolution1080x1920Chip = view.findViewById(R.id.dialogResolutionSelectorChip1080x1920)
        mResolution768x1366Chip = view.findViewById(R.id.dialogResolutionSelectorChip768x1366)
        mResolution768x1280Chip = view.findViewById(R.id.dialogResolutionSelectorChip768x1280)
        mResolution720x1280Chip = view.findViewById(R.id.dialogResolutionSelectorChip720x1280)
        mResolution480x800Chip = view.findViewById(R.id.dialogResolutionSelectorChip480x800)
        mResolution480x640Chip = view.findViewById(R.id.dialogResolutionSelectorChip480x640)
        mResolution240x400Chip = view.findViewById(R.id.dialogResolutionSelectorChip240x400)
        mResolution240x320Chip = view.findViewById(R.id.dialogResolutionSelectorChip240x320)
    }

    private fun initHintTextView(){
        mHintTextView.setText(if(isSingleSelection){ R.string.dialogResolutionSelectorHint }else{ R.string.dialogResolutionSelectorHintSupportMultiSelect })
    }

    private fun initCheckbox(){
        mDoNotShowDialogNextTimeCheckbox.visibility = if (showDoNotShowNextCheckbox) View.VISIBLE else View.GONE
        mDoNotShowDialogNextTimeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange?.invoke(isChecked)
        }
    }

    private fun initChipGroup(){
        mResolutionGroupLayout.isSingleSelection = isSingleSelection
    }

    private fun initResolutionChipVisible(){
        // 先隐藏所有的chip
        getAllResolutionChipList().forEach { chip ->
            chip.setGone()
        }
        // 展示已选中的分辨率的chip
        resolutionList.forEach {  resolutions ->
            resolutions.toChip()?.apply {
                setVisible()
                isChecked = true
            }
        }
        // 固定几个分辨率是一定展示的
        pinnedResolutionList.forEach {  resolution ->
            resolution.toChip()?.setVisible()
        }
    }

    private fun initDoNotShowCheckbox(){

    }

    private fun initShowAllResolution(){
        // 检查是否选中了所有的标签都显示了
        var hasHiddenChip = false
        for (chip in getAllResolutionChipList()){
            if (chip.visibility == View.GONE){
                hasHiddenChip = true
                break
            }
        }
        if (!hasHiddenChip){
            mShowAllResolutionTextView.setGone()
        }
        // 点击事件
        mShowAllResolutionTextView.setOnClickListener { self ->
            // 隐藏自己
            self.setGone()
            // 显示所有chip
            getAllResolutionChipList().forEach { chip ->
                chip.setVisible()
            }
        }
    }

    override fun onPositiveButtonClick() {
        val resultResolutionList = arrayListOf<BingImage.Resolution>()
        // 检查是那些被勾上的
        getAllResolutionChipList().forEach {  chip ->
            if (chip.isChecked){
                chip.toResolution()?.apply {
                    resultResolutionList.add(this)
                }
            }
        }
        // 防止为空
        if (onResolutionSelected?.invoke(resultResolutionList) != false){
            super.onPositiveButtonClick()
        }
    }

    private fun getAllResolutionChipList(): List<Chip>{
        return listOf(
            mResolution1920x1200Chip, mResolution1920x1080Chip, mResolution1366x768Chip,
            mResolution1280x720Chip, mResolution1024x768Chip, mResolution800x600Chip,
            mResolution800x480Chip, mResolution640x480Chip, mResolution400x240Chip,
            mResolution320x240Chip, mResolution1080x1920Chip, mResolution768x1366Chip,
            mResolution768x1280Chip, mResolution720x1280Chip, mResolution480x800Chip,
            mResolution480x640Chip, mResolution240x400Chip, mResolution240x320Chip
        )
    }

    /**
     * 内部方法
     */
    private fun BingImage.Resolution.toChip(): Chip?{
        return when (this) {
            BingImage.Resolution.L_1920_1200 -> mResolution1920x1200Chip
            BingImage.Resolution.L_1920_1080 -> mResolution1920x1080Chip
            BingImage.Resolution.L_1366_768 -> mResolution1366x768Chip
            BingImage.Resolution.L_1280_720 -> mResolution1280x720Chip
            BingImage.Resolution.L_1024_768 -> mResolution1024x768Chip
            BingImage.Resolution.L_800_600 -> mResolution800x600Chip
            BingImage.Resolution.L_800_480 -> mResolution800x480Chip
            BingImage.Resolution.L_640_480 -> mResolution640x480Chip
            BingImage.Resolution.L_400_240 -> mResolution400x240Chip
            BingImage.Resolution.L_320_240 -> mResolution320x240Chip
            BingImage.Resolution.P_1080_1920 -> mResolution1080x1920Chip
            BingImage.Resolution.P_768_1366 -> mResolution768x1366Chip
            BingImage.Resolution.P_768_1280 -> mResolution768x1280Chip
            BingImage.Resolution.P_720_1280 -> mResolution720x1280Chip
            BingImage.Resolution.P_480_800 -> mResolution480x800Chip
            BingImage.Resolution.P_480_640 -> mResolution480x640Chip
            BingImage.Resolution.P_240_400 -> mResolution240x400Chip
            BingImage.Resolution.P_240_320 -> mResolution240x320Chip
            else -> null
        }
    }

    private fun Chip.toResolution(): BingImage.Resolution?{
        return when(this){
            mResolution1920x1200Chip -> BingImage.Resolution.L_1920_1200
            mResolution1920x1080Chip -> BingImage.Resolution.L_1920_1080
            mResolution1366x768Chip -> BingImage.Resolution.L_1366_768
            mResolution1280x720Chip -> BingImage.Resolution.L_1280_720
            mResolution1024x768Chip -> BingImage.Resolution.L_1024_768
            mResolution800x600Chip -> BingImage.Resolution.L_800_600
            mResolution800x480Chip -> BingImage.Resolution.L_800_480
            mResolution640x480Chip -> BingImage.Resolution.L_640_480
            mResolution400x240Chip -> BingImage.Resolution.L_400_240
            mResolution320x240Chip -> BingImage.Resolution.L_320_240
            mResolution1080x1920Chip -> BingImage.Resolution.P_1080_1920
            mResolution768x1366Chip -> BingImage.Resolution.P_768_1366
            mResolution768x1280Chip -> BingImage.Resolution.P_768_1280
            mResolution720x1280Chip -> BingImage.Resolution.P_720_1280
            mResolution480x800Chip -> BingImage.Resolution.P_480_800
            mResolution480x640Chip -> BingImage.Resolution.P_480_640
            mResolution240x400Chip -> BingImage.Resolution.P_240_400
            mResolution240x320Chip -> BingImage.Resolution.P_240_320
            else -> null
        }
    }
}