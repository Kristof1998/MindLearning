package hu.droth.kristof.mindlearning.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

class NetworkUtil @Inject constructor(private val context: Context) {

    fun isInternetConnected(): Boolean {
        val internetConnectionType = getConnectionType(context)
        return internetConnectionType != InternetConnectionType.NONE
    }

    fun getConnectionType(context: Context): InternetConnectionType {
        var result =
            InternetConnectionType.NONE // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        result = InternetConnectionType.WIFI
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        result = InternetConnectionType.CELLULAR
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                        result = InternetConnectionType.VPN
                    }
                }
            }
        }
        return result
    }

    enum class InternetConnectionType{
        NONE,
        CELLULAR,
        WIFI,
        VPN
    }
}