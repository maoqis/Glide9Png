package com.github.maoqis.glide9png.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

class NinePngImageHeaderParser implements ImageHeaderParser {
    private static final String TAG = "NineImageHeaderParser";
    public boolean is9png = false;
    DefaultImageHeaderParser defaultImageHeaderParser = new DefaultImageHeaderParser();

    @NonNull
    @Override
    public ImageType getType(@NonNull InputStream is) throws IOException {
        Log.d(TAG, "getType: InputStream");
        is9png = NinePngUtils.is9pngInner(is);
        Log.d(TAG, "is9png =" + is9png);
        return ImageType.UNKNOWN;
    }

    @NonNull
    @Override
    public ImageType getType(@NonNull ByteBuffer byteBuffer) throws IOException {
        Log.d(TAG, "getType: ByteBuffer");
        is9png = NinePngUtils.is9pngInner(byteBuffer);
        Log.d(TAG, "is9png =" + is9png);
        return ImageType.UNKNOWN;

    }

    @Override
    public int getOrientation(@NonNull InputStream is, @NonNull ArrayPool byteArrayPool) throws IOException {
        return defaultImageHeaderParser.getOrientation(is, byteArrayPool);
    }

    @Override
    public int getOrientation(@NonNull ByteBuffer byteBuffer, @NonNull ArrayPool byteArrayPool) throws IOException {
        return defaultImageHeaderParser.getOrientation(byteBuffer, byteArrayPool);
    }
}
