package com.dilekbaykara.tasky.features.shared.data.network
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class NetworkConnectivityManager @Inject constructor(
    private val context: Context
) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: Flow<Boolean> = _isConnected.asStateFlow()
    init {
        updateNetworkState()
    }
    fun isOnline(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            val isConnected = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            _isConnected.value = isConnected
            isConnected
        } catch (e: SecurityException) {
            Log.d("NetworkConnectivityManager", "DEBUG: Network permission denied: ${e.message}")
            _isConnected.value = false
            false
        } catch (e: Exception) {
            Log.d("NetworkConnectivityManager", "DEBUG: Error checking network state: ${e.message}")
            _isConnected.value = false
            false
        }
    }
    private fun updateNetworkState() {
        isOnline()
    }
    fun requiresNetwork(): Boolean {
        return !isOnline()
    }
}
