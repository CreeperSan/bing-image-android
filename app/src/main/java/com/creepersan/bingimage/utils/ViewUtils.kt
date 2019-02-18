package com.creepersan.bingimage.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

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

fun ImageView.setImageGlide(url:String){
    Glide.with(this).load(url).into(this)
}

/* RecyclerView */
fun RecyclerView.isReachTop():Boolean{
    return !this.canScrollVertically(1)
}

fun RecyclerView.isReachBottom():Boolean{
    return !this.canScrollVertically(1)
}