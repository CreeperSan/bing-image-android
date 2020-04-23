package com.creepersan.bingimage.view.holder.setting

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.creepersan.bingimage.R
import com.creepersan.bingimage.activity.SettingActivity

class SettingCatalogHolder(context:Context, parent:ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_setting_catalog,parent,false)){

    val title = itemView as TextView

    fun initHolder(item:SettingActivity.CatalogSettingItem){
        title.text = item.title
    }

}