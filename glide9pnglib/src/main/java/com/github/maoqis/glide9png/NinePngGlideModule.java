package com.github.maoqis.glide9png;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.module.LibraryGlideModule;
import com.github.maoqis.glide9png.decoder.ByteBufferBitmap9pngDecoder;
import com.github.maoqis.glide9png.decoder.NinePatchDrawableDecoder;
import com.github.maoqis.glide9png.decoder.StreamBitmap9pngDecoder;
import com.github.maoqis.glide9png.encoder.NinePngBitmapEncoder;
import com.github.maoqis.glide9png.encoder.NinePngDrawableEncoder;
import com.github.maoqis.glide9png.target.NinePngImageViewTargetFactory;
import com.bumptech.glide.request.target.ImageViewTargetFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

@GlideModule
public class NinePngGlideModule extends LibraryGlideModule {
    private static final String TAG = "NineWebGlideModule";

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        Log.d(TAG, "registerComponents: registry=" + registry);
        // We should put our decoder before the build-in decoders,
        // because the Downsampler will consume arbitrary data and make the inputstream corrupt
        // on some devices
        final Resources resources = context.getResources();
        final BitmapPool bitmapPool = glide.getBitmapPool();
        final ArrayPool arrayPool = glide.getArrayPool();
        ByteBufferBitmap9pngDecoder byteBufferBitmapDecoder = new ByteBufferBitmap9pngDecoder(bitmapPool,resources);
        StreamBitmap9pngDecoder streamBitmapDecoder = new StreamBitmap9pngDecoder(bitmapPool, arrayPool,resources);
        ResourceDecoder<ParcelFileDescriptor, Bitmap> parcelFileDescriptorVideoDecoder = VideoDecoder.parcel(bitmapPool);

        NinePngBitmapEncoder bitmapEncoder = new NinePngBitmapEncoder(arrayPool);

        registry
                //Bitmap支持9png
                .prepend(Registry.BUCKET_BITMAP, ByteBuffer.class, Bitmap.class, byteBufferBitmapDecoder)
                .prepend(Registry.BUCKET_BITMAP, InputStream.class, Bitmap.class, streamBitmapDecoder)
                .prepend(Bitmap.class, bitmapEncoder)//加码器缓存时候用到
                //NinePatchDrawable
                .prepend(
                        Registry.BUCKET_BITMAP_DRAWABLE,
                        ByteBuffer.class,
                        NinePatchDrawable.class,
                        new NinePatchDrawableDecoder<>(resources, byteBufferBitmapDecoder, arrayPool))
                .prepend(
                        Registry.BUCKET_BITMAP_DRAWABLE,
                        InputStream.class,
                        NinePatchDrawable.class,
                        new NinePatchDrawableDecoder<>(resources, streamBitmapDecoder, arrayPool))
                .prepend(NinePatchDrawable.class, new NinePngDrawableEncoder(bitmapEncoder))//
        ;

    }


    /**
     * called after your application.onCreate
     */
    static void replaceImageViewTargetFactory(GlideContext glideContext) {
        if (glideContext == null) {
            Log.w(TAG, "replaceImageViewTargetFactory: glideContext == null");
            return;
        }
        //反射设置，NinePatchImageViewTargetFactory ，适配ImageView
        try {
            Field field;
            field = glideContext.getClass().getDeclaredField("imageViewTargetFactory");
            field.setAccessible(true);
            ImageViewTargetFactory imageViewTargetFactory = (ImageViewTargetFactory) field.get(glideContext);
            if (imageViewTargetFactory instanceof NinePngImageViewTargetFactory) {
                Log.d(TAG, "replaceImageViewTargetFactory: 不用重复设置 return");
                return;
            }
            ImageViewTargetFactory factory = new NinePngImageViewTargetFactory(imageViewTargetFactory);
            field.set(glideContext, factory);
            field.setAccessible(false);
            Log.d(TAG, "已设置NinePatchImageViewTargetFactory: 原imageViewTargetFactory=" + imageViewTargetFactory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    static GlideContext getGlideContext(@NonNull Glide glide) {
        try {
            Field field = glide.getClass().getDeclaredField("glideContext");
            field.setAccessible(true);
            GlideContext glideContext = (GlideContext) field.get(glide);
            field.setAccessible(false);
            return glideContext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
