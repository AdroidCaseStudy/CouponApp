package com.cg.couponsapp.utils

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar

class MakeProgressBar (view : View){

    val layout = view.findViewById<FrameLayout>(android.R.id.content)
    val pBar = ProgressBar(view.context,null,android.R.attr.progressBarStyleLarge)
    val params = FrameLayout.LayoutParams(175,175)


    fun make() : ProgressBar{
        params.gravity = Gravity.CENTER
        layout.addView(pBar, params)
        return pBar
    }
}