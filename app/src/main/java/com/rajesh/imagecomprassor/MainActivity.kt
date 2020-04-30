package com.rajesh.imagecomprassor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rajesh.imagecompreser.ImageCompreser
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE1 = 101
    private val GALLERY_REQUEST_CODE1 = 201


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_gallery.setOnClickListener(this)
        btn_camera.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_gallery -> {
                Dexter.withActivity(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            galleryIntent.type = "image/*"
                            // Start the Intent
                            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE1)
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest()
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                        }
                    }).check()
            }
            R.id.btn_camera -> openCamera()
        }
    }

    private fun openCamera() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        // Ensure that there's a camera activity to handle the intent
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            // Create the File where the photo should go
                            val photoFile: File? = try {
                                createImageFile()
                            } catch (ex: IOException) {
                                null
                            }

                            // Continue only if the File was successfully created
                            photoFile?.also {
                                val photoURI: Uri = FileProvider.getUriForFile(
                                    this@MainActivity,
                                    packageName + ".fileprovider",
                                    it
                                )
                                takePictureIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    photoURI
                                )
                                startActivityForResult(
                                    takePictureIntent,
                                    CAMERA_CAPTURE_IMAGE_REQUEST_CODE1
                                )
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }
            }).check()
    }

    var currentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = cacheDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE1) {
            if (data != null) {
                try {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                    // Get the cursor
                    val cursor = contentResolver.query(
                        selectedImage!!,
                        filePathColumn, null, null, null
                    )
                    // Move to first row
                    cursor!!.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val imgDecodableString = cursor.getString(columnIndex)

                    val b = BitmapFactory.decodeFile(imgDecodableString)

                    val file = File(imgDecodableString)
                    cursor.close()

                    if (file.exists()) {
                        tv_original_size.text = ImageCompreser.getFileSize(file)
                        ImageCompreser.compressImage(
                            this@MainActivity,
                            file,
                            object : ImageCompreser.OnCompressListener {
                                override fun onCompressCompleted(compressFile: File) {
                                    tv_compress_size.text = ImageCompreser.getFileSize(compressFile)
                                    val bitmap = BitmapFactory.decodeFile(compressFile.absolutePath)
                                    // Set the Image in ImageView after decoding the String
                                    iv_img.setImageBitmap(bitmap)
                                }
                            })
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE1) {
            if (resultCode == Activity.RESULT_OK) {

                val file = File(currentPhotoPath)
                if (file.exists()) {
                    tv_original_size.text = ImageCompreser.getFileSize(file)
                    ImageCompreser.compressImage(
                        this@MainActivity,
                        file,
                        object : ImageCompreser.OnCompressListener {
                            override fun onCompressCompleted(compressFile: File) {
                                tv_compress_size.text = ImageCompreser.getFileSize(compressFile)
                                val bitmap = BitmapFactory.decodeFile(compressFile.absolutePath)
                                // Set the Image in ImageView after decoding the String
                                iv_img.setImageBitmap(bitmap)
                            }
                        })
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(this, "User cancelled image capture", Toast.LENGTH_SHORT).show()
            } else {
                // failed to capture image
                Toast.makeText(this, "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show()
            }

        }
    }

}
