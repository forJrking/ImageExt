package com.github.forjrking.image.glide.progress


interface OnProgressListener {
    fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long)
}