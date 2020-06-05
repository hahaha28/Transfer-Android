package `fun`.inaction.transfer

import `fun`.inaction.transfer.events.NewFilesEvent
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import org.greenrobot.eventbus.EventBus

class PickFileActivity : AppCompatActivity() {

    private val PICK_ALL_FROM_SAF:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_file)
    }

    fun onClickSAF(v: View){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent,PICK_ALL_FROM_SAF)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        when(requestCode){
            PICK_ALL_FROM_SAF -> {
                data?.data?.let { uri ->
                    postNewFile(uri)
                }
            }
        }

    }

    /**
     * 推送新文件到传输队列
     */
    fun postNewFile(uri: Uri){
        EventBus.getDefault().post(
            NewFilesEvent(
                listOf(uri)
            )
        )
    }

    /**
     * 推送新文件到传输队列
     */
    fun postNewFiles(uriList:List<Uri>){
        EventBus.getDefault().post(
            NewFilesEvent(
                uriList
            )
        )
    }

}
