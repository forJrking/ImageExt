# ImageExt 参考Coil对Glide封装实现

## 前言

`glide` 是 `Google 官方`推荐的一款图片加载库，Coil是号称[Kotlin-first的Android图片加载库](https://juejin.cn/post/6844903913007611917) ，融合了kotlin特性、android最主流的技术和趋势，本篇我们主要分享如何用`kotlin`把`glide`封装的使用起来像`coil`一样。

## 集成和API预览 [![](https://jitpack.io/v/forJrking/ImageExt.svg)](https://jitpack.io/#forJrking/ImageExt)

1. ```groovy
   allprojects {
   	repositories {
   		maven { url 'https://www.jitpack.io' }
   	}
   }
   ```

2. ```groovy
   dependencies {
       implementation 'com.github.forJrking:ImageExt:0.0.4'
   }
   ```

3. ```kotlin
   //配置全局占位图 错误图 非必须
   ImageOptions.DrawableOptions.setDefault {
       placeHolderResId =  R.drawable.ic_launcher_background
       errorResId = R.color.gray
   }
   // URL
   imageView.load("https://www.example.com/image.jpg")
   // Resource
   imageView.load(R.drawable.image)
   //回调和进度监听
   imageView.load("https://www.example.com/image.jpg") {
       placeHolderResId = R.drawable.placeholder
       transformation = arrayOf(GrayscaleTransformation())
       progressListener { isComplete, percentage, bytesRead, totalBytes ->
           //加载进度
       }
       requestListener {
           onSuccess {
           }
           onFail {
           }
       }
   }
   
   //代替AppGlideModule实现来修改glide配置接口
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
   ```
   
4. 其他扩展函数和效果

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
   ```

   ![](https://files.catbox.moe/f27rwx.gif)
## Api总览


| `load: Any？`                                                | 加载资源                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `with: Any?`                                                 | Glide.with( )参数，默认用ImageView                           |
| `placeHolderResId: Int`                                      | 占位图 资源id                                                |
| `placeHolderDrawable: Drawable?`                             | 占位图 drawable                                              |
| `errorResId: Int`                                            | 错误图 资源id                                                |
| `errorDrawable: Drawable?`                                   | 错误图 drawable                                              |
| `skipMemoryCache: Boolean = false`                           | 跳过内存缓存                                                 |
| `diskCacheStrategy: ImageOptions.DiskCache`                  | 磁盘缓存策略                                                 |
| `priority: ImageOptions.LoadPriority`                        | 加载优先级                                                   |
| `thumbnail: Float = 0f`                                      | 缩略图 缩略系数                                              |
| ` thumbnailUrl: Any? = null`                                 | 缩略图 url、File等                                           |
| `size: ImageOptions.OverrideSize?`                           | override                                                     |
| `isAnim: Boolean = true`                                     | 动画 gif动图支持                                             |
| `isCrossFade: Boolean = false`                               | crossFade                                                    |
| `isCircle: Boolean = false`                                  | 圆形头像                                                     |
| `isGray: Boolean = false`                                    | 黑白图像                                                     |
| `isFitCenter: Boolean = false`                               | FitCenter                                                    |
| `centerCrop: Boolean = false`                                | centerCrop                                                   |
| `format: Bitmap.Config? = null`                              | 输出图像模式                                                 |
| `borderWidth: Int = 0, borderColor: Int = 0`                 | 边框宽度，边框颜色                                           |
| `isBlur: Boolean = false, blurRadius: Int = 25, blurSampling: Int = 4` | 高斯模糊，模糊半径和图像缩放倍数（倍数越高处理速度越快，图像越不清晰） |
| `isRoundedCorners: Boolean = false, roundRadius: Int = 0, cornerType: ImageOptions.CornerType` | 圆角，圆角弧度，圆角模式，单角、对角、四角                   |
| `vararg transformation: Transformation<Bitmap>`              | 转换器，支持圆角、黑白等和其他自定义                         |
| `onProgressListener: OnProgressListener? = null`             | 网络资源进度监听，仅网络资源有效                             |
| `requestListener: OnImageListener?`                          | 加载结果监听,成功和失败                                      |

## 封装实现

首先看coil的调用方式采用了`kotlin`扩展函数形式，给ImageView增加了一个`load(url)`函数，然后其他占位图等配置通过DSL方式去设置。DSL如何学习和使用后面单独说。

第一步封装如下一个函数：

```kotlin
/**模仿 coil DSL写法**/
fun ImageView.load(load: Any?, options: (ImageOptions.() -> Unit)? = null) {
    ImageLoader.loadImage(ImageOptions(load).also(options))
}
```

第二步封装配置类：

```kotlin
/**
 * 图片加载库的配置，封装原始加载配置属性，进行转换
 */
class ImageOptions {
    /*** 加载原始资源*/
    var res: Any? = null
    /*** 显示容器*/
    var imageView: ImageView? = null
    /*** imageView存在的上下文或者fragment\activity*/
    var context: Any? = null
        get() {
            return field ?: imageView
        }
    /*** 加载占位图资源ID，如果placeholder是0表示没有占位图*/
    @DrawableRes
    var placeHolderResId = 0
		.... 省略其动画、错误图等等他属性
    var centerCrop: Boolean = false
    /*** 网络进度监听器*/
    var onProgressListener: OnProgressListener? = null
    /*** 加载监听*/
    var requestListener: OnImageListener? = null
	  ....省略缓存策略和优先级等等枚举
}
```

第三步 策略实现，由于要使用okhttp拦截器做进度监听，通过注解方式配置glide的网络下载器。

```kotlin
/**Glide策略封装*/
object ImageLoader {
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
            // 占位图、错误图
            ...
            //缓存配置,优先级,缩略图请求
            ...
            //动画、transformation
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
}
```

## Kotlin DSL

DSL的编写可以用下面代码简单理解和记忆（主要参考：[如何让你的回调更具Kotlin风味 (juejin.cn)](https://juejin.cn/post/6844903769436585991)）

```kotlin
class DSLTest{
  //普通变量
  var str:String? =null
  //函数变量
  var onSuccess: ((String?) -> Unit)? = null
	//调用函数变量
  fun onSuccessDo() {
    	...
  		onSuccess.invoke("success $str")
  }
}
//定义调用的函数
load(dslPar:(DSLTest.() -> Unit)? = null){
   DSLTest().also(dslPar)
}
//使用
load{
  str = "ACC"
  onSuccess{
    //TODO
  }
}
```

## 与传统策略模式封装对比

- 定义策略模式基础接口

```kotlin
/** 图片加载策略  接口*/
public interface BaseImageLoaderStrategy {
    void loadImage(load Any,ImageOptions options);
}
```

- 实现策略接口

```kotlin
/*** 具体的加载策略， Glide 加载框架*/
public class GlideImageLoader implements BaseImageLoaderStrategy {
    @Override
    public void loadImage(Context context, ImageOptions options) {
        Glide.with(context).apply(...).into(options.getImgView());
    }
}
```

- 策略调度器调用

```kotlin
ImageLoader.loadImage(ImageOptions(imageView).apply{
	.....
})
```

策略模式一般会封装很多接口满足日常需求，由于kotlin特性我们封装一个超长参数的方法，然后使用可选参数的方式调用，但是java就无能为力了，只能对可选参数赋值null。

```kotlin
//可选参数示例
load(url:String,isCirle:Boolean = false, width:Int=0, height:Int = 0){
  .....
}
//用 参数名 = 值 使用可选参数
iv_8.load(url2, height = 800)
```

策略模式封装写法和扩展函数+DSL写法对比：

- 统一的接口封装，都具有可扩展性
- 都可以用kotlin特性不用定义大量接口
- 扩展函数更加方便简洁
- DSL的写法让代码更加易懂，更具kotlin风格
- 多方法接口回调，可以只选择个别方法

## 总结

- 最后要方便的使用到项目中那就打包发布jitpack仓库，项目开源地址和文档

[forJrking/ImageExt: 基于Glide封装ImageView加载图片资源的扩展函数集 (github.com)](https://github.com/forJrking/ImageExt)

- ~~由于使用到基于okhttp的下载进度管理所以使用了 glide 的@GlideModule配置方法，这样可能会和你项目自定义配置有冲突，目前只能拉代码自己修改，然后依赖Module方式了。如有更好方式联系我改进。~~

- Android图片加载库常见的只有几种，其他库可以自行参考实现。Kotlin真香！！！


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
一个点击可以变为半透明

## CircleImageView 圆形头像

一个圆形图片展示控件

