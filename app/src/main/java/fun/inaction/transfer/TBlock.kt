package `fun`.inaction.transfer

/**
 * 传输队列中存储的类型
 * 如果是文件类型，data 中是uri
 * 如果是消息类型，data 中是消息
 */
class TBlock(val type:Int,val data:String) {
    companion object{
        const val TYPE_MSG = 1
        const val TYPE_FILE = 2
    }
}