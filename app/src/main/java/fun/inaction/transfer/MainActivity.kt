package `fun`.inaction.transfer

import `fun`.inaction.dialog.dialogs.CommonDialog
import `fun`.inaction.transfer.databinding.ActivityMainBinding
import `fun`.inaction.transfer.events.NewFilesEvent
import `fun`.inaction.transfer.events.NewMsgEvent
import `fun`.inaction.transfer.utils.LogUtil
import `fun`.inaction.transfer.utils.getInfo
import `fun`.inaction.transfer.utils.getResourceColor
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.util.function.BinaryOperator
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

        // 连接开关的监听
        connectSwitch.setOnCheckedChangeListener{v,isCheck->
            if(isCheck){
                connect()
                ipText.isCursorVisible = false
            }else{
                disConnect()
            }
        }
        setConnectText(ConnectStatus.DISCONNECT)

    }

    /**
     * 检查ip
     */
    fun checkIp(ip:String):Boolean{
        val temps = ip.split(".")
        if(temps.size == 4){
            for(temp in temps){
                if(temp.length > 3 || temp.isEmpty()){
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     * 连接
     */
    fun connect(){
        // 设置连接信息
        setConnectText(ConnectStatus.CONNECTING)
        val ip = ipText.text.toString().trim()
        LogUtil.i("Server IP = $ip")
        // 先检查IP是否合法
        LogUtil.i("检查IP")
        if(!checkIp(ip)){
            // 非法IP
            ipTextLayout.error = "非法IP！"
            setConnectText(ConnectStatus.DISCONNECT)
            connectSwitch.isChecked = false
            LogUtil.i("非法IP")
            return
        }
        ipTextLayout.error = null
        //
        thread{
            if(socketUtil == null || socketUtil!!.isClosed) {
                try {
                    LogUtil.i("开始连接")
                    client = Client(ip, 8888)
                    socketUtil = SocketUtil(client?.connect(3000), TransferImpl::class.java)
                    sendLooper = SendLooper(transferQueue, socketUtil!!)
                    sendLooper?.loop()
                    // 连接成功，显示信息
                    runOnUiThread {
                        setConnectText(ConnectStatus.CONNECTED)
                        LogUtil.i("连接至 $ip 成功")
                    }
                }catch (e:Exception){
                    // 连接失败
                    runOnUiThread {
                        setConnectText(ConnectStatus.DISCONNECT)
                        connectSwitch.isChecked = false
                        showConnectFailDialog()
                        LogUtil.i("连接至 $ip 失败")
                    }
                    LogUtil.e(e.getInfo())
                }
            }else{
                runOnUiThread {
                    setConnectText(ConnectStatus.DISCONNECT)
                    connectSwitch.isChecked = false
                }
            }
        }

    }

    /**
     * 取消连接
     */
    fun disConnect(){
        socketUtil?.isClosed?.let {isClose->
            if(!isClose){
                socketUtil?.close()
            }
        }
        // 显示连接信息
        setConnectText(ConnectStatus.DISCONNECT)
    }

    override fun onResume() {
        super.onResume()

    }

    /**
     * 连接状态枚举类
     */
    enum class ConnectStatus{
        // 正在连接，已连接，断开连接
        CONNECTING,CONNECTED,DISCONNECT
    }

    /**
     * 设置连接信息的显示
     */
    fun setConnectText(status:ConnectStatus){
        when(status){
            ConnectStatus.DISCONNECT -> {
                connectStatus.text = "未连接"
                connectStatus.setColor(
                    getResourceColor(
                        R.color.disconnect_color
                    )
                )
            }
            ConnectStatus.CONNECTING -> {
                connectStatus.text = "正在连接..."
                connectStatus.setColor(
                    getResourceColor(
                        R.color.connecting_color
                    )
                )
            }
            ConnectStatus.CONNECTED -> {
                connectStatus.text = "已连接"
                connectStatus.setColor(
                    getResourceColor(
                        R.color.connected_color
                    )
                )
            }
        }
    }

    /**
     * 显示连接失败的对话框
     */
    fun showConnectFailDialog(){
        val dialog = CommonDialog(this)
        with(dialog){
            setTitle("连接失败")
            setContent("1.请检查IP是否正确\n" +
                       "2.请确保和电脑处于同一局域网下\n" +
                       "3.确保电脑的防火墙不拦截")
            onConfirmClickListener = {
                dialog.dismiss()
            }
            onCancelClickListener = {
                dialog.dismiss()
            }
            show()
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
     * 点击传输队列按钮
     */
    fun onClickTransferQueue(v:View){

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
