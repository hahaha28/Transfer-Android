package `fun`.inaction.transfer

import java.util.*

/**
 * 待发送数据队列
 */
class TBlockQueue {

    private val queue = LinkedList<TBlock>()

    val size:Int
        get() {
            return queue.size
        }

    /**
     * 添加数据
     */
    fun add(block: TBlock) {
        synchronized(this) {
            when (block.type) {
                TBlock.TYPE_MSG -> queue.addFirst(block)
                else -> queue.add(block)
            }
        }
    }

    /**
     * 添加一堆数据
     */
    fun addBlocks(blockList:List<TBlock>){
        synchronized(this){
            blockList.forEach { block ->
                when(block.type){
                    TBlock.TYPE_MSG -> queue.addFirst(block)
                    else -> queue.add(block)
                }
            }
        }
    }

    /**
     * 从队列中取出数据
     */
    fun poll(): TBlock? {
        synchronized(this) {
            return queue.poll()
        }
    }

}