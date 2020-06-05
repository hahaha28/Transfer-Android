package `fun`.inaction.transfer

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import inaction.`fun`.network.SocketUtil

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
        val type = `fun`.inaction.transfer.getFileType(name)
        sendFile(name,type,size,contentResolver.openInputStream(uri))
    }

}

fun SocketUtil.sendFile(uriString:String,contentResolver: ContentResolver){
    val uri = Uri.parse(uriString)
    sendFile(uri,contentResolver)
}

fun SocketUtil.sendFile(uriString:String){
    sendFile(uriString,MyApplication.getContext().contentResolver)
}

/**
 * 获取文件的后缀名
 */
fun getFileType(fileName:String):String{
    val temps = fileName.split(".")
    return if (temps.size < 2) "UnKnow" else temps[temps.size - 1]
}
