# Image_Compreser
Image Compreser
[![](https://jitpack.io/v/rajeshbdabhi/Image_Compreser.svg)](https://jitpack.io/#rajeshbdabhi/Image_Compreser)

This library use full for compress large size images without lose it quality

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
    		implementation 'com.github.rajeshbdabhi:Image_Compreser:latest-version'
	}
	
Usage:

ImageCompreser.compressImage(
                        context,
                        file,
                        object : ImageCompreser.OnCompressListener {
                            override fun onCompressCompleted(compressFile: File) {
                                //here can get new compress image file
                            }
                        })
