package com.github.forjrking.image.core

import android.graphics.drawable.Drawable

class OnImageListener {
    internal var onFailAction: ((String?) -> Unit)? = null
    internal var onSuccessAction: ((Drawable?) -> Unit)? = null

    fun onFail(action: (String?) -> Unit) {
        onFailAction = action
    }

    fun onSuccess(action: (Drawable?) -> Unit) {
        onSuccessAction = action
    }
}