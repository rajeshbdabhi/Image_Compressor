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

	ImageCompressor.compressImage(context, file, new ImageCompressor.OnCompressListener() {
            @Override
            public void onCompressCompleted(@NotNull File compressFile) {
                //here can get new compress image file
            }
        });

Version 1.0.3 allow you to make zip file and extract zip files

Usage in kotlin:

	
	Step 1:
		
		choose any kind of file which you want make zip
		
		Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "*/*"
                            startActivityForResult(this, PICKFILE_REQUEST_CODE)
           }
		
		
	Step 2:	
	
		override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        		super.onActivityResult(requestCode, resultCode, data)
        		if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            			if (data != null) {
					//This line get real path of you choosen file and copy of that so no anykind of effect to orignal file.
                			val pickFile = File(ZipManager.getFilePathFromURI(this@ZipActivity, data.data!!)!!)
                			fileArrayList.add(pickFile)
                			zipAdapter.notifyDataSetChanged()
            			}
        		}
    	}
    
    
    	Step 3:
	
    		//pass here list of file to make it on zip
		ZipManager.makeZip(this, fileArrayList, object : ZipManager.OnCompressListener {
        		override fun onCompressCompleted(zipFile: File?) {
                    	Toast.makeText(this@ZipActivity, "Zip is ready for use", Toast.LENGTH_SHORT).show()
                	}
       	})
		
		
	Step 4:
	
		//this method help to extract zip file
		val extStorageDirectory: String = Environment.getExternalStorageDirectory().path
            	val newFile = File(extStorageDirectory, "TEST")
            	ZipManager.makeUnZip(this@ZipActivity, zipFile, newFile, object : ZipManager.OnDeCompressListener {
                    override fun onDeCompressCompleted(zipFile: File?) {
                        Toast.makeText(this@ZipActivity, "Un Zip done", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
	
