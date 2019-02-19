package com.creepersan.bingimage.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.creepersan.bingimage.R

class MainDrawerSelectionHolder(context: Context, parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_drawer_selection,parent,false)){
    private val icon = itemView.findViewById<ImageView>(R.id.itemMainDrawerSelectionIcon)
    private val title = itemView.findViewById<TextView>(R.id.itemMainDrawerSelectionText)
    private val hint = itemView.findViewById<ImageView>(R.id.itemMainDrawerSelectionIconHint)

    fun setHint(iconID:Int = 0){
        if (iconID == 0){
            hint.visibility = View.GONE
        }else{
            hint.setImageResource(iconID)
            hint.visibility = View.VISIBLE
        }
    }

    fun setIcon(iconID:Int){
        icon.setImageResource(iconID)
    }

    fun setTitle(content:String){
        title.text = content
    }

    fun setOnClickListener(action:()->Unit){
        itemView.setOnClickListener { action.invoke() }
    }

}