package com.creepersan.bingimage.view.holder.setting

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.bingimage.R
import com.creepersan.bingimage.activity.SettingActivity

class SettingCheckBoxHolder(context:Context, parent:ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_setting_checkbox,parent,false)){
    val title = itemView.findViewById<TextView>(R.id.itemSettingCheckBoxTitle)
    val content = itemView.findViewById<TextView>(R.id.itemSettingCheckBoxContent)
    val icon = itemView.findViewById<ImageView>(R.id.itemSettingCheckBoxIcon)
    val checkBox = itemView.findViewById<CheckBox>(R.id.itemSettingCheckBoxCheckBox)

    fun initHolder(item:SettingActivity.CheckBoxSettingItem){
        title.text = item.title
        content.text = item.content
        icon.setImageResource(item.icon)
        checkBox.isChecked = item.isCheck
        checkBox.setOnClickListener(null)
        itemView.setOnClickListener {
            item.clickAction?.invoke(item, checkBox, adapterPosition)
        }
    }


}
