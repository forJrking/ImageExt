package com.github.forjrking.image.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.github.forjrking.image.glide.http.OkHttpUrlLoader
import com.github.forjrking.image.glide.progress.ProgressManager
import com.github.forjrking.image.glide.progress.ProgressManager.glideProgressInterceptor
import okhttp3.OkHttpClient
import java.io.InputStream

@GlideModule(glideName = "IGlideModule")
class AppGlideModuleIml : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        options?.applyOptions(context, builder)
//        builder.setDiskCache(InternalCacheDiskCacheFactory(context, "Glide", IMAGE_DISK_CACHE_MAX_SIZE.toLong()))
//        val calculator = MemorySizeCalculator.Builder(context).build()
//        val defaultMemoryCacheSize = calculator.memoryCacheSize
//        val defaultBitmapPoolSize = calculator.bitmapPoolSize
//        val customMemoryCacheSize = (1.2 * defaultMemoryCacheSize).toInt()
//        val customBitmapPoolSize = (1.2 * defaultBitmapPoolSize).toInt()
//        builder.setMemoryCache(LruResourceCache(customMemoryCacheSize.toLong()))
//        builder.setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))

    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        //下载进度的实现
        registry.replace(GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(OkHttpClient.Builder().glideProgressInterceptor().build()))
        options?.registerComponents(context, glide, registry)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return options?.isManifestParsingEnabled() ?: false
    }

    companion object {
        /**把Glide配置方法进行暴露接口*/
        var options: IAppGlideOptions? = null
    }
}

/**把Glide配置方法进行保留*/
interface IAppGlideOptions {
    fun isManifestParsingEnabled(): Boolean = false
    fun applyOptions(context: Context, builder: GlideBuilder)
    /**glide 下载进度的主要逻辑 需要在OkHttpClient.Builder().glideProgressInterceptor()**/
    fun registerComponents(context: Context, glide: Glide, registry: Registry)
}