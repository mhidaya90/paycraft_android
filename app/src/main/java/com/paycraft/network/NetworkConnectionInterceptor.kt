package com.paycraft.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.paycraft.BuildConfig
import com.paycraft.base.SessionManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException


class NetworkConnectionInterceptor(private val mContext: Context, val tag: String = "") :
    Interceptor {

    private val isConnected: Boolean
        get() {
            val connectivityManager =
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) {
            throw NoConnectivityException()
        }
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("deviceToken", SessionManager.instance().getFcmToken())
            .addHeader("deviceType", "android")
            .addHeader("appVersionCode", BuildConfig.VERSION_CODE.toString())
            .addHeader("Authorization", SessionManager.instance().getHeader())
            .build()

        // Log the cURL command for the request
        Log.d(tag, getCurlCommand(request))
        val response: Response = chain.proceed(request)
        // Proceed with the request and log the response
        Log.d(tag, response.toString())
        return response
    }


    @Throws(IOException::class)
    private fun getCurlCommand(request: Request): String {
        // Build the cURL command string for the request
        val curlCommand = StringBuilder("curl -X ")
            .append(request.method)
            .append(" ")

        // Append headers
        for (header in request.headers.names()) {
            curlCommand.append(" -H \"").append(header).append(": ").append(request.headers[header])
                .append("\"")
        }

        // Append request body if present
        val copy = request.newBuilder().build()
        if (copy.body != null) {
            val buffer = Buffer()
            copy.body!!.writeTo(buffer)
            curlCommand.append(" --data-raw '").append(buffer.readUtf8()).append("'")
        }

        // Append URL
        curlCommand.append(" \"").append(request.url).append("\"")
        return curlCommand.toString()
    }
}