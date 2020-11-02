package com.rajesh.imagecomprassor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rajesh.imagecompressor.ImageCompressor;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ImageCompressor.compressImage(this, null, new ImageCompressor.OnCompressListener() {
            @Override
            public void onCompressCompleted(@NotNull File compressFile) {

            }
        });

    }
}