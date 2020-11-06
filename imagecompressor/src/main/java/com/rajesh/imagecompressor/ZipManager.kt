package com.rajesh.imagecompressor

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


/**
 * Created on 02-11-2020.
 */
open class ZipManager {
    companion object {
        private const val BUFFER = 2048

        /**
         * this function is user for create one zip file of different different multiple files. it will get files from cache directory and automatically delete file after zipping completed.
         * */
        @JvmStatic
        fun makeZip(
            context: Context,
            fileForZip: ArrayList<File>,
            onCompressListener: OnCompressListener
        ) {
            try {
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

                val extStorageDirectory: String =
                    Environment.getExternalStorageDirectory().toString()
                val newFile = File(context.cacheDir, "Zip_$timeStamp.zip")
                newFile.createNewFile()

                var origin: BufferedInputStream? = null
                val dest = FileOutputStream(newFile)
                val out = ZipOutputStream(
                    BufferedOutputStream(
                        dest
                    )
                )
                val data = ByteArray(BUFFER)

                for (file: File in fileForZip) {
                    val fi = context.contentResolver.openInputStream(Uri.fromFile(file))
                    origin = BufferedInputStream(fi, BUFFER)
                    val entry = ZipEntry(file.name)
                    out.putNextEntry(entry)
                    var count: Int
                    while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                        out.write(data, 0, count)
                    }
                    origin.close()
                }
                out.close()

                for (file: File in fileForZip) {
                    file.delete()
                }
                onCompressListener.onCompressCompleted(newFile)
            } catch (e: Exception) {
                e.printStackTrace()
                onCompressListener.onCompressCompleted(null)
            }
        }

        /**
         * this function use for extract all file from zip
         * **/
        @JvmStatic
        fun makeUnZip(
            context: Context,
            zipFile: File,
            zipExtractFile: File,
            onDeCompressListener: OnDeCompressListener
        ) {
            zipExtractFile.mkdir()

            val zipFileName = zipExtractFile.path
            //create target location folder if not exist
            dirChecker(zipFileName)

            try {
                val fin = context.contentResolver.openInputStream(Uri.fromFile(zipFile))
                val zis = ZipInputStream(fin)
                var ze: ZipEntry? = null
                val buffer = ByteArray(1024)
                var count: Int
                while (zis.nextEntry.also { ze = it } != null) {
                    val filename = ze!!.name

                    // Need to create directories if not exists, or
                    // it will generate an Exception...
                    if (ze!!.isDirectory) {
                        dirChecker(zipFileName + File.separator + filename)
                        continue
                    }
                    val fout = FileOutputStream(zipFileName + File.separator + filename)
                    while (zis.read(buffer).also { count = it } != -1) {
                        fout.write(buffer, 0, count)
                    }
                    fout.close()
                    zis.closeEntry()
                }
                zis.close()
                onDeCompressListener.onDeCompressCompleted(zipExtractFile)
            } catch (e: IOException) {
                e.printStackTrace()
                onDeCompressListener.onDeCompressCompleted(null)
            }
        }

        private fun dirChecker(dir: String) {
            val f = File(dir)
            if (!f.isDirectory) {
                f.mkdirs()
            }
        }

        /**
         * this function copy your choose file in catch directory and use this file to create zip. don't worry about cache file will automatically delete after zipping is complete.
         * */
        @JvmStatic
        fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
            //copy file and send new file path
            var fileName = getFileName(contentUri)
            val lastIndex = fileName?.lastIndexOf(".")
            if (lastIndex == -1) {
                fileName += ".${getMimeType(context, contentUri!!)}"
            }
            if (!TextUtils.isEmpty(fileName)) {
                val copyFile = File("${context.cacheDir}${File.separator}$fileName")
                copying(context, contentUri, copyFile)
                return copyFile.absolutePath
            }
            return null
        }

        private fun getFileName(uri: Uri?): String? {
            if (uri == null) return null
            var fileName: String? = null
            val path = uri.path
            println("testPath:$path")
            val cut = path!!.lastIndexOf('/')
            if (cut != -1) {
                fileName = path.substring(cut + 1)
            }
            return fileName
        }

        private fun getMimeType(context: Context, uri: Uri): String? {
            //Check uri format to avoid null
            return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                //If scheme is a content
                val mime = MimeTypeMap.getSingleton()
                mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
            } else {
                //If scheme is a File
                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
            }
        }

        private fun copying(context: Context, srcUri: Uri?, dstFile: File?) {
            context.contentResolver.openInputStream(srcUri!!).use { ins ->
                FileOutputStream(dstFile).use { out ->
                    // Transfer bytes from in to out
                    val buf = ByteArray(1024)
                    var len: Int
                    while (ins!!.read(buf).also { len = it } != -1) {
                        out.write(buf, 0, len)
                    }
                }
            }
        }

    }

    interface OnCompressListener {
        fun onCompressCompleted(zipFile: File?)
    }

    interface OnDeCompressListener {
        fun onDeCompressCompleted(zipFile: File?)
    }

}