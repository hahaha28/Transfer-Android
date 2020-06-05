package `fun`.inaction.transfer

import `fun`.inaction.transfer.databinding.ActivityMainBinding
import `fun`.inaction.transfer.events.NewFilesEvent
import `fun`.inaction.transfer.events.NewMsgEvent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.MessageQueue
import android.util.Log
import android.view.View
import inaction.`fun`.data.transfer.TransferImpl
import inaction.`fun`.network.Client
import inaction.`fun`.network.SocketUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.ConnectException
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var client: Client? = null
    private var socketUtil: SocketUtil? = null
    private var sendLooper: SendLooper? = null

    /**
     * 传输队列
     */
    private val transferQueue = TBlockQueue()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EventBus.getDefault().register(this)

    }

    /**
     * 点击连接按钮
     */
    fun onClickConnect(v:View){
        thread{
            val ip = ipText.text.toString().trim()
            if(client == null || !client!!.serverIp.equals(ip)) {
                try {
                    client = Client(ip, 8888)
                    socketUtil = SocketUtil(client?.connect(), TransferImpl::class.java)
                    sendLooper = SendLooper(transferQueue, socketUtil!!)
                    sendLooper?.loop()
                }catch (e:ConnectException){
                    // 连接失败
                    e.printStackTrace()
                }
            }
        }

    }

    /**
     * 点击选择文件按钮
     */
    fun onClickPickFileButton(v: View){
        val intent = Intent(this,PickFileActivity::class.java)
        startActivity(intent)
    }

    /**
     * 点击消息按钮
     */
    fun onClickMsg(v:View){
        startActivity(Intent(this,SendMsgActivity::class.java))
    }

    /**
     * 点击检查连接按钮
     */
    fun onClickCheckConnect(v:View){

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onNewFilesEvent(event: NewFilesEvent){
        val blockList = LinkedList<TBlock>()
        event.uriList.forEach { uri->
            blockList.add(TBlock(TBlock.TYPE_FILE,uri.toString()))
        }
        transferQueue.addBlocks(blockList)
        sendLooper?.loop()
    }

    @Subscribe
    fun onNewMsgEvent(event:NewMsgEvent){
        val block = TBlock(TBlock.TYPE_MSG,event.msg)
        transferQueue.add(block)
        sendLooper?.loop()
    }



    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()

    }



}
