package com.github.forjrking.image.core

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.bumptech.glide.load.Transformation
import com.github.forjrking.image.glide.progress.OnProgressListener

/**
 * 图片加载库的配置，封装原始加载配置属性，进行转换
 */
class ImageOptions {

    /*** 加载原始资源*/
    var res: Any? = null

    /*** 显示容器*/
    var imageView: ImageView? = null

    /**
     * imageView存在的上下文或者fragment\activity
     */
    var context: Any? = null
        get() {
            return field ?: imageView
        }

    /**
     * 加载占位图资源ID，如果placeholder是0表示没有占位图
     */
    @DrawableRes
    var placeHolderResId = 0

    /**
     * 加载占位图资源Drawable对象
     */
    var placeHolderDrawable: Drawable? = null

    /**
     * 错误占位图的资源ID
     */
    @DrawableRes
    var errorResId = 0

    /**
     * 加载失败占位图资源Drawable对象
     */
    var errorDrawable: Drawable? = null

    @DrawableRes
    var fallbackResId = 0

    var fallbackDrawable: Drawable? = null

    /*** 是否渐隐加载*/
    var isCrossFade = true

    /*** 是否跳过内存缓存*/
    var skipMemoryCache: Boolean = false

    /*** 没有动画，默认是使用动画*/
    var isAnim: Boolean = true

    /*** 磁盘缓存*/
    var diskCacheStrategy = DiskCache.AUTOMATIC

    /*** 加载优先级*/
    var priority = LoadPriority.NORMAL

    /*** 加载缩略图*/
    var thumbnail: Float = 0f

    /*** 缩略图链接*/
    var thumbnailUrl: Any? = null

    /*** 图片的尺寸*/
    var size: OverrideSize? = null

    /**
     * 特效处理：圆形图片
     * Glide要将isCrossFade设置为false，不然会影响展示效果
     */
    var isCircle: Boolean = false

    var isGray: Boolean = false

    var centerCrop: Boolean = false

    var isFitCenter: Boolean = false

    /*** 圆形是否带边框*/
    var format: Bitmap.Config? = null

    /*** 圆形是否带边框*/
    var borderWidth: Int = 0

    /*** 圆形边框的颜色*/
    @ColorInt
    var borderColor: Int = Color.TRANSPARENT

    /**
     * 模糊特效
     * Glide要将isCrossFade设置为false，不然会影响展示效果
     */
    var isBlur: Boolean = false

    /***  高斯模糊半經*/
    var blurRadius: Int = 25

    /*** 缩放系数 加速模糊速度*/
    var blurSampling: Int = 4

    /**
     * 是否圆角
     * Glide要将isCrossFade设置为false，不然会影响展示效果
     */
    var isRoundedCorners: Boolean = false

    /*** 圆角的弧度*/
    var roundRadius: Int = 0

    /*** 圆角的边向*/
    var cornerType: CornerType = CornerType.ALL

    enum class CornerType {
        ALL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT, OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT, DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT
    }

    /*** 转换器*/
    var transformation: Array<out Transformation<Bitmap>>? = null

    /*** 网络进度监听器*/
    var onProgressListener: OnProgressListener = null
        private set

    fun progressListener(listener: OnProgressListener) {
        this.onProgressListener = listener
    }

    /*** 加载监听*/
    var requestListener: OnImageListener? = null
        private set

    fun requestListener(listener: OnImageListener.() -> Unit) {
        requestListener = OnImageListener().also(listener)
    }

    /**
     * 指定加载图片的大小
     * @param width 宽
     * @param height 高
     */
    class OverrideSize(val width: Int, val height: Int)

    /**
     * 硬盘缓存策略
     */
    enum class DiskCache(val strategy: Int) {
        /**
         * 没有缓存
         */
        NONE(1),

        /**
         * 根据原始图片数据和资源编码策略来自动选择磁盘缓存策略，需要写入权限
         */
        AUTOMATIC(2),

        /**
         * 在资源解码后将数据写入磁盘缓存，即经过缩放等转换后的图片资源
         */
        RESOURCE(3),

        /**
         * 在资源解码前就将原始数据写入磁盘缓存，需要写入权限
         */
        DATA(4),

        /**
         * 使用DATA和RESOURCE缓存远程数据，仅使用RESOURCE来缓存本地数据，需要写入权限
         */
        ALL(5)
    }

    /**
     * 加载优先级策略
     * 指定了图片加载的优先级后，加载时会按照图片的优先级进行顺序加载
     * IMMEDIATE优先级时会直接加载，不需要等待
     */
    enum class LoadPriority(val priority: Int) {
        /**
         * 低优先级
         */
        LOW(1),

        /**
         * 普通优先级
         */
        NORMAL(2),

        /**
         * 高优先级
         */
        HIGH(3),

        /**
         * 立即加载，无需等待
         */
        IMMEDIATE(4)
    }
}