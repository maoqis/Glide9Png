package com.github.maoqis.glide9png.trans;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.github.maoqis.glide9png.utils.NinePngUtils;

import java.security.MessageDigest;

public class NinePngTransformationWrap implements Transformation<Bitmap> {
    private static final String TAG = "NinePngTransformationWr";
    Transformation<Bitmap> bitmapTransformation;

    public NinePngTransformationWrap(Transformation<Bitmap> bitmapTransformation) {
        this.bitmapTransformation = bitmapTransformation;
    }


    @NonNull
    @Override
    public Resource transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap bitmap = resource.get();
        boolean is9png = NinePngUtils.is9png(bitmap);
        Log.d(TAG, "transform: is9png=" + is9png + " skip " + bitmapTransformation);
        if (is9png) {
            return resource;
        }
        return bitmapTransformation.transform(context, resource, outWidth, outHeight);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        bitmapTransformation.updateDiskCacheKey(messageDigest);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + " bitmapTransformation=" + bitmapTransformation;
    }
}
