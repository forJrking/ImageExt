# ImageExt

### Glide封装实现, 主要为ImageView添加扩展函数来简化常见图片加载api [![](https://jitpack.io/v/forJrking/ImageExt.svg)](https://jitpack.io/#forJrking/ImageExt)

![img](/Users/forjrking/Downloads/img.gif)

## 使用方法

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```

```groovy
dependencies {
    implementation 'com.github.forJrking:ImageExt:0.0.1'
}
```
```kotlin
iv_2.loadImage(url, placeHolder = R.color.blue)
//模糊
iv_3.loadBlurImage(url)
//圆形
iv_4.loadCircleImage(url)
//边框
iv_5.loadBorderImage(url, borderWidth = 10, borderColor = Color.RED)
//黑白
iv_6.loadGrayImage(url)
//圆角
iv_7.loadRoundCornerImage(url, radius = 10, type = ImageOptions.CornerType.ALL)
//resize
iv_8.loadResizeImage(url, width = 400, height = 800)
//监听回调结果
iv.loadImage(url4, loadListener = object : OnImageListener {
    override fun onSuccess(drawable: Drawable?) {
        Toast.makeText(application, R.string.load_success, Toast.LENGTH_LONG).show()
    }
    override fun onFail(msg: String?) {
        Toast.makeText(application, R.string.load_failed, Toast.LENGTH_LONG).show()
    }
})
//终极扩展 参数非常多必须使用可选参数方式调用
iv_9.loadImage(load = R.drawable.test, with = MainActivity@ this, 
               placeHolderResId = R.color.black,errorResId = R.color.blue,isAnim = false,
        requestListener = object : OnImageListener {
           ...
        },
        onProgressListener = object : OnProgressListener {
         ...
        }, transformation = *arrayOf(GrayscaleTransformation())
)
```

## 可选扩展函数Api

```kotlin
ImageView.loadImage(...)
ImageView.loadProgressImage(...)
ImageView.loadResizeImage(...)
ImageView.loadGrayImage(...)
ImageView.loadBlurImage(...)
ImageView.loadBlurImage(...)
ImageView.loadRoundCornerImage(...)
ImageView.loadCircleImage(...)
ImageView.loadBorderImage(...)

//终极扩展函数
@JvmOverloads
fun ImageView.loadImage(load: Any?, with: Any? = this,
//占位图 错误图
@DrawableRes placeHolderResId: Int = placeHolderImageView, placeHolderDrawable: Drawable? = null,
@DrawableRes errorResId: Int = placeHolderImageView, errorDrawable: Drawable? = null,
@DrawableRes fallbackResId: Int = placeHolderImageView, fallbackDrawable: Drawable? = null,
//缓存策略等
skipMemoryCache: Boolean = false,
diskCacheStrategy: ImageOptions.DiskCache = ImageOptions.DiskCache.AUTOMATIC,
//优先级
priority: ImageOptions.LoadPriority = ImageOptions.LoadPriority.NORMAL,
//缩略图
thumbnail: Float = 0f, thumbnailUrl: Any? = null,
size: ImageOptions.OverrideSize? = null,
//gif或者动画
isAnim: Boolean = true,
isCrossFade: Boolean = false,
isCircle: Boolean = false,
isGray: Boolean = false,
isFitCenter: Boolean = false,
centerCrop: Boolean = false,
//输出图像像素格式
format: Bitmap.Config? = null,
//边框 一组一起
borderWidth: Int = 0, borderColor: Int = 0,
//模糊处理 一组一起使用
isBlur: Boolean = false, blurRadius: Int = 25, blurSampling: Int = 4,
//圆角 一组一起使用
isRoundedCorners: Boolean = false, roundRadius: Int = 0, cornerType: ImageOptions.CornerType = ImageOptions.CornerType.ALL,
//自定义转换器
vararg transformation: Transformation<Bitmap>,
//进度监听,请求回调监听
onProgressListener: OnProgressListener? = null, requestListener: OnImageListener? = null) {...}
```

## CircleProgressView 仿微博图片加载
就是原封不动来自[GlideImageView](https://github.com/sunfusheng/GlideImageView) ，在布局中加入即可，有三种样式可供选择。
```xml
<CircleProgressView
 	android:id="@+id/progressView"
 	android:layout_width="50dp"
 	android:layout_height="50dp"
 	android:layout_centerInParent="true"
 	android:layout_margin="10dp"
 	android:progress="0"
 	android:visibility="gone"
 	app:cpv_progressNormalColor="@color/transparent10"
 	app:cpv_progressReachColor="@color/transparent90_white"
 	app:cpv_progressStyle="FillInnerArc"
 	app:cpv_progressTextColor="@color/red"
 	app:cpv_progressTextSize="13sp"
 	app:cpv_progressTextVisible="false" />
```
## SelectImageView 仿微信图片点击响应
一个点击可以变为半透明的View，算是一个Bonus，所以放在了Sample里。逻辑十分简单，看代码即可。

