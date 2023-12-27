package com.github.maoqis.glide9png.encoder;

import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;

import java.io.File;

public class NinePngDrawableEncoder implements ResourceEncoder<NinePatchDrawable>  {
   private static final String TAG = "NinePngDrawableEncoder";
   private final NinePngBitmapEncoder bitmapEncoder;

   public NinePngDrawableEncoder(NinePngBitmapEncoder bitmapEncoder) {
      this.bitmapEncoder = bitmapEncoder;
   }

   @NonNull
   @Override
   public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
      Log.d(TAG, "getEncodeStrategy: SOURCE");
      return EncodeStrategy.SOURCE;
   }

   @Override
   public boolean encode(@NonNull Resource<NinePatchDrawable> data, @NonNull File file, @NonNull Options options) {
      Log.d(TAG, "encode: 不保存NinePatchDrawable -> file。使用原数据流保存文件");
      return true;
   }
}
