package com.github.maoqis.glide9png.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.maoqis.glide9png.NinePngGlideApi;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NinePngGlideApi.afterGlideInit(GlideApp.get(MainActivity.this.getApplicationContext()));
        setContentView(R.layout.activity_main);
        ImageView iv = findViewById(R.id.iv_test);

        String url = "https://www.xijnp.com:8888/down/0jBZcbLr8X7h.png";
        String urlChunk = "https://www.xijnp.com:8888/down/TYe2aqTbwIBj.png";

        findViewById(R.id.bt_click).setOnClickListener(v -> {
            Log.d(TAG, "bt_click: ");

            GlideApp.with(MainActivity.this)
                    .load(urlChunk)
                    .into(iv);
        });

        //sdcard 权限，网络在xml中
        checkPermissionAndLoadImg();

    }

    private void checkPermissionAndLoadImg() {
        int hasWriteExternalPermission =
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            //TODO 有权限，做自己的后续操作

        } else {
            //未授权，申请授权(从相册选择图片需要读取存储卡的权限)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
    }
}