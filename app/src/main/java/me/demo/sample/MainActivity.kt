package me.demo.sample

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.github.forjrking.image.*
import com.github.forjrking.image.core.ImageOptions
import com.github.forjrking.image.glide.AppGlideModuleIml
import com.github.forjrking.image.glide.GlideImageLoader
import com.github.forjrking.image.glide.IAppGlideOptions
import com.github.forjrking.image.glide.transformation.CircleWithBorderTransformation
import com.github.forjrking.image.glide.transformation.GrayscaleTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.demo.glide.sample.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


/**
 * Kotlin Sample功能
 * @author : BaoZhou
 * @date : 2020/5/9 3:13 PM
 */
class MainActivity : AppCompatActivity() {
    var url1 = "https://t7.baidu.com/it/u=3713375227,571533122&fm=193&f=GIF"
    var url3 =
        "http://img.mp.itc.cn/upload/20170311/33f2b7f7ffb04ecb81e42405e20b3fdc_th.gif?2=2"
    var url4 = "http://img.mp.itc.cn/upload/20170311/33f2b7f7ffb04ecb81e42405e20b3fdc_th.gif"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppGlideModuleIml.options = object : IAppGlideOptions {
            override fun applyOptions(context: Context, builder: GlideBuilder) {
                //修改缓存大小等
                Log.d("TAG", "applyOptions")
            }

            override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
                //修改注册组件 例如 okhttp 注意如果修改可能会导致进度丢失
                Log.d("TAG", "registerComponents")
            }

        }

        Log.d("TAG", "clearDiskCache")
        GlobalScope.launch(Dispatchers.IO) {
            GlideImageLoader.clearDiskCache(applicationContext)
        }
        GlideImageLoader.clearMemory(applicationContext)

        ImageOptions.DrawableOptions.setDefault {
            placeHolderResId = R.drawable.ic_launcher_background
            errorResId = R.color.gray
        }

        initView()
    }

    private fun initView() {
        circleProgressView.visibility = View.VISIBLE
        iv_0.postDelayed(
            {
                iv_0.load(url3) {
                    progressListener { isComplete, percentage, bytesRead, totalBytes ->
                        // 跟踪进度
                        Log.d("TAG", "onProgress: $percentage")
                        if (isComplete) {
                            circleProgressView.visibility = View.GONE
                        } else {
                            circleProgressView.progress = percentage
                        }
                    }
                }

            }, 500
        )

        iv_1.setOnClickListener { downloadImage() }
        iv_8.setOnClickListener { }
        iv_1.loadImage(url1, placeHolder = R.color.blue)
//
        iv_2.loadImage(url4, requestListener = {
            onSuccess {
                Toast.makeText(application, R.string.load_success, Toast.LENGTH_LONG).show()
            }
            onFail {
                Toast.makeText(application, R.string.load_failed, Toast.LENGTH_SHORT).show()
            }
        })
//
        iv_4.loadCircleImage(url1)
        iv_5.loadBorderImage(url1, borderWidth = 10, borderColor = Color.RED)
        iv_6.loadGrayImage(url1)
        iv_7.loadRoundCornerImage(url1, radius = 40, type = ImageOptions.CornerType.ALL)
//        iv_8.loadBlurImage(url4)
//        iv_8.loadResizeImage(url2, width = 400, height = 800)
//
//        iv_9.loadImage(load = R.drawable.test, with = MainActivity@ this, placeHolderResId = R.color.black,
//                requestListener = object : OnImageListener {
//                    override fun onSuccess(drawable: Drawable?) {
//                    }
//
//                    override fun onFail(msg: String?) {
//                    }
//
//                },
//                onProgressListener = object : OnProgressListener {
//                    override fun onProgress(isComplete: Boolean, percentage: Int, bytesRead: Long, totalBytes: Long) {
//                    }
//
//                }, transformation = *arrayOf(GrayscaleTransformation(), CircleWithBorderTransformation(borderWidth = 0, borderColor = 0)))
//        iv_10.loadImage(url2, placeHolder = R.color.green)
        iv_8.load(url1) {
            placeHolderResId = R.color.black
            transformation = arrayOf(GrayscaleTransformation(),
                CircleWithBorderTransformation(borderWidth = 0, borderColor = 0))
            progressListener { isComplete, percentage, bytesRead, totalBytes ->
                //加载进度
            }
            drawableOptions = ImageOptions.DrawableOptions.DEFAULT
            requestListener {
                onSuccess {

                }
                onFail {

                }
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    @AfterPermissionGranted(WRITE_EXTERNAL_PERM)
    private fun downloadImage() {
        if (hasStoragePermission()) {
            GlobalScope.launch {
                try {
                    val downloadImage =
                        GlideImageLoader.downloadImage(context = this@MainActivity, imgUrl = url1)
                    Log.d("TAG", "downloadImage: ${downloadImage?.absolutePath}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.need_write_external),
                WRITE_EXTERNAL_PERM,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    companion object {
        private const val WRITE_EXTERNAL_PERM = 123
    }

}