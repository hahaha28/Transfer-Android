package `fun`.inaction.transfer

import `fun`.inaction.transfer.databinding.ActivitySendMsgBinding
import `fun`.inaction.transfer.events.NewMsgEvent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_send_msg.*
import org.greenrobot.eventbus.EventBus

class SendMsgActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendMsgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 设置标题
        supportActionBar?.title = "发送消息"

        msgText.requestFocus()
    }

    fun onClickSend(v: View){
        val msg = msgText.text.toString()
        EventBus.getDefault().post(NewMsgEvent(msg))
    }

}
