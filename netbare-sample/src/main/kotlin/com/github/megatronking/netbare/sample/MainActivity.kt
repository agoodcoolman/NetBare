package com.github.megatronking.netbare.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareConfig
import com.github.megatronking.netbare.NetBareListener
import com.github.megatronking.netbare.http.HttpInjectInterceptor
import com.github.megatronking.netbare.http.HttpInterceptorFactory
import com.github.megatronking.netbare.ssl.JKS
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

class MainActivity : AppCompatActivity(), NetBareListener {

    companion object {
        private const val REQUEST_CODE_PREPARE = 1
    }

    private lateinit var mNetBare : NetBare

    private lateinit var mActionButton : Button
    private lateinit var shuacode : Button

    private lateinit var codes: AppCompatEditText

    private lateinit var logs: AppCompatTextView
    private var terminalString: StringBuilder = StringBuilder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        mNetBare = NetBare.get()

        mActionButton = findViewById(R.id.action)
        shuacode = findViewById(R.id.shuacode)
        codes = findViewById(R.id.codes)
        logs = findViewById(R.id.logs)

        controllerButtonVisiable()
        mActionButton.setOnClickListener {
            if (mNetBare.isActive) {
                mNetBare.stop()
            } else{
                prepareNetBare()
            }
            if (TextUtils.isEmpty(ShareUtils.getCookie())) {
                Toast.makeText(application, "姐妹服务开启了，请去联通手动填入别人的邀请码，然后请求一下，然后我好获取你的参数", Toast.LENGTH_LONG).show()
            }
        }
        shuacode.setOnClickListener {
            val codes = codes.text.toString().trim()
            if (TextUtils.isEmpty(codes)) {
                Toast.makeText(application, "请填入邀请码", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(ShareUtils.getCookie())) {
                Toast.makeText(application, "姐妹请点击开启服务，请去联通APP手动填入别人的邀请码，然后请求一下，然后我好获取你的参数", Toast.LENGTH_LONG).show()
            } else {
                // 已经获取参数的话，就不要再去开启拦截了。直接关掉。
               if (mNetBare.isActive) {
                    mNetBare.stop()
                }

                val codeLists = codes.split("\n")
                if (codeLists.size == 0) {
                    Toast.makeText(application, "请填入邀请码", Toast.LENGTH_LONG).show()
                }
                if (codeLists.size> 0) {
                    // 这里已经有码，并且已经获取了用户的数据了
                    val intent = Intent()

                    intent.putStringArrayListExtra("list", ArrayList(codeLists))
                    intent.setClass(applicationContext, WorkService::class.java)
                    startService(intent)
                }
            }
        }

        // 监听NetBare服务的启动和停止
        mNetBare.registerNetBareListener(this)
    }
    // 写log
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showTerminal(content: String) {
        terminalString.append(content)
        logs.text = terminalString.toString()
    }

    // 控制按钮是否显示
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun show(clazz: Class<UnionInjector>) {
        controllerButtonVisiable()
    }

    fun controllerButtonVisiable() {
        if (TextUtils.isEmpty(ShareUtils.getCookie())) {
            mActionButton.visibility = View.VISIBLE
            shuacode.visibility = View.INVISIBLE
        } else {
            mActionButton.visibility = View.INVISIBLE
            shuacode.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mNetBare.unregisterNetBareListener(this)
        mNetBare.stop()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()

    }
    override fun onServiceStarted() {
        runOnUiThread {
            mActionButton.setText(R.string.stop_netbare)
        }
    }

    override fun onServiceStopped() {
        runOnUiThread {
            mActionButton.setText(R.string.start_netbare)
        }
    }

    private fun prepareNetBare() {
        // 安装自签证书
        if (!JKS.isInstalled(this, App.JSK_ALIAS)) {
            try {
                JKS.install(this, App.JSK_ALIAS, App.JSK_ALIAS)
            } catch(e : IOException) {
                // 安装失败
            }
            return
        }
        // 配置VPN
        val intent = NetBare.get().prepare()
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_PREPARE)
            return
        }
        // 启动NetBare服务
        mNetBare.start(NetBareConfig.defaultHttpConfig(App.getInstance().getJSK(),
                interceptorFactories()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PREPARE) {
            prepareNetBare()
        }
    }

    private fun interceptorFactories() : List<HttpInterceptorFactory> {
        // 拦截器范例1：打印请求url
//        val interceptor1 = HttpUrlPrintInterceptor.createFactory()
//        // 注入器范例1：替换百度首页logo
//        val injector1 = HttpInjectInterceptor.createFactory(BaiduLogoInjector())
//        // 注入器范例2：修改发朋友圈定位
//        val injector2 = HttpInjectInterceptor.createFactory(WechatLocationInjector())
        // 可以添加其它的拦截器，注入器
        val injector3 = HttpInjectInterceptor.createFactory(UnionInjector())
        // ...
//        return listOf(interceptor1, injector1, injector2, injector3)
        return listOf(injector3)
    }


}
