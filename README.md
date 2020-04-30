# Image_Compressor
Image Compressor
[![](https://jitpack.io/v/rajeshbdabhi/Image_Compressor.svg)](https://jitpack.io/#rajeshbdabhi/Image_Compressor)

This library use full for compress large size image file.

It will compress image in kb like whatsapp without losing it quality.

It will store new compress file in your phone catch without touch your orignal file

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories
	
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


Step 2. Add the dependency

Add it in your app level build.gradle

	dependencies {
    		implementation 'com.github.rajeshbdabhi:Image_Compressor:latest-version'
	}
	
Usage in kotlin:

	ImageCompressor.compressImage(
                        context,
                        file,
                        object : ImageCompressor.OnCompressListener {
                            override fun onCompressCompleted(compressFile: File) {
                                //here can get new compress image file
                            }
                        })
			

Usage in java:

	ImageCompressor.Companion.compressImage(context, file, new ImageCompressor.OnCompressListener() {
            @Override
            public void onCompressCompleted(@NotNull File compressFile) {
                //here can get new compress image file
            }
        });
