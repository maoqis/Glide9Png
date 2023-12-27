package com.github.maoqis.glide9png.decoder;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.ByteBufferUtil;
import com.github.maoqis.glide9png.utils.Constants;
import com.github.maoqis.glide9png.utils.NinePngUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferBitmap9pngDecoder implements ResourceDecoder<ByteBuffer, Bitmap> {
    private static final String TAG = "ByteBufferBitmap9pngDecoder";
    private final BitmapPool bitmapPool;


    public ByteBufferBitmap9pngDecoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }


    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {

        boolean is9png = false;
        try {
            is9png = NinePngUtils.is9png(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "handles: is9png=" + is9png);
        options.set(Constants.IS_9PNG, is9png);

        return is9png;
    }


    @Override
    public Resource<Bitmap> decode(@NonNull ByteBuffer source, int width, int height, @NonNull Options options) throws IOException {

        boolean is9png = options.get(Constants.IS_9PNG);
        Log.d(TAG, "decode: is9png=" + is9png);
        InputStream is = ByteBufferUtil.toStream(source);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, new BitmapFactory.Options());
        return BitmapResource.obtain(bitmap, bitmapPool);

    }
}