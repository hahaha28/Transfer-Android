package `fun`.inaction.transfer

import `fun`.inaction.transfer.databinding.ActivityAboutSAFBinding
import `fun`.inaction.transfer.utils.LogUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_about_s_a_f.*

class AboutSAFActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutSAFBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutSAFBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置标题
        supportActionBar?.title = "如何使用SAF"

        val url = "http://io.inaction.fun/static/about_saf.html"
//        val url = "https://baidu.com"



        webview.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                showProgress(false)
            }
        }
        showProgress(true)
//        webview.clearCache(true)
        webview.loadUrl(url)
    }
    
    private fun showProgress(boolean: Boolean){
        if(boolean) {
            webview.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        }else{
            webview.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }
    
}
