package com.github.megatronking.netbare.sample

import android.util.Log
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.http.HttpBody
import com.github.megatronking.netbare.http.HttpRequest
import com.github.megatronking.netbare.http.HttpRequestHeaderPart
import com.github.megatronking.netbare.http.HttpResponseHeaderPart
import com.github.megatronking.netbare.injector.InjectorCallback
import com.github.megatronking.netbare.injector.SimpleHttpInjector
import com.github.megatronking.netbare.io.HttpBodyInputStream
import com.github.megatronking.netbare.stream.ByteStream
import com.google.gson.JsonParser
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.nio.ByteBuffer
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

class UnionInjector:SimpleHttpInjector( ){

    companion object {
        const val TAG = "UnionInjector"
    }

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
        }
        return shouldInject
    }

    override fun onRequestInject(request: HttpRequest, body: HttpBody, callback: InjectorCallback) {
        //        // 获取手机加密内容
        val params = String(body.toBuffer().array())
//      encryptMobile=7f1efd01973e0200752ea77a3e0715a8&invitationCode=4xa7Fd84t

        val replace = params.replace(params.split("=")[2], "5lGgy9Z6R")
        val wrap = ByteBuffer.wrap(replace.toByteArray())

        super.onRequestInject(request, body, callback)
    }

    override fun onRequestInject(header: HttpRequestHeaderPart, callback: InjectorCallback) {
        val query = header.uri().query

//        // 获取手机加密内容
//        val encryptMobileParam = header.uri().getQueryParameter("encryptMobile")
//        // 获取邀请码，然后用自己的反射注入进去
//        val code = header.uri().getQueryParameter("invitationCode")
//
//        ShareUtils.setEncryptMobile(encryptMobileParam)
//        header.uri().buildUpon()
//        header.uri().path.replace(code, "5lGgy9Z6R")
        callback.onFinished(header)
    }

}