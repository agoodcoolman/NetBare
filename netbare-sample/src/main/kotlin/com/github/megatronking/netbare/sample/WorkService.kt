package com.github.megatronking.netbare.sample

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.BufferedReader
import java.io.InputStreamReader
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

                    connection.setRequestProperty("X-Requested-With", "XMLHttpRequest")
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.4; SM-A7009 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36; unicom{version:android@7.0100,desmobile:15580399096};devicetype{deviceBrand:samsung,deviceModel:SM-A7009};{yw_code:}")
                    connection.setRequestProperty("Referer", "https://m.client.10010.com/DoubleCard_Pro/static/doubleCard/activityIndexDu?encryptMobile=MMMM&invitationCode=".replace("MMMM", ShareUtils.getEncryptMobile()))
                    connection.setRequestProperty("Accept-Encoding", "gzip,deflate")
                    connection.setRequestProperty("Accept-Language", "zh-CN,en-US;q=0.8")
                    connection.setRequestProperty("Cookie", ShareUtils.getCookie())
                    connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01")

                    var inStream = connection.inputStream
                    reader = BufferedReader(InputStreamReader(inStream))
                    var response = StringBuilder()
                    var allText = reader.use(BufferedReader::readText)
                    response.append(allText)
                    EventBus.getDefault().post(response.toString())
                }
            }).start()
        }

    }
}