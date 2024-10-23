package com.paycraft.file_picker

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.paycraft.BuildConfig
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


fun File.mimeType(context: Context): String? {
    val uri = Uri.fromFile(this)
    val cR: ContentResolver = context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(uri))
}

fun Uri.fileFromUri(file: File, context: Context) {
    val inputStream: InputStream? = context.contentResolver.openInputStream(this)
    val outputStream: OutputStream = FileOutputStream(file)
    val buf = ByteArray(1024)
    var len: Int
    inputStream?.let {
        while (it.read(buf).also { len = it } > 0) {
            outputStream.write(buf, 0, len)
        }
    }
    outputStream.close()
    inputStream?.close()
}

fun Fragment.newFile(context: Context, ext: String): File = aFile(context, ext)

fun aFile(context: Context, ext: String) = File(
    context.cacheDir,
    "${UUID.randomUUID()}$ext"
)

fun File.uriFromFile(context: Context): Uri = FileProvider.getUriForFile(
    context,
    BuildConfig.APPLICATION_ID + ".provider",
    this
)

suspend fun compressImageTo(context: Context, inputFile: File, targetSizeKB: Long): File? {
    Log.d("FilePickerExt", "compressImageTo: Before : " + inputFile.length())
    return try {
        // Compress the image using Compressor library
        Compressor.compress(context, inputFile) {
            // Specify the target size in bytes (500KB)
            resolution(640, 360)
            quality(80)
            size(targetSizeKB * 1024) // 2 MB
        }.also {
            Log.d("FilePickerExt", "compressImageTo: After : " + it.length())
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
