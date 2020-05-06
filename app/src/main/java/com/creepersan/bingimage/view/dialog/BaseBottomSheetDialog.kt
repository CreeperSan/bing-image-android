package com.creepersan.bingimage.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.creepersan.bingimage.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

open class BaseBottomSheetDialog(
    context: Context,
    private val viewResID: Int,
    private var title: String = "",
    private var dismissWhenTouchOutSide: Boolean = true,
    private var positiveButtonText: String = "",
    private var negativeButtonText: String = "",
    private var neutralButtonText: String = "",
    private var isAllowSlideToClose: Boolean = true
) : BottomSheetDialog(context) {
    companion object{
        private const val TAG = "BaseBottomSheetDialog"
    }

    private lateinit var mDialogRootView: View
    private lateinit var mDialogView: View
    private lateinit var mDialogBottomBehaviour : BottomSheetBehavior<View>

    init {
        // 初始化 View
        mDialogRootView = layoutInflater.inflate(R.layout.dialog_base_bottom_sheet, null)
        findRootViews(mDialogRootView)
        if (!::mDialogView.isInitialized){
            mDialogView = LayoutInflater.from(context).inflate(viewResID, mContentLayout, true)
        }

        // 初始化
        initBaseTitle()
        initBaseButtons()
        initBaseDialogListener()
        initBaseDialogAttrs()

        // 生命周期回调
        setContentView(mDialogRootView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View初始化
        onViewReady(mDialogView)
    }

    /**
     * 拓展生命周期
     */

    protected open fun onViewReady(view: View){}

    protected open fun onPositiveButtonClick(){
        dismiss()
    }

    protected open fun onNegativeButtonClick(){
        dismiss()
    }

    protected open fun onNeutralButtonClick(){
        dismiss()
    }

    /**
     * 初始化方法
     */
    private lateinit var mRootLayout : CoordinatorLayout
    private lateinit var mTitleTextView : TextView
    private lateinit var mContentLayout : ConstraintLayout
    private lateinit var mNeutralButton : Button
    private lateinit var mNegativeButton : Button
    private lateinit var mPositiveButton : Button
    private fun findRootViews(rootView: View){
        mRootLayout = rootView.findViewById(R.id.dialog_base_root_layout)
        mTitleTextView = rootView.findViewById(R.id.dialogBaseTitle)
        mContentLayout = rootView.findViewById(R.id.dialogBaseContentLayout)
        mNeutralButton = rootView.findViewById(R.id.dialogBaseNeutralButton)
        mNegativeButton = rootView.findViewById(R.id.dialogBaseNegativeButton)
        mPositiveButton = rootView.findViewById(R.id.dialogBasePositiveButton)
    }

    private fun initBaseTitle(){
        mTitleTextView.visibility = if (title.isEmpty()) View.GONE else View.VISIBLE
        mTitleTextView.text = title
    }

    private fun initBaseButtons(){
        // 初始化按钮
        mPositiveButton.text = positiveButtonText
        mPositiveButton.visibility = if (positiveButtonText.isNotEmpty()) View.VISIBLE else View.GONE
        mPositiveButton.setOnClickListener {
            onPositiveButtonClick()
        }
        mNegativeButton.text = negativeButtonText
        mNegativeButton.visibility = if (negativeButtonText.isNotEmpty()) View.VISIBLE else View.GONE
        mNegativeButton.setOnClickListener {
            onNegativeButtonClick()
        }
        mNeutralButton.text = neutralButtonText
        mNeutralButton.visibility = if (neutralButtonText.isNotEmpty()) View.VISIBLE else View.GONE
        mNeutralButton.setOnClickListener {
            onNeutralButtonClick()
        }
    }

    private fun initBaseDialogListener(){
        // 初始化监听
        mDialogBottomBehaviour = BottomSheetBehavior.from(mDialogView.parent as View)
        mDialogBottomBehaviour.peekHeight = context.resources.displayMetrics.heightPixels
        mDialogBottomBehaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset.toInt() == BottomSheetBehavior.STATE_HIDDEN){
                    dismiss()
                }

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    private fun initBaseDialogAttrs(){
        // 对话框属性设置
        setCanceledOnTouchOutside(dismissWhenTouchOutSide)
        setCancelable(dismissWhenTouchOutSide)
        mDialogBottomBehaviour.isHideable = false
    }

    /**
     * 重载的一些方法
     */

    override fun dismiss() {
        super.dismiss()
        mDialogBottomBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    /**
     * 事件回调
     */

    interface OnBottomSheetButtonClickListener{

        fun onClick(dialog: BaseBottomSheetDialog): Boolean

    }

}