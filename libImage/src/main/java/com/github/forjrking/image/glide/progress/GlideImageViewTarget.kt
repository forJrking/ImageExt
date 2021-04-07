package com.github.forjrking.image.glide.progress

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition

class GlideImageViewTarget(view: ImageView?, var url: Any?) : DrawableImageViewTarget(view) {

    override fun onLoadFailed(errorDrawable: Drawable?) {
        url?.apply {
            ProgressManager.getProgressListener(this.toString())?.let {
                it.invoke(false, 100, 0, 0)
                ProgressManager.removeListener(this.toString())
            }
        }
        super.onLoadFailed(errorDrawable)
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        url?.apply {
            ProgressManager.getProgressListener(this.toString())?.let {
                it.invoke(true, 100, 0, 0)
                ProgressManager.removeListener(this.toString())
            }
        }
        super.onResourceReady(resource, transition)
    }
}