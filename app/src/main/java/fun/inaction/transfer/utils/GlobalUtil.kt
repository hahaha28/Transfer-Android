package `fun`.inaction.transfer.utils

import `fun`.inaction.transfer.MyApplication
import android.os.Build
import kotlin.Exception

/**
 * 获取资源的颜色
 */
fun getResourceColor(id:Int):Int{
    val context = MyApplication.getContext()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return context.getColor(id)
    }else{
        return context.resources.getColor(id)
    }
}

fun Exception.getInfo():String{
    var s:String = ""
    for(i in stackTrace){
        s = s + i.toString()+"\n"
    }
    return s
}