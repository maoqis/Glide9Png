# Glide9Png

中文： 让Glide支持加载经过appt编译后的9.png文件。
英文：
A Glide 9.png intergration library for decoding 9.png file to NinePatchDrawable , not transforming
source Bimap(has the 9.png chunk) and NinePatchDrawable, encoding to config save source data, and
displaying NinePatchDrable to ImageView. You just need to init the library after Glide be inited

## 接入 （guide）

### 1.1 仓库引入（import maven）

源码中Glide4使用的4.16.0进行适配。版本号时候追加 .4.16.0

````
    io.github.maoqis.glide9png:${VERSION}.4.16.0
````


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

### 1.3 初始化代码

````
NinePngGlideApi.afterGlideInit(GlideApp.get(MainActivity.this.getApplicationContext()));
````

### 1.4 调用方式没变

```
            GlideApp.with(MainActivity.this)
                    .load(urlChunk)
                    .into(iv);
```

由于9.png一般是用于背景图。如果src使用时候，需要考虑图片imageType，一般用于FitXY模式。

## 源码简单介绍

## 参考

