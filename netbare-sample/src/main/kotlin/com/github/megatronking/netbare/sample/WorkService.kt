package com.github.megatronking.netbare.sample

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class WorkService: IntentService("Http") {

    override fun onHandleIntent(p0: Intent?) {
        val codes = p0?.getStringArrayListExtra("list") as List<String>
        upload(codes)
    }


    fun upload(codes : List<String>) {
        for (item in codes) {

            val urlgood = "https://m.client.10010.com/DoubleCard_Pro/static/doubleCard/actFriendHelp?encryptMobile=HHHHHHH&invitationCode=WWWWWW"
                    .replace("HHHHHHH", ShareUtils.getEncryptMobile())
                    .replace("WWWWWW", item);

            Thread (object : Runnable{
                override fun run() {
                    var connection: HttpURLConnection? = null
                    var reader: BufferedReader? = null
                    var url = URL(urlgood)
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.setRequestProperty("Cookie", ShareUtils.getCookie())


                }
            })
        }

    }
}