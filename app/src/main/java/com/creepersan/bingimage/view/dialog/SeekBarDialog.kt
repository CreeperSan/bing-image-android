package com.creepersan.bingimage.view.dialog

import android.content.Context
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.creepersan.bingimage.R
import kotlin.math.abs

class SeekBarDialog(
    context: Context,
    title: String,
    private var hint: String = "",
    private var min: Int = 0,
    private var progress : Int = 0,
    private var max : Int = 0,
    private var onSeek: ((dialog:SeekBarDialog, fromUser:Boolean) -> Unit)? = null,
    private var onStartSeek: ((dialog:SeekBarDialog) -> Unit)? = null,
    private var onStopSeek: ((dialog:SeekBarDialog) -> Unit)? = null,
    private var onConfirm: ((dialog:SeekBarDialog) -> Boolean)? = null
) : BaseBottomSheetDialog(
    context,
    R.layout.dialog_seekbar,
    title = title,
    positiveButtonText = "确定"
), SeekBar.OnSeekBarChangeListener {

    private lateinit var mSeekBar: SeekBar
    private lateinit var mHintTextView: TextView

    override fun onViewReady(view: View) {
        super.onViewReady(view)
        findViews(view)
        initSeekBar()
        initHintText()
    }

    private fun findViews(view: View){
        mSeekBar = view.findViewById(R.id.dialogSeekBarSeekBar)
        mHintTextView = view.findViewById(R.id.dialogSeekBarHint)
    }

    private fun initSeekBar(){
        setMin(min)
        setMax(max)
        setProgress(progress)
        mSeekBar.setOnSeekBarChangeListener(this)
    }

    private fun initHintText(){
        mHintTextView.text = hint
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        onSeek?.invoke(this, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        onStartSeek?.invoke(this)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        onStopSeek?.invoke(this)
    }

    override fun onPositiveButtonClick() {
        if (onConfirm?.invoke(this) != true){
            super.onPositiveButtonClick()
        }
    }

    /**
     * 下面是一些供外部操作的方法
     */

    fun setHint(hint: String){
        mHintTextView.text = hint
    }

    fun setProgress(progress: Int){
        mSeekBar.progress = progress - min
    }

    fun setMax(max: Int){
        mSeekBar.progress = max - min
    }

    fun setMin(min: Int){
        val minDiff = min - this.min
        this.min = min
        this.max = max + minDiff
        this.progress = progress + minDiff
        mSeekBar.progress = progress
        mSeekBar.max = max

    }

    fun getProgress(): Int{
        return mSeekBar.progress - min
    }

}