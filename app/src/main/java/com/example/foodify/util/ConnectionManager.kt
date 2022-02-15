package com.example.foodify.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {

    fun checkConnectivity(context: Context):Boolean{    //connectivityManager provides info about device's connection at some context

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        //used to check whether device is connected to internet or not
        if(activeNetwork?.isConnected != null){
            return activeNetwork.isConnected
        } else{
            return false
        }
    }

}