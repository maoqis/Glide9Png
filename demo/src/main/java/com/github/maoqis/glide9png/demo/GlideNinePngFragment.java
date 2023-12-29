package com.github.maoqis.glide9png.demo;

import static android.os.Environment.DIRECTORY_PICTURES;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.maoqis.android.base.component.BaseCaseFragment;
import com.github.maoqis.glide9png.NinePngGlideApi;
import com.github.maoqis.glide9png.encoder.NinePngBitmapEncoder;
import com.github.maoqis.glide9png.utils.NinePngUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GlideNinePngFragment extends BaseCaseFragment {
    private static final String TAG = "Glide9pngFragment";

    @Override
    protected int getRLayout() {
        return R.layout.fragment_glide_9png;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onInitView(View rootView) {


        ImageView ivH48 = rootView.findViewById(R.id.iv_h48);
        ImageView ivAPPT = rootView.findViewById(R.id.iv_appt);

        //直接显示原9.png 有黑边，缺少Bitmap中的9.png 的chunk信息。
        String url = "https://www.xijnp.com:8888/down/0jBZcbLr8X7h.png";
        String urlChunk = "https://www.xijnp.com:8888/down/TYe2aqTbwIBj.png";
        File fileChuck = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), "ninepatch_bubble_chunk.9.png");


        //source not appt
        findSetOnClickListener(R.id.tv_source_not_appt, v -> {

            String fileName = "ninepatch_bubble.9.png";
            try {
                GlideApp.with(this).asBitmap()
                        .dontTransform()//
                        .load(url)
//                        .load(getContext().getAssets().open(fileName))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                Drawable drawable = get9pngBitmapDrawable(bitmap);
                                ivH48.setImageDrawable(drawable);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                Log.d(TAG, "onLoadCleared() called with: placeholder = [" + placeholder + "]");
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //原因
        findSetOnClickListener(R.id.tv_not_appt, v -> {
            String fileName = "ninepatch_bubble.9.png";
            Bitmap assetBitmap = getAssetBitmap(fileName);
            Drawable pngBitmapDrawable = get9pngBitmapDrawable(assetBitmap);
            ivAPPT.setImageDrawable(pngBitmapDrawable);
        });


        //方案：https://www.jianshu.com/p/d3a7d5edb7bd中提到 ，使用 'appt s -i 来源 -o 目标' ，编译9.png添加块信息。
        findSetOnClickListener(R.id.tv_appt, v -> {
            String fileName = "ninepatch_bubble_chunk.9.png";
            Bitmap assetBitmap = getAssetBitmap(fileName);
            Drawable pngBitmapDrawable = get9pngBitmapDrawable(assetBitmap);

            ivAPPT.setImageDrawable(pngBitmapDrawable);


        });
        findSetOnClickListener(R.id.tv_glide_appt_only_download, v -> {
            Log.d(TAG, "ok, downloadOnly时候，解析获取的bitmap，下载有缓存。 ，方案：https://www.jianshu.com/p/d3a7d5edb7bd");
            GlideApp.with(this)
                    .load(urlChunk)
                    .downloadOnly(new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            String path = resource.getPath();
                            Log.d(TAG, "onResourceReady: " + path);
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inJustDecodeBounds = true;
                            Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
                            boolean is9Png = NinePngUtils.is9png(bitmap);
                            Log.d(TAG, "onResourceReady: inJustDecodeBounds is9png=" + is9Png + " outMimeType=" + opts.outMimeType);
                            is9Png = NinePngUtils.is9png(resource);
                            Log.d(TAG, "onResourceReady: read chuck by file is9Png=" + is9Png);

                            opts.inJustDecodeBounds = false;
                            bitmap = BitmapFactory.decodeFile(path, opts);
                            Drawable pngBitmapDrawable = get9pngBitmapDrawable(bitmap);
                            ivAPPT.setImageDrawable(pngBitmapDrawable);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        });

        findSetOnClickListener(R.id.tv_glide_decoder, v -> {


//            no. debug发现，读取文件时候就丢失了块信息。要研读的时候是否可以直接读原文件。或者是因为ByteBufferReader的问题。
//            ByteBufferReader ，BitmapFactory.decodeStream(stream(), /* outPadding= */ null, options);
//            ByteBufferUtil.toStream(ByteBufferUtil.rewind(buffer));
            Log.d(TAG, "no,when 直接Glide加载 。Downsampler 采用压缩导致。");
            Registry registry = GlideApp.get(getActivity()).getRegistry();
            Log.d(TAG, "onInitView: registry=" + registry);
            GlideApp.with(getActivity())
                    .asBitmap()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .load(fileChuck)
                    .load(urlChunk)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            Drawable drawable = get9pngBitmapDrawable(bitmap);
                            ivAPPT.setImageDrawable(drawable);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d(TAG, "onLoadCleared() called with: placeholder = [" + placeholder + "]");
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }
                    });
        });
        findSetOnClickListener(R.id.tv_glide_custom_9png, v -> {

            NinePngGlideApi.afterGlideInit(GlideApp.get(this.getActivity().getApplicationContext()));

            /**
             *需要去掉，{@link NinePngBitmapEncoder}
             */

            GlideApp.with(getActivity())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontTransform()//默认的变化要去掉。
                    .load(urlChunk)
//                    .load(fileChuck)
                    .into(ivAPPT);
        });

        findSetOnClickListener(R.id.tv_glide_9png_cache, v -> {
            Log.d(TAG, "tv_glide_9png_cache: ");
            //set in application.onCreate

            NinePngGlideApi.afterGlideInit(GlideApp.get(this.getActivity().getApplicationContext()));
            /**
             * NineBitmapEncoder中直接返回了Source策略，保存的file不做转化。
             * 见：/data/user/0/com.maoqis.testcase/cache/image_manager_disk_cache23ae35c87a0847140a03006afa1a6d82840f64c371125bee1463e3f1840edd21.0
             */
            GlideApp.with(getActivity())
                    .asBitmap()
                    .load(urlChunk)
                    .dontTransform()//如果没配置使用NinePngGlideExtension，则需要配置dontTransform，排查into时候Transform。
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivAPPT);

        });

        //9,
        findSetOnClickListener(R.id.tv_glide_9png_skip_trans, v -> {
            Log.d(TAG, "tv_glide_9png_skip_trans: ");

            NinePngGlideApi.afterGlideInit(GlideApp.get(this.getActivity().getApplicationContext()));
            GlideApp.with(getActivity())
                    .asBitmap()
                    .load(urlChunk)
//                    .dontTransform()// 使用NinePngGlideExtension 拦截了optionalCenterCrop。
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivAPPT);

        });
        //10
        findSetOnClickListener(R.id.tv_glide_9png_drawable, v -> {
            Log.d(TAG, "tv_glide_9png_drawable: ");

            NinePngGlideApi.afterGlideInit(GlideApp.get(this.getActivity().getApplicationContext()));
            GlideApp.with(getActivity())
                    .load(urlChunk)
//                    .asBitmap()//不用asBitmap。
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivAPPT);

        });

        findSetOnClickListener(R.id.tv_set_fit_xy, v -> {
            Log.d(TAG, "tv_set_fit_xy: ");
            NinePngGlideApi.afterGlideInit(GlideApp.get(this.getActivity().getApplicationContext()));
            ivAPPT.setScaleType(ImageView.ScaleType.CENTER_CROP);
            GlideApp.with(this)
                    .load(urlChunk)
                    .into(ivAPPT);
        });

        findSetOnClickListener(R.id.tv_restore_scale_type, v -> {
            Log.d(TAG, "tv_restore_scale_type: ");
            NinePngGlideApi.afterGlideInit(GlideApp.get(this.getActivity().getApplicationContext()));
            GlideApp.with(this)
                    .load(url)
                    .into(ivAPPT);
        });
    }


    @Nullable
    private Bitmap getAssetBitmap(String fileName) {

        InputStream is = null;
        try {
            is = getContext().getAssets().open(fileName);
            Rect outPadding = new Rect();
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, new BitmapFactory.Options());
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @NonNull
    private Drawable get9pngBitmapDrawable(@NonNull Bitmap bitmap) {
        boolean is9png = NinePngUtils.is9png(bitmap);


        Resources resources = getContext().getResources();
        if (is9png) {
            Log.d(TAG, "onResourceReady: is9png");
            Toast.makeText(getContext(), "is 9png", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onResourceReady: is not 9png");
            Toast.makeText(getContext(), "isn't 9png", Toast.LENGTH_SHORT).show();
        }
        Drawable drawable = NinePngUtils.createDrawable(bitmap, is9png, resources);
        return drawable;
    }

}
