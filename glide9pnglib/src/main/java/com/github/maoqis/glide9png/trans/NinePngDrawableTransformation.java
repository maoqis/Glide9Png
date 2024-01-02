package com.github.maoqis.glide9png.trans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;

import java.security.MessageDigest;

public class NinePngDrawableTransformation implements Transformation<NinePatchDrawable> {
    private static final String TAG = "NinePngDrawableTrans";
    Transformation<Bitmap> wrap;

    public NinePngDrawableTransformation(Transformation<Bitmap> wrap) {
        this.wrap = wrap;
    }

    @NonNull
    @Override
    public Resource<NinePatchDrawable> transform(@NonNull Context context, @NonNull Resource<NinePatchDrawable> resource, int outWidth, int outHeight) {
        Log.d(TAG, "transform: 不转换");
        return resource;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        wrap.updateDiskCacheKey(messageDigest);
    }
}
