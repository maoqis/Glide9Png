package com.github.maoqis.glide9png;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideContext;
import com.github.maoqis.glide9png.target.NinePngImageViewTargetFactory;

public class NinePngGlideApi {
    private static final String TAG = "GlideNinePngApi";


    public static void afterGlideInit(Glide glide) {
        GlideContext glideContext = NinePngGlideModule.getGlideContext(glide);
        NinePngGlideModule.replaceImageViewTargetFactory(glideContext);
    }


}
