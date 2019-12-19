package com.github.megatronking.netbare.sample

import android.content.Context

class ShareUtils {
    companion object {
        const val Cookie = "cookie"
        const val Mobile = "mobile"
        fun getCookie():String? {
            return App.getInstance().getSharedPreferences(Cookie, Context.MODE_PRIVATE).getString(Cookie, "")
        }

        fun setCookie(cookie: String): Boolean {
             return App.getInstance()
                     .getSharedPreferences(Cookie, Context.MODE_PRIVATE)
                     .edit()
                     .putString(Cookie, cookie)
                     .commit();

        }

        fun setEncryptMobile(encryptMobile : String) : Boolean {
            return App.getInstance()
                    .getSharedPreferences(Mobile, Context.MODE_PRIVATE)
                    .edit()
                    .putString(Mobile, encryptMobile)
                    .commit();
        }

        fun getEncryptMobile() : String {
            return App.getInstance().getSharedPreferences(Mobile, Context.MODE_PRIVATE).getString(Mobile, "")
        }
    }
}