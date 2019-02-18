package com.creepersan.bingimage.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import com.creepersan.bingimage.R

class PreviewDownloadResolutionItemHolder(context: Context, parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_preview_download_resolution,parent,false)){
    val checkBox = itemView as RadioButton

    fun setTitle(content:String){
        checkBox.text = content
    }

    fun setCheck(isCheck:Boolean){
        checkBox.isChecked = isCheck
    }

    fun isCheck():Boolean{
        return checkBox.isChecked
    }

    fun setOnClickListener(listener: View.OnClickListener){
        checkBox.setOnClickListener(listener)
    }



}