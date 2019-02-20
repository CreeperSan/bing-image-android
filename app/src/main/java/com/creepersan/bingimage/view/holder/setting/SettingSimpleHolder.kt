package com.creepersan.bingimage.view.holder.setting

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.bingimage.R
import com.creepersan.bingimage.activity.SettingActivity

class SettingSimpleHolder(context:Context, parent:ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_setting_simple,parent,false)){
    val title = itemView.findViewById<TextView>(R.id.itemSettingSimpleTitle)
    val content = itemView.findViewById<TextView>(R.id.itemSettingSimpleContent)
    val icon = itemView.findViewById<ImageView>(R.id.itemSettingSimpleIcon)

    fun initHolder(item:SettingActivity.SimpleSettingItem){
        title.text = item.title
        content.text = item.content
        icon.setImageResource(item.icon)
        itemView.setOnClickListener {
            item.clickAction?.invoke(item, adapterPosition)
        }
    }


}
