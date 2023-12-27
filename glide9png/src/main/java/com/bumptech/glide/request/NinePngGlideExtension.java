package com.bumptech.glide.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.DrawableTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation;
import com.github.maoqis.glide9png.trans.NinePngDrawableTransformation;
import com.github.maoqis.glide9png.trans.NinePngTransformationWrap;

import java.lang.reflect.Field;

/**
 * 因为包访问权限问题，暂只放到这个类中。
 *
 * 1. .into时候，使用了几个transform。
 * 我们需要用NineTransformationWrap，把所有设置Transform的地方进行包装，在包装类里面进行判断9png不做bitmap转化，glide api中的转换都是Bitmap。
 * 2. 如果不asBimap(默认)，一定会经过DrawableTransformation .
 * 把解码后的 drawable（比如{@link NinePatchDrawable}） 转成bitmap, 这样会有问题。所以要跳过DrawableTransformation。
 * {@link BaseRequestOptions#transform(Transformation, boolean)}}
 *
 */
@GlideExtension
public class NinePngGlideExtension {
    private static final String TAG = "NinePngGlideExtension";

    /**
     * 将构造方法设为私有，作为工具类使用
     */
    private NinePngGlideExtension() {
    }

    /**
     * 1.自己新增的方法的第一个参数必须是RequestOptions options
     * 2.方法必须是静态的
     * 反射调用方法方法时候，注意Class传子类类型
     *
     * @param options
     */
    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> optionalCenterCrop(BaseRequestOptions<?> options) {
        Log.d(TAG, "optionalCenterCrop() called with: options = [" + options + "]");
        //需要和BaseRequestOptions在 相同包名下才能直接调用
        try {
            BaseRequestOptions<?> ret = options.optionalTransform(DownsampleStrategy.CENTER_OUTSIDE, new NinePngTransformationWrap(new CenterCrop()));
            return ret;
        } catch (Exception e) {
            Log.e(TAG, "optionalCenterCrop: ", e);
            return options;
        }
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> optionalCenterInside(BaseRequestOptions<?> options) {
        Log.d(TAG, "optionalCenterInside() called with: options = [" + options + "]");

        //optionalScaleOnlyTransform 私有，需要用反射
        try {
            BaseRequestOptions<?> ret = optionalScaleOnlyTransform(options,
                    DownsampleStrategy.CENTER_INSIDE, new NinePngTransformationWrap(new CenterInside()));

            return ret;
        } catch (Exception e) {
            Log.e(TAG, "optionalCenterInside: ", e);
            return options;
        }
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> optionalCircleCrop(BaseRequestOptions<?> options) {
        return options.optionalTransform(DownsampleStrategy.CENTER_OUTSIDE, new NinePngTransformationWrap(new CircleCrop()));
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> optionalFitCenter(BaseRequestOptions<?> options) {
        Log.d(TAG, "optionalFitCenter() called with: options = [" + options + "]");
        //optionalScaleOnlyTransform 私有，需要用反射
        try {
            BaseRequestOptions<?> ret = optionalScaleOnlyTransform(options,
                    DownsampleStrategy.FIT_CENTER, new NinePngTransformationWrap(new FitCenter()));

            return ret;
        } catch (Exception e) {
            Log.e(TAG, "optionalFitCenter: ", e);
            return options;
        }
    }

    /**
     * 添加NineDrawableTransformation，拦截DrawableTransformation的转换。
     * 由于只是扩展方法，不是真正复写方法，比如源码中的其他方法调用该方法地方，则不能调到扩展类。
     * 所以后面需要复写直接和间接调用处，避免没有使用NineDrawableTransformation，在aseRequestOptions类中寻找调用处。
     * 标记遍历方法，能保证效率。这个方法是公用根节点，向上寻找叶子调用处，保存根到当前节点路径，叶子节点处理完后标记断点已处理；
     * 向上返回，深度优先，左右中（后序遍历），子节点都处理完后，标记当前节点已处理。（方法搜索调用处作为遍历器，）
     *
     * @param options
     * @param transformation
     * @param isRequired
     * @return
     */
    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> transform(BaseRequestOptions<?> options,
                                                  @NonNull Transformation<Bitmap> transformation,
                                                  boolean isRequired) {

        boolean isAutoCloneEnabled = getIsAutoCloneEnabled(options);

        if (isAutoCloneEnabled) {
            return options.clone().transform(transformation, isRequired);
        }

        DrawableTransformation drawableTransformation = new DrawableTransformation(transformation, isRequired);
        options.transform(Bitmap.class, transformation, isRequired);
        options.transform(Drawable.class, drawableTransformation, isRequired);
        // TODO: remove BitmapDrawable decoder and this transformation.
        // Registering as BitmapDrawable is simply an optimization to avoid some iteration and
        // isAssignableFrom checks when obtaining the transformation later on. It can be removed without
        // affecting the functionality.
        /**
         * {@link NinePngDrawableTransformation} ,如果碰到 子类（NineDrawableTransformation）放前面，先匹配到。
         */
        options.transform(NinePatchDrawable.class, new NinePngDrawableTransformation(transformation), isRequired);
        options.transform(BitmapDrawable.class, drawableTransformation.asBitmapDrawable(), isRequired);
        options.transform(GifDrawable.class, new GifDrawableTransformation(transformation), isRequired);
        return options.selfOrThrowIfLocked();
    }


    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    @NonNull
    public static BaseRequestOptions<?> optionalTransform(BaseRequestOptions<?> options,
                                                          @NonNull DownsampleStrategy downsampleStrategy,
                                                          @NonNull Transformation<Bitmap> transformation) {
        boolean isAutoCloneEnabled = getIsAutoCloneEnabled(options);
        if (isAutoCloneEnabled) {
            return options.clone().optionalTransform(downsampleStrategy, transformation);
        }
        options.downsample(downsampleStrategy);
        //为了代码阅读方便，直接调用了this. ,不使用重装机制。
        return transform(options, transformation, /* isRequired= */ false);
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> optionalScaleOnlyTransform(BaseRequestOptions<?> options,
                                                                   @NonNull DownsampleStrategy strategy, @NonNull Transformation<Bitmap> transformation) {
        //这里直接调用了，因为private
        return scaleOnlyTransform(options, strategy, transformation, false /*isTransformationRequired*/);
    }


    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> transform(BaseRequestOptions<?> options, @NonNull Transformation<Bitmap> transformation) {
        return transform(options, transformation, /* isRequired= */ true);
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> transform(BaseRequestOptions<?> options,
                                                  @NonNull DownsampleStrategy downsampleStrategy,
                                                  @NonNull Transformation<Bitmap> transformation) {
        if (getIsAutoCloneEnabled(options)) {
            return options.clone().transform(downsampleStrategy, transformation);
        }

        options.downsample(downsampleStrategy);

        return transform(options, transformation);
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> transform(BaseRequestOptions<?> options, @NonNull Transformation<Bitmap>... transformations) {
        if (transformations.length > 1) {
            return transform(options, new MultiTransformation<>(transformations), /* isRequired= */ true);
        } else if (transformations.length == 1) {
            return transform(options, transformations[0]);
        } else {
            return options.selfOrThrowIfLocked();
        }
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> transforms(BaseRequestOptions<?> options, @NonNull Transformation<Bitmap>... transformations) {
        return transform(options, new MultiTransformation<>(transformations), /* isRequired= */ true);
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> optionalTransform(BaseRequestOptions<?> options, @NonNull Transformation<Bitmap> transformation) {
        return transform(options, transformation, /* isRequired= */ false);
    }

    /**
     * 再向上寻找父方法，这样修改比较全。
     */
    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    @NonNull
    public static BaseRequestOptions<?> scaleOnlyTransform(
            BaseRequestOptions<?> options,
            @NonNull DownsampleStrategy strategy,
            @NonNull Transformation<Bitmap> transformation,
            boolean isTransformationRequired) {
        BaseRequestOptions<?> result =
                isTransformationRequired
                        ? transform(options, strategy, transformation)
                        : optionalTransform(options, strategy, transformation);
        setIsScaleOnlyOrNoTransform(result, true);
        return result;
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> scaleOnlyTransform(BaseRequestOptions<?> options,
                                                           @NonNull DownsampleStrategy strategy, @NonNull Transformation<Bitmap> transformation) {
        //这里直接调用了，因为private
        return scaleOnlyTransform(options, strategy, transformation, true /*isTransformationRequired*/);
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> fitCenter(BaseRequestOptions<?> options) {
        //这里直接调用了，因为private
        return scaleOnlyTransform(options, DownsampleStrategy.FIT_CENTER, new NinePngTransformationWrap(new FitCenter()));
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> centerInside(BaseRequestOptions<?> options) {
        //这里直接调用了，因为private
        return scaleOnlyTransform(options, DownsampleStrategy.CENTER_INSIDE, new NinePngTransformationWrap(new CenterInside()));
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> centerCrop(BaseRequestOptions<?> options) {
        return transform(options, DownsampleStrategy.CENTER_OUTSIDE, new NinePngTransformationWrap(new CenterCrop()));
    }

    @GlideOption(override = GlideOption.OVERRIDE_REPLACE)
    public static BaseRequestOptions<?> circleCrop(BaseRequestOptions<?> options) {
        return transform(options, DownsampleStrategy.CENTER_INSIDE, new NinePngTransformationWrap(new CircleCrop()));
    }


    /**
     * 注意反射调用field时候 需要注意 要调用子类还是父类。
     * @param options
     * @return
     */
    private static boolean getIsAutoCloneEnabled(BaseRequestOptions<?> options) {
        boolean isAutoCloneEnabled = false;
        try {
            Field field = BaseRequestOptions.class.getDeclaredField("isAutoCloneEnabled");
            field.setAccessible(true);
            isAutoCloneEnabled = (boolean) field.get(options);
            field.setAccessible(false);
        } catch (Exception e) {
            Log.e(TAG, "transform: ", e);
        }
        return isAutoCloneEnabled;
    }

    private static void setIsScaleOnlyOrNoTransform(BaseRequestOptions<?> options, boolean value) {
        try {
            Field field = BaseRequestOptions.class.getDeclaredField("isScaleOnlyOrNoTransform");
            field.setAccessible(true);
            field.set(options, value);
            field.setAccessible(false);
        } catch (Exception e) {
            Log.e(TAG, "setIsScaleOnlyOrNoTransform: ", e);
        }
    }
}