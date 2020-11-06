package com.rajesh.imagecomprassor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rajesh.imagecompressor.ZipManager
import kotlinx.android.synthetic.main.activity_zip.*
import java.io.File


class ZipActivity : AppCompatActivity() {

    private val PICKFILE_REQUEST_CODE = 201

    private val fileArrayList = ArrayList<File>()
    private lateinit var zipAdapter: ZipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zip)

        zipAdapter = ZipAdapter(this, fileArrayList)

        rv_files.apply {
            layoutManager = LinearLayoutManager(this@ZipActivity)
            adapter = zipAdapter
        }

        btn_add.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "*/*"
                            startActivityForResult(this, PICKFILE_REQUEST_CODE)
                        }
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        //onPermissionDenied
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }
                }).check()
        }

        btn_zip.setOnClickListener {
            ZipManager.makeZip(this, fileArrayList, object : ZipManager.OnCompressListener {
                override fun onCompressCompleted(zipFile: File?) {
                    Toast.makeText(this@ZipActivity, "Zip is ready for use", Toast.LENGTH_SHORT)
                        .show()
                    val extStorageDirectory: String =
                        Environment.getExternalStorageDirectory().path
                    val newFile = File(extStorageDirectory, "TEST")
                    ZipManager.makeUnZip(
                        this@ZipActivity,
                        zipFile!!,
                        newFile,
                        object : ZipManager.OnDeCompressListener {
                            override fun onDeCompressCompleted(zipFile: File?) {
                                Toast.makeText(this@ZipActivity, "Un Zip done", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                }
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val pickFile =
                    File(ZipManager.getFilePathFromURI(this@ZipActivity, data.data!!)!!)
                fileArrayList.add(pickFile)
                zipAdapter.notifyDataSetChanged()
            }
        }
    }

}