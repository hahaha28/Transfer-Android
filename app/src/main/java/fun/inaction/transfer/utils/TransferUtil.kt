package `fun`.inaction.transfer.utils

import `fun`.inaction.transfer.MyApplication
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import inaction.`fun`.network.SocketUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.Okio
import okio.source
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

val okHttpClient = OkHttpClient()

fun upload(inputStream: InputStream, fileName:String): Call {

    val mediaBody = object : RequestBody() {
        override fun contentType(): MediaType? {
            return "*/*".toMediaTypeOrNull()
        }

        override fun writeTo(sink: BufferedSink) {
            inputStream.source().use { source -> sink.writeAll(source) }
        }
    }
    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", fileName, mediaBody)
        .build()

    val request = Request.Builder()
        .url("http://10.12.165.42:9090/upload")
        .post(body)
        .build()
    return okHttpClient.newCall(request)
}

fun SocketUtil.sendFile(uri: Uri,contentResolver:ContentResolver){
    contentResolver.query(uri,
        null,
        null,
        null,
        null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        val size = cursor.getLong(sizeIndex)
        val type = getFileType(name)
//        sendFile(name,type,size,contentResolver.openInputStream(uri))
        upload(contentResolver.openInputStream(uri)!!,name).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("tag",e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("tag",response.body!!.string());
            }
        })
    }

}

fun SocketUtil.sendFile(uriString:String,contentResolver: ContentResolver){
    val uri = Uri.parse(uriString)
    sendFile(uri,contentResolver)
}

fun SocketUtil.sendFile(uriString:String){
    sendFile(uriString,
        MyApplication.getContext().contentResolver)
}

fun uploadFile(uri: Uri,contentResolver:ContentResolver) {
    contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        val size = cursor.getLong(sizeIndex)
        val type = getFileType(name)
//        sendFile(name,type,size,contentResolver.openInputStream(uri))
        upload(contentResolver.openInputStream(uri)!!, name).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("tag", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("tag", response.body!!.string());
            }
        })
    }
}

fun uploadFile(uriString: String) {
    uploadFile(Uri.parse(uriString),MyApplication.getContext().contentResolver)
}

/**
 * 获取文件的后缀名
 */
fun getFileType(fileName:String):String{
    val temps = fileName.split(".")
    return if (temps.size < 2) "UnKnow" else temps[temps.size - 1]
}
