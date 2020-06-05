package `fun`.inaction.transfer

import android.util.Log
import inaction.`fun`.network.SocketUtil
import java.util.*
import kotlin.concurrent.thread

class SendLooper {

    /**
     * 待发送的队列
     */
    private val queue:TBlockQueue

    /**
     * 标记是否正在循环
     */
    private var isLoop = false

    /**
     * 循环的锁
     */
    private val loopLock: Any = Any()

    /**
     * 队列的锁
     */
    private val queueLock: Any = Any()

    var socketUtil: SocketUtil

    constructor(queue:TBlockQueue,socketUtil: SocketUtil) {
        this.queue = queue
        this.socketUtil = socketUtil
    }

    /**
     * 依次发送队列中的数据
     *  已开启线程
     */
    fun loop() {
        if (!isLoop) {
            thread {
                synchronized(loopLock) {
                    if (!isLoop) {
                        isLoop = true
                        while (queue.size != 0) {
                            val block = queue.poll()
                            block?.let {
                                sendBlock(it)
                            }
                            Log.e("MyDebug","one block over,size = ${queue.size}")
                        }
                        isLoop = false
                    }
                }
            }
        }
    }

    /**
     * 发送
     */
    private fun sendBlock(block: TBlock) {
        Log.e("MyDebug","start send Block")
        when (block.type) {
            TBlock.TYPE_MSG -> socketUtil.sendMsg(block.data)
            else -> socketUtil.sendFile(block.data)
        }
    }

}