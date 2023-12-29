package com.github.maoqis.glide9png.target;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.DrawableImageViewTarget;

public class NineDrawableImageViewTarget extends DrawableImageViewTarget {
    private static final String TAG = "NineDrawableImageViewTa";

    public NineDrawableImageViewTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(@Nullable Drawable resource) {
        boolean is9Png = false;
        if (resource instanceof NinePatchDrawable) {
            is9Png = true;
        }
        Log.d(TAG, "setResource: is9Png=" + is9Png
                + " resource=" + resource);
        if (is9Png) {
            NineTargetUtils.setFitXYFor9Png(view);
            super.setResource(resource);
        } else {
            //加载新的资源时候，会先设置resource = null，这样就恢复了上次ImageView scaleType，并清除了恢复标记
            NineTargetUtils.restoreScaleType(view);
            super.setResource(resource);
        }
    }

}
