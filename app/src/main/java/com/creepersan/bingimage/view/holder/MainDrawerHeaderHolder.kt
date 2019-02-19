package com.creepersan.bingimage.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.creepersan.bingimage.R

class MainDrawerHeaderHolder(context: Context, parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_drawer_header,parent,false)){
    val text = itemView.findViewById<TextView>(R.id.itemMainDrawerHeaderText)

    fun setText(content:String){
        text.text = content
    }

}