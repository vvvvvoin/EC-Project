package com.example.firstkotlinapp.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun ContentResolver.getFileName(uri : Uri) : String{
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use{
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun makeMultipartBody(uri: Uri, context: Context): MultipartBody.Part {
    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r", null)
    val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
    val file = File(context.cacheDir, context.contentResolver.getFileName(uri))
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    val requestBody: RequestBody =  file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", file.name, requestBody)
}