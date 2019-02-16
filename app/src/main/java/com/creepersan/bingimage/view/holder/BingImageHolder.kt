package com.creepersan.bingimage.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.creepersan.bingimage.R

class BingImageHolder(context:Context, parent:ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_image,parent,false)){
    val image = itemView.findViewById<ImageView>(R.id.itemMainImageImage)

    fun setImageByUrl(url:String){
        Glide.with(image).load(url).into(image)
    }

    fun setOnClickListener(listener: View.OnClickListener){
        image.setOnClickListener(listener)
    }

}
