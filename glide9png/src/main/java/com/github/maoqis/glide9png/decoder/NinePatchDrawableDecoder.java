package com.github.maoqis.glide9png.decoder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.github.maoqis.glide9png.resource.LazyNinePngDrawableResource;
import com.bumptech.glide.util.Preconditions;

import java.io.IOException;

public class NinePatchDrawableDecoder<DataType> implements ResourceDecoder<DataType, NinePatchDrawable> {
    private static final String TAG = "NinePatchDrawableDecode";
    private final ResourceDecoder<DataType, Bitmap> decoder;
    private final Resources resources;
    private final ArrayPool arrayPool;

    public NinePatchDrawableDecoder(
            @NonNull Resources resources, @NonNull ResourceDecoder<DataType, Bitmap> decoder, ArrayPool arrayPool) {
        this.resources = Preconditions.checkNotNull(resources);
        this.decoder = Preconditions.checkNotNull(decoder);
        this.arrayPool = arrayPool;
    }

    @Override
    public boolean handles(@NonNull DataType source, @NonNull Options options) throws IOException {
        boolean handles = decoder.handles(source, options);
        Log.d(TAG, "handles: is9png=" + handles + " source=" + source);
        return handles;
    }

    @Override
    public Resource<NinePatchDrawable> decode(
            @NonNull DataType source, int width, int height, @NonNull Options options)
            throws IOException {

        Log.d(TAG, "decode: source=" + source);
        Resource<Bitmap> bitmapResource = decoder.decode(source, width, height, options);
        return LazyNinePngDrawableResource.obtain(resources, bitmapResource);
    }
}
