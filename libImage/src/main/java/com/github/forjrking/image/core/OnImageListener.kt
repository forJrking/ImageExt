package com.github.forjrking.image.core

import android.graphics.drawable.Drawable

interface OnImageListener {

    fun onSuccess(drawable: Drawable?)

    fun onFail(msg: String?)
}