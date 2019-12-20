package com.github.megatronking.netbare.sample

import android.util.Log
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.http.*
import com.github.megatronking.netbare.injector.InjectorCallback
import com.github.megatronking.netbare.injector.SimpleHttpInjector
import com.github.megatronking.netbare.io.HttpBodyInputStream
import com.github.megatronking.netbare.stream.ByteStream
import com.google.gson.JsonParser
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.nio.ByteBuffer
import java.util.regex.Pattern
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

class UnionInjector:SimpleHttpInjector( ){

    companion object {
        const val TAG = "UnionInjector"
    }
    private var mHoldResponseHeader: HttpResponseHeaderPart? = null

    override fun sniffRequest(request: HttpRequest): Boolean {
        // 进去到签到页面的
//        "http://m.client.10010.com/mobileService/customer/query/getMyUnicomPrivil.htm?phone=15580399096"
//        if (request.url().startsWith("http://m.client.10010.com/mobileService/customer/query/getMyUnicomPrivil.htm")) {
//            val requestHeader = request.requestHeader("Cookies")
//            ShareUtils.setCookie(requestHeader[0])
//            Log.i(WechatLocationInjector.TAG, "获取cookies");
//        }

        val shouldInject = request.url().startsWith("https://m.client.10010.com/DoubleCard_Pro/static/doubleCard/actFriendHelp")
        // 自己的code 5lGgy9Z6R
        if (shouldInject) {
            // 还是要手动去获取手机号码的加密
            val requestHeader = request.requestHeader("Cookie")
            ShareUtils.setCookie(requestHeader[0])
            Log.i(WechatLocationInjector.TAG, "获取cookies");
            EventBus.getDefault().post("参数获取成功")
        }
        return shouldInject
    }

    override fun onRequestInject(request: HttpRequest, body: HttpBody, callback: InjectorCallback) {
        //        // 获取手机加密内容
        EventBus.getDefault().post(UnionInjector::class.java)
        val params = String(body.toBuffer().array())
        val matcher = Pattern.compile("(?<=encryptMobile=)(.+?)(?=&)").matcher(params)
        if (matcher.find()) ShareUtils.setEncryptMobile(matcher.group(1))

//      encryptMobile=7f1efd01973e0200752ea77a3e0715a8&invitationCode=4xa7Fd84t
        // 替换然后返回
        val replace = params.replace(params.split("=")[2], "5lGgy9Z6R")
        // 变成二进制
        val wrap = ByteBuffer.wrap(replace.toByteArray())
        // 用父类的调用下回调
        super.onRequestInject(request, HttpRawBody(wrap), callback)
    }

    override fun onRequestInject(header: HttpRequestHeaderPart, callback: InjectorCallback) {
        // 不管输入是多少，要把请求头的内容的长度写成71，这是这个请求的。
        val injectHeader = header!!
                .newBuilder()
                .replaceHeader("Content-Length", "71")
                .build()

        callback.onFinished(injectHeader)
    }

}