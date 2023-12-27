package com.github.maoqis.glide9png.target;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.github.maoqis.glide9png.utils.NinePngUtils;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class NinePngImageViewTarget extends BitmapImageViewTarget {
    private static final String TAG = "NinePngImageViewTarget";
    Context contextApp;

    public NinePngImageViewTarget(ImageView view) {
        super(view);
        contextApp = view.getContext();
    }

    @Override
    protected void setResource(Bitmap resource) {
        boolean is9Png = NinePngUtils.is9png(resource);
        Log.d(TAG, "setResource: is9Png=" + is9Png + " resource=" + resource);
        if (is9Png) {
            Drawable drawable = NinePngUtils.createDrawable(resource, true, contextApp.getResources());
            view.setImageDrawable(drawable);
        } else {
            view.setImageBitmap(resource);
        }
    }
}
