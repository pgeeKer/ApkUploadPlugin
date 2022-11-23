package com.apk.upload.plugin.helper

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author panxiangxing
 */
class HttpHelper {

    companion object {

        fun getOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }
    }
}