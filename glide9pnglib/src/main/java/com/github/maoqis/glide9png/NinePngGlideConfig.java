package com.github.maoqis.glide9png;

import com.github.maoqis.glide9png.utils.Constants;

public class NinePngGlideConfig {
    public int designDensity = Constants.DESIGN_DENSITY_160_3;

    private static class SingletonHolder {
        private static final NinePngGlideConfig INSTANCE = new NinePngGlideConfig();
    }

    private NinePngGlideConfig() {
    }

    public static final NinePngGlideConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
