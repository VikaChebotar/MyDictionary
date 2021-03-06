package com.mydictionary.data.wordinforepo.datasource.remote

import android.content.Context
import android.net.ConnectivityManager
import com.mydictionary.data.R
import com.mydictionary.data.wordinforepo.isOnline
import okhttp3.Interceptor


import com.mydictionary.data.wordinforepo.OXFORD_API_APP_ID_HEADER
import com.mydictionary.data.wordinforepo.OXFORD_API_APP_KEY_HEADER
import com.mydictionary.domain.NoConnectivityException
import okhttp3.Response

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */
class HeaderInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        val request = chain!!.request().newBuilder().addHeader(
            OXFORD_API_APP_KEY_HEADER,
            context.getString(R.string.oxford_app_key)
        ).addHeader(
            OXFORD_API_APP_ID_HEADER,
            context.getString(R.string.oxford_app_id)
        ).build()
        return chain.proceed(request)
    }
}

class ConnectivityInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        if (!(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).isOnline()) {
            throw NoConnectivityException()
        }

        val builder = chain!!.request().newBuilder()
        return chain.proceed(builder.build())
    }
}

