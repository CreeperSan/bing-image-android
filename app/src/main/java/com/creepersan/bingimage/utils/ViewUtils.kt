package com.creepersan.bingimage.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

/*  TextView  */

fun TextView.setTextOrDisappear(content:String){
    if (content.isEmpty()){
        visibility = View.GONE
    }else{
        text = content
        visibility = View.VISIBLE
    }
}

/*  ImageView  */

fun ImageView.setImageGlide(url:String, placeHolder:Int=0, errorHolder:Int=0, scaleType:ImageView.ScaleType?=null){
    Glide
        .with(this)
        .load(url)
        .placeholder(placeHolder)
        .error(errorHolder)
        .transition(withCrossFade())
        .apply{
            when(scaleType){
                ImageView.ScaleType.FIT_CENTER -> fitCenter()
                ImageView.ScaleType.CENTER_CROP -> centerCrop()
                ImageView.ScaleType.CENTER_INSIDE -> centerInside()
            }
        }
        .into(this)
}

/* RecyclerView */
fun RecyclerView.isReachTop():Boolean{
    return !this.canScrollVertically(1)
}

fun RecyclerView.isReachBottom():Boolean{
    return !this.canScrollVertically(1)
}