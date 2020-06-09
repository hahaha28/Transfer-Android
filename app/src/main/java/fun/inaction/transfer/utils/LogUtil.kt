package `fun`.inaction.transfer.utils

import android.util.Log

object LogUtil {

    private val TAG = "MyDebug"

    /**
     * 日志开关
     * v,d,i,w,e
     */
    private val switch = intArrayOf(1,1,1,1,1)

    fun v(msg:String){
        if(switch[0] == 1){
            Log.v(TAG,msg)
        }
    }

    fun d(msg:String){
        if(switch[1] == 1){
            Log.d(TAG,msg)
        }
    }

    fun i(msg:String){
        if(switch[2] == 1){
            Log.i(TAG,msg)
        }
    }

    fun w(msg:String){
        if(switch[3] == 1){
            Log.w(TAG,msg)
        }
    }

    fun e(msg:String){
        if(switch[4] == 1){
            Log.e(TAG,msg)
        }
    }



}