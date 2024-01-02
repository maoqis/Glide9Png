# Glide9Png

中文： 让Glide支持加载经过appt编译后的9.png文件。
英文：
A Glide 9.png intergration library for decoding 9.png file to NinePatchDrawable , not transforming
source Bimap(has the 9.png chunk) and NinePatchDrawable, encoding to config save source data, and
displaying NinePatchDrable to ImageView. You just need to init the library after Glide be inited

## 一、接入 （guide）

### 1.1 仓库引入（import maven）

源码中Glide4使用的4.16.0进行适配。版本号时候追加 .4.16.0

````
正式版DONE: implementation 'io.github.maoqis:glide9png:1.0.1.4.16.0'
测试版DONE: implementation 'io.github.maoqis:glide9png:1.0.1.4.16.0-SNAPSHOT'
````

```
//正式 mavenCentral()
//测试 snapshots
maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' 

```

https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/maoqis/glide9png/0.0.2.4.16.0-SNAPSHOT/maven-metadata.xml

### 1.2 Glide4 必要配置

Glide4 需要配置一个AppGlideModule，才能让NinePngGlideModule（GlideModuleLib）生效，编译时生成代码。

源码使用的4.16.0进行适配。

```
@GlideModule
public class AppGlideSingle extends AppGlideModule {
    private static final String TAG = "AppGlideSingle";
    @Override
    public boolean isManifestParsingEnabled() {
        return false;//需要配置false，取消旧的方式。
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        Log.d(TAG, "applyOptions: ");
    }
}
```

``` 所在moudle需要引入编译器
    annotationProcessor "com.github.bumptech.glide:compiler:4.16.0"
    implementation 'com.github.bumptech.glide:glide:4.16.0'
```

### 1.3 初始化代码

````
NinePngGlideApi.afterGlideInit(GlideApp.get(MainActivity.this.getApplicationContext()));
````

### 1.4 调用方式没变

支持加载到src，您不用单独为ImageView设置scaleType=FitXY。

```
            GlideApp.with(MainActivity.this)
                    .load(urlChunk)
                    .into(iv);
```

### 1.5 用dp显示了9.png图片了，而非px显示。默认1dp=3px。

```
目前9png解析时候Bitmap中density的dpi=480 ，即3倍图。140px，显示为46.6666dp。

API 见NinePngGlideApi.setDesignDensityDPI
```

#### 多设备适配（TODO）

TOOD: 为单独的某一次请求配置Bitmap显示成多少dp。
自定义GlideOption方法，Option中保持setDesignDensityDPI。

```
为了适配不同设备密度的变化，比如Pad，精细密度392dp、屏幕宽度411dp等。
比如原来360dp宽（1080px）的手机上，要显示46.6666dp。如果要等比例显示。
现在屏幕还是1080px宽度变成392dp。
那应该显示46.6666dp * 392(int类型精度问题) /360 。360对应dpi=480；392对应dip=440左右（因392的精度问题）。
```

## 二、资源

### 需要经过经过adb工具appt编译后的9.png

### 如果后台上传图标时候支持9.png图标就好了。

TODO：可以把设计出的9.png图标，自动生成appt编译后图标。

1. 需要校验上传的9.png是否满足9png。
2. 使用appt编译：编译后的png就有9.png对应的chunk信息。
3. 校验下客户端的api能否读取出chunk信息。如果客户端读取时候有问题，也不能使用。

## 三、源码实现的步骤

可以运行 AndroidExperienceCase 看下。

```GlideNinePngFragment
https://github.com/maoqis/AndroidExperienceCase/blob/master/app/src/main/java/com/maoqis/testcase/feature/GlideNinePngFragment.java
```

![image](https://raw.githubusercontent.com/maoqis/AndroidExperienceCase/master/images/temp.png)

### NineDrawableImageViewTarget 中为ImageView 设置了 FitXY

由于9.png一般是用于背景图。如果src使用时候，需要考虑图片imageType，一般用于FitXY模式。
NineDrawableImageViewTarget 中为ImageView 设置了 FitXY, 并在加载到非9png图片后进行恢复last ScaleType

```agsl
    protected void setResource(@Nullable Drawable resource) {
        boolean is9Png = false;
        if (resource instanceof NinePatchDrawable) {
            is9Png = true;
        }
        Log.d(TAG, "setResource: is9Png=" + is9Png);
        if (is9Png) {
            NineTargetUtils.setFitXyFor9Png(view);
            super.setResource(resource);
        } else {
            NineTargetUtils.restoreScaleType(view);
            super.setResource(resource);
        }
    }
```

## 参考

## 版本更新

### 1.0.1 （TODO）
1. 修复pom文件中子依赖库问题。

### 1.0.0 (废弃，依赖有问题)
24年元旦过完发布。
1. 支持Glide加载appt后的9.png图片，并使用原数据流作为文件缓存、公用内存缓存、活动缓存。
2. 支持显示9.png时，自动切换ScaleType模式。9.png使用FitXY显示；再显示其他格式时恢复上次模式比如CenterCrop。
3. 支持简单3步取，.into直接显示的ImageView。不用做额外操作，比如asBitmap，dontTransform（所以的transform都被屏蔽）。


