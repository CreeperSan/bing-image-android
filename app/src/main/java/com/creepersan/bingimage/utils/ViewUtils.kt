package com.creepersan.bingimage.utils

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

/*  TextView  */

fun TextView.setTextOrDisappear(content:String){
    if (content.isEmpty()){
        setGone()
    }else{
        text = content
        setVisible()
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

/* View */
fun View.setVisible(){
    if(this.visibility != View.VISIBLE){
        this.visibility = View.VISIBLE
    }
}

fun View.setGone(){
    this.visibility = View.GONE
}