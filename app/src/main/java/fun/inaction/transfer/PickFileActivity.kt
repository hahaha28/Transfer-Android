package `fun`.inaction.transfer

import `fun`.inaction.transfer.adapters.GridSpacingItemDecoration
import `fun`.inaction.transfer.adapters.PickFileRVAdapter
import `fun`.inaction.transfer.bean.PickFileItem
import `fun`.inaction.transfer.events.NewFilesEvent
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_pick_file.*
import org.greenrobot.eventbus.EventBus

class PickFileActivity : AppCompatActivity() {

    private val PICK_FROM_SAF:Int = 0

    private val itemList:MutableList<PickFileItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_file)

        // 设置标题
        supportActionBar?.title = "选择文件"
        // 初始化 itemList
        initItemList()
        // 设置RecyclerView
        val adapter = PickFileRVAdapter(itemList)
        val layoutManager = GridLayoutManager(this,3)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3,50,true))
    }

    /**
     * 初始化RecyclerView的数据
     */
    private fun initItemList(){
        itemList.add(PickFileItem(R.drawable.ic_mobile,"内部存储",onAllFileItemClickListener))
        itemList.add(PickFileItem(R.drawable.ic_image,"图片",onImageItemClickListener))
        itemList.add(PickFileItem(R.drawable.ic_audio,"音乐",onAudioItemClickListener))
        itemList.add(PickFileItem(R.drawable.ic__video,"视频",onVideoItemClickListener))
    }

    val onAllFileItemClickListener = {
        openSAF()
    }

    val onImageItemClickListener = {
        openSAF("image/*")
    }

    val onVideoItemClickListener = {
        openSAF("video/*")
    }

    val onAudioItemClickListener = {
        openSAF("audio/*")
    }

    /**
     * 打开SAF
     * @param type 筛选文件类型
     */
    private fun openSAF(type:String = "*/*"){
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            this.type = type

            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            startActivityForResult(this,PICK_FROM_SAF)
        }
    }

    /**
     * 点击SAF帮助按钮
     */
    fun onClickSAFHelp(v:View){
        val intent = Intent(this,AboutSAFActivity::class.java)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        when(requestCode){
            PICK_FROM_SAF -> {
                data?.clipData?.let { clip ->
                    val uriList = mutableListOf<Uri>()
                    for(i in 0 until clip.itemCount){
                        uriList.add(clip.getItemAt(i).uri)
                    }
                    postNewFiles(uriList)
                }
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
