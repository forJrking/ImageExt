package com.github.forjrking.image.glide.progress


//interface OnProgressListener {
//    fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long)
//}

//给函数起别名 便于调用和书写
typealias OnProgressListener = ((isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) -> Unit)?