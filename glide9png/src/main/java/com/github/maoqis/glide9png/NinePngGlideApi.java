package com.github.maoqis.glide9png;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideContext;

public class NinePngGlideApi {
    private static final String TAG = "GlideNinePngApi";


    public static void afterGlideInit(Glide glide) {
        GlideContext glideContext = NinePngGlideModule.getGlideContext(glide);
        NinePngGlideModule.replaceImageViewTargetFactory(glideContext);
    }

    /**
     * 默认使用3x图。设计稿上3px = 1dp。如果出图是720p 则配置320。
     * 该接口可以适配精细密度设备。
     * @param dpi
     */
    public static void setDesignDensityDPI(Integer dpi){
        if (dpi == null) {
            return;
        }
        NinePngGlideConfig.getInstance().designDensity = dpi;
    }


}
