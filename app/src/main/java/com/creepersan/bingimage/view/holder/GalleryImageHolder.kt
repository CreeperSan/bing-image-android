package com.creepersan.bingimage.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.creepersan.bingimage.R
import com.creepersan.bingimage.utils.setGone
import com.creepersan.bingimage.utils.setVisible

class GalleryImageHolder(context: Context, parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gallery_image,parent,false)){

    val image = itemView.findViewById<ImageView>(R.id.itemGalleryImageImage)
    val text = itemView.findViewById<TextView>(R.id.itemGalleryText)

    fun setOnClickListener(listenr: View.OnClickListener){
        itemView.setOnClickListener(listenr)
    }

    fun setText(content:String){
        if (content == ""){
            text.setGone()
        }else{
            text.text = content
            text.setVisible()
        }
    }

}