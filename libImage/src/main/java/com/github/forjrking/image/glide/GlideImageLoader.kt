package com.github.forjrking.image.glide

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.util.Preconditions
import com.github.forjrking.image.core.ImageOptions
import com.github.forjrking.image.glide.progress.GlideImageViewTarget
import com.github.forjrking.image.glide.progress.ProgressManager
import com.github.forjrking.image.glide.transformation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

/**Glide封装*/
object GlideImageLoader {

    @JvmStatic
    fun loadImage(options: ImageOptions) {
        Preconditions.checkNotNull(options, "ImageConfigImpl is required")
        val context = options.context
        Preconditions.checkNotNull(context, "Context is required")
        Preconditions.checkNotNull(options.imageView, "ImageView is required")

        val requestsWith = glideRequests(context)

        //根据类型获取
        val glideRequest = when (options.res) {
            is String -> requestsWith.load(options.res as String)
            is Bitmap -> requestsWith.load(options.res as Bitmap)
            is Drawable -> requestsWith.load(options.res as Drawable)
            is Uri -> requestsWith.load(options.res as Uri)
            is URL -> requestsWith.load(options.res as URL)
            is File -> requestsWith.load(options.res as File)
            is Int -> requestsWith.load(options.res as Int)
            is ByteArray -> requestsWith.load(options.res as ByteArray)
            else -> requestsWith.load(options.res)
        }

        glideRequest.apply {
            // 缩略图大小
            if (options.thumbnail > 0f) {
                thumbnail(options.thumbnail)
            }
            // 缩略图请求
            options.thumbnailUrl?.let {
                thumbnail(glideRequests(context).load(options.thumbnailUrl))
            }
            //缓存配置
            val diskCacheStrategy = when (options.diskCacheStrategy) {
                ImageOptions.DiskCache.ALL -> DiskCacheStrategy.ALL
                ImageOptions.DiskCache.NONE -> DiskCacheStrategy.NONE
                ImageOptions.DiskCache.RESOURCE -> DiskCacheStrategy.RESOURCE
                ImageOptions.DiskCache.DATA -> DiskCacheStrategy.DATA
                ImageOptions.DiskCache.AUTOMATIC -> DiskCacheStrategy.AUTOMATIC
            }
            diskCacheStrategy(diskCacheStrategy)
            //优先级
            val priority = when (options.priority) {
                ImageOptions.LoadPriority.LOW -> Priority.LOW
                ImageOptions.LoadPriority.NORMAL -> Priority.NORMAL
                ImageOptions.LoadPriority.HIGH -> Priority.HIGH
                ImageOptions.LoadPriority.IMMEDIATE -> Priority.IMMEDIATE
            }
            priority(priority)
            skipMemoryCache(options.skipMemoryCache)

            if (options.placeHolderDrawable != null) {
                placeholder(options.placeHolderDrawable)
            }
            //设置占位符
            if (options.placeHolderResId != 0) {
                placeholder(options.placeHolderResId)
            }
            if (options.errorDrawable != null) {
                error(options.errorDrawable)
            }
            //设置错误的图片
            if (options.errorResId != 0) {
                error(options.errorResId)
            }
            //设置请求 url 为空图片
            if (options.errorDrawable != null) {
                error(options.errorDrawable)
            }
            if (options.fallbackResId != 0) {
                fallback(options.fallbackResId)
            }
            if (options.fallbackDrawable != null) {
                fallback(options.fallbackDrawable)
            }
            //目标尺寸
            val size = options.size
            size?.let {
                override(size.width, size.height)
            }

            val format = when (options.format) {
                Bitmap.Config.ARGB_8888 -> DecodeFormat.PREFER_ARGB_8888
                Bitmap.Config.RGB_565 -> DecodeFormat.PREFER_RGB_565
                else -> DecodeFormat.DEFAULT
            }
            //解码格式
            format(format)

            //region ========== 特效 ==========
            if (!options.isAnim) {
                dontAnimate()
            }
            if (options.isCrossFade) {
                val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                transition(DrawableTransitionOptions.withCrossFade(factory))
            }
            if (options.centerCrop) {
                centerCrop()
            }
            if (options.isFitCenter) {
                fitCenter()
            }

            if (options.isCircle || options.borderWidth > 0) {
                if (options.isCircle) {
                    transform(CircleWithBorderTransformation(options.borderWidth, options.borderColor))
                } else {
                    transform(BorderTransformation(options.borderWidth, options.borderColor))
                }
            }

            // 是否设置圆角特效
            if (options.isRoundedCorners) {
                var transformation: BitmapTransformation? = null
                // 圆角特效受到ImageView的scaleType属性影响
                val scaleType = options.imageView?.scaleType
                if (scaleType == ImageView.ScaleType.FIT_CENTER ||
                        scaleType == ImageView.ScaleType.CENTER_INSIDE ||
                        scaleType == ImageView.ScaleType.CENTER ||
                        scaleType == ImageView.ScaleType.CENTER_CROP) {
                    transformation = CenterCrop()
                }
                if (transformation == null) {
                    transform(RoundedCornersTransformation(options.roundRadius, 0, options.cornerType))
                } else {
                    transform(transformation, RoundedCornersTransformation(options.roundRadius, 0, options.cornerType))
                }
            }

            if (options.isBlur) {
                transform(BlurTransformation(options.imageView!!.context, options.blurRadius, options.blurSampling))
            }

            if (options.isGray) {
                transform(GrayscaleTransformation())
            }

            options.transformation?.let {
                transform(*options.transformation!!)
            }

            //endregion
            options.requestListener?.let {
                addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        options.requestListener?.onFailAction?.invoke(e.toString())
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        options.requestListener?.onSuccessAction?.invoke(resource)
                        return false
                    }
                })
            }

            into(GlideImageViewTarget(options.imageView, options.res))
        }

        options.onProgressListener?.let {
            ProgressManager.addListener(options.res.toString(), options.onProgressListener)
        }

    }

    private fun glideRequests(context: Any?): GlideRequests {
        return when (context) {
            is Context -> IGlideModule.with(context)
            is Activity -> IGlideModule.with(context)
            is FragmentActivity -> IGlideModule.with(context)
            is Fragment -> IGlideModule.with(context)
            is android.app.Fragment -> IGlideModule.with(context)
            is View -> IGlideModule.with(context)
            else -> throw NullPointerException("not support")
        }
    }

    /*** 清除本地缓存*/
    suspend fun clearDiskCache(context: Context) = withContext(Dispatchers.IO) {
        Glide.get(context).clearDiskCache()
    }

    /*** 清除内存缓存*/
    @JvmStatic
    fun clearMemory(context: Context) {
        Glide.get(context).clearMemory();
    }

    /*** 取消图片加载*/
    @JvmStatic
    fun clearImage(context: Context, imageView: ImageView?) {
        Glide.get(context).requestManagerRetriever[context].clear(imageView!!)
    }

    /*** 预加载*/
    @JvmStatic
    fun preloadImage(context: Any?, url: String?) {
        glideRequests(context).load(url).preload()
    }

    /*** 重新加载*/
    @JvmStatic
    fun resumeRequests(context: Any?) {
        glideRequests(context).resumeRequests()
    }

    /*** 暂停加载*/
    @JvmStatic
    fun pauseRequests(context: Any?) {
        glideRequests(context).pauseRequests()
    }

    /**下载*/
    suspend fun downloadImage(context: Context, imgUrl: String?): File? = withContext(Dispatchers.IO) {
        var extension = MimeTypeMap.getFileExtensionFromUrl(imgUrl)
        if (extension.isNullOrEmpty()) extension = "png"
        val file = Glide.with(context).download(imgUrl).submit().get()
        val appDir = context.getExternalFilesDir("img")
        if (!appDir!!.exists()) {
            appDir.mkdirs()
        }
        //保存的文件名
        val fileName = "img_" + System.nanoTime() + "." + extension
        val targetFile = File(appDir, fileName)
        file.copyTo(targetFile)
        //扫描媒体库
        val mimeTypes = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        MediaScannerConnection.scanFile(context, arrayOf(targetFile.absolutePath), arrayOf(mimeTypes), null)
        targetFile
    }

}
