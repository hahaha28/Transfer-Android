package `fun`.inaction.transfer.utils

import `fun`.inaction.transfer.MyApplication
import `fun`.inaction.transfer.bean.FileTransferItem
import `fun`.inaction.transfer.bean.MsgTransferItem
import `fun`.inaction.transfer.bean.TransferItem
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.source
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

object TClient {

    private const val PORT = "9090"

    private var ip:String? = null
    private val okHttpClient = OkHttpClient.Builder()
        .pingInterval(10,TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val itemQueue = LinkedList<TransferItem>()

    private val handler = Handler(Looper.getMainLooper())

    private val webSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.e("tag","receive msg:${text}")
        }
    }


    fun connect(ip:String) {
        this.ip = ip;
        wsConnect()
        testConnect { success ->
            if (success) {
                looperSend()
            }
        }
    }

    private fun wsConnect() {
        val webSocketRequest = Request.Builder()
            .url("ws://${ip}:${PORT}/transfer")
            .build()
        webSocket = okHttpClient.newWebSocket(webSocketRequest, webSocketListener)
    }

    fun disConnect() {
        this.ip = null
        webSocket?.close(1000,"正常关闭")
    }

    fun testConnect(callback: (Boolean) -> Unit) {
        testHttpConnect(callback)
    }

    fun testHttpConnect(callback:(Boolean)->Unit) {
        if (ip == null) {
            handler.post {
                callback(false)
            }
        } else {
            val request = Request.Builder()
                .url("http://${ip}:${PORT}/")
                .get()
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    handler.post {
                        callback(false)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    handler.post {
                        callback(true)
                    }
                }
            })
        }
    }

    private fun upload(inputStream: InputStream, fileName:String): Call {

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
            .url("http://${ip}:${PORT}/upload")
            .post(body)
            .build()
        return okHttpClient.newCall(request)
    }


    private fun uploadFile(uri: Uri):Call? {
        val contentResolver = MyApplication.getContext().contentResolver
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
            return upload(contentResolver.openInputStream(uri)!!, name)
        }
        return null
    }

    private fun sendMsg(msg:String) {

    }

    fun send(item:TransferItem) {
        // 先加入队列
        itemQueue.push(item)

        // 有连接就发送
        testConnect { success ->
            if (success) {
                looperSend()
            } else {

            }
        }
    }

    private fun looperSend() {
        val copyItemQueue = LinkedList(itemQueue)
        copyItemQueue.forEach {
            when(it) {
                is FileTransferItem -> {
                    uploadFile(it.uri)?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            // TODO 发送失败
                        }

                        override fun onResponse(call: Call, response: Response) {
                            itemQueue.remove(it)
                        }
                    })
                }

                is MsgTransferItem -> {
                    if (webSocket?.send(it.msg) == true) {
                        itemQueue.remove(it)
                    }
                }
            }
        }
    }

}