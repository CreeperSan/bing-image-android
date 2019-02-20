package com.creepersan.bingimage.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.creepersan.bingimage.R

class GalleryImageHolder(context: Context, parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gallery_image,parent,false)){

    val image = itemView.findViewById<ImageView>(R.id.itemGalleryImageImage)

    fun setOnClickListener(listenr: View.OnClickListener){
        itemView.setOnClickListener(listenr)
    }

}