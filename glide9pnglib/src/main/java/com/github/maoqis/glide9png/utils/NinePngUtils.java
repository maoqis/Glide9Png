package com.github.maoqis.glide9png.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.github.maoqis.glide9png.NinePngGlideConfig;
import com.github.maoqis.glide9png.io.IntReader;
import com.github.maoqis.glide9png.io.ByteBufferReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class NinePngUtils {


    private static final String TAG = "NinePngUtils";
    /**
     * TODO: 需要计算9png 块(npTc)的数据。
     * 可以结果反推，第一个块HDR，应该就在后面几个块中。如果npTc块位置在后面，那读取就不方便。
     */
    public static final int MAX_32_NUM_9PNG_HEAD = 32;//200byte = 4byte * {(Png mark: 2) + [(HDR chunk: 1+1+13/4) + ... + (npTc: 2+?)]}


    @NonNull
    public static Drawable createDrawable(@NonNull Bitmap bitmap, boolean is9png, Resources resources) {
        Drawable drawable;
        if (is9png) {
            drawable = new NinePatchDrawable(resources, bitmap, bitmap.getNinePatchChunk(), null, null);
        } else {
            drawable = new BitmapDrawable(resources, bitmap);
        }
        return drawable;
    }


    public static boolean is9png(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] bytes = loadNinePatchChunk(fileInputStream);
            return is9png(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean is9png(@NonNull InputStream source, ArrayPool arrayPool) throws IOException {
        NinePngImageHeaderParser ninePngImageHeaderParserWrap = new NinePngImageHeaderParser();
        ArrayList<ImageHeaderParser> parsers = new ArrayList<>();
        parsers.add(ninePngImageHeaderParserWrap);
        ImageHeaderParserUtils.getType(parsers, source, arrayPool);

        boolean is9png = ninePngImageHeaderParserWrap.is9png;
        return is9png;
    }

    public static boolean is9png(@NonNull ByteBuffer source) throws IOException {
        NinePngImageHeaderParser ninePngImageHeaderParserWrap = new NinePngImageHeaderParser();
        ArrayList<ImageHeaderParser> parsers = new ArrayList<>();
        parsers.add(ninePngImageHeaderParserWrap);
        ImageHeaderParserUtils.getType(parsers, source);

        boolean is9png = ninePngImageHeaderParserWrap.is9png;
        return is9png;
    }

    static boolean is9pngInner(InputStream source) {
        try {
            byte[] bytes = loadNinePatchChunk(source);
            return is9png(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    static boolean is9pngInner(ByteBuffer source) {
        try {
            byte[] bytes = loadNinePatchChunk(source);
            return is9png(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean is9png(@NonNull Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        byte[] chunk = bitmap.getNinePatchChunk();
        return is9png(chunk);
    }

    public static boolean is9png(byte[] chunk) {
        boolean is9png;
        if (chunk == null) {
            is9png = false;
            Log.d(TAG, "get9pngBitmapDrawable: chunk == null");
        } else {
            if (NinePatch.isNinePatchChunk(chunk)) {
                is9png = true;
            } else {
                is9png = false;
            }
        }
        return is9png;
    }

    public static byte[] loadNinePatchChunk(InputStream is) throws IOException {
        IntReader reader = new IntReader(is, true);
        // check PNG signature
        // A PNG always starts with an 8-byte signature: 137 80 78 71 13 10 26 10 (decimal values).
        if (reader.readInt() != 0x89504e47 || reader.readInt() != 0x0D0A1A0A) {
            return null;
        }
        int max32Num = MAX_32_NUM_9PNG_HEAD;
        while (true) {
            if (max32Num <= 0) {
                return null;
            }
            max32Num--;
            int length = reader.readInt();
            int type = reader.readInt();
            // check for nine patch chunk type (npTc)
            if (type != 0x6E705463) {
                reader.skip(length + 4/*crc*/);
                continue;
            }
            return reader.readByteArray(length);
        }
    }


    public static byte[] loadNinePatchChunk(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer == null) {
            return null;
        }

        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        ByteBufferReader reader = new ByteBufferReader(byteBuffer);
        // check PNG signature
        // A PNG always starts with an 8-byte signature: 137 80 78 71 13 10 26 10 (decimal values).
        if (reader.getUInt32() != 0x89504e47 || reader.getUInt32() != 0x0D0A1A0A) {
            return null;
        }
        int max32Num = MAX_32_NUM_9PNG_HEAD;
        while (true) {
            if (max32Num <= 0) {
                return null;
            }
            max32Num--;
            int length = reader.getUInt32();
            int type = reader.getUInt32();
            // check for nine patch chunk type (npTc)
            if (type != 0x6E705463) {
                reader.skip(length + 4/*crc*/);
                continue;
            }
            return reader.readByteArray(length);
        }
    }

    @Nullable
    public static Bitmap getNineBitmap(InputStream is, Resources resources) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        int srcDpi = resources.getDisplayMetrics().densityDpi;
        opts.inDensity = NinePngGlideConfig.getInstance().designDensity;
        Log.d(TAG, "getNineBitmap: densityDpi=" + srcDpi + " to designDensity（3倍图默认480）=" + opts.inDensity);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
        return bitmap;
    }
}
