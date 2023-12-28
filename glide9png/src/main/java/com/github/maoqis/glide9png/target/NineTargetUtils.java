package com.github.maoqis.glide9png.target;

import android.util.Log;
import android.widget.ImageView;

public class NineTargetUtils {

    private static final String TAG = "NineTargetUtils";
    public static final int TAG_9PNG_LAST_SCALE_TYPE = "TAG_9PNG_LAST__SCALE_TYPE".hashCode();

    public static void setFitXYFor9Png(ImageView view) {
        Log.d(TAG, "setFitXyFor9Png: ");

        ImageView.ScaleType scaleType = view.getScaleType();
        if (scaleType != ImageView.ScaleType.FIT_XY) {
            Log.d(TAG, "setFitXyFor9Png: 使用FIT_XY 加载9.png, 非9png时候恢复");
            //方便恢复
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setTag(TAG_9PNG_LAST_SCALE_TYPE, scaleType);
        }
    }

    public static void restoreScaleType(ImageView view) {
        Object tag = view.getTag(TAG_9PNG_LAST_SCALE_TYPE);
        if (tag instanceof ImageView.ScaleType) {
            Log.d(TAG, "restoreScaleType: ScaleType=" + tag);
            view.setScaleType((ImageView.ScaleType) tag);
        }
    }
}
