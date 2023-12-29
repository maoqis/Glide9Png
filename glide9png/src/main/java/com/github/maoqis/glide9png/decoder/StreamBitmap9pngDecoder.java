package com.github.maoqis.glide9png.decoder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.github.maoqis.glide9png.utils.Constants;
import com.github.maoqis.glide9png.utils.NinePngUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decodes Webp {@link Bitmap Bitmaps} from {@link InputStream InputStreams}.
 * For static lossless and transparent webp
 *
 * @author liuchun
 */
public class StreamBitmap9pngDecoder implements ResourceDecoder<InputStream, Bitmap> {
    private static final String TAG = "StreamBitmap9pngDecoder";
    private final BitmapPool bitmapPool;
    private final ArrayPool arrayPool;
    private final Resources resources;


    public StreamBitmap9pngDecoder(BitmapPool bitmapPool, ArrayPool arrayPool, Resources resources) {
        this.bitmapPool = bitmapPool;
        this.arrayPool = arrayPool;
        this.resources = resources;
    }

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
        Log.d(TAG, "handles: ");
        boolean is9png = NinePngUtils.is9png(source, arrayPool);
        Log.d(TAG, "handles: is9png=" + is9png);
        options.set(Constants.IS_9PNG, is9png);

        return is9png;
    }

    @Override
    public Resource<Bitmap> decode(@NonNull InputStream source, int width, int height, @NonNull Options options) throws IOException {
        Log.d(TAG, "decode:");
        Bitmap bitmap = NinePngUtils.getNineBitmap(source, resources);
        return BitmapResource.obtain(bitmap, bitmapPool);
    }


}
