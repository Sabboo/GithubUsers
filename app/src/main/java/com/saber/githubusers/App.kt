package com.saber.githubusers

import android.app.Application
import com.saber.githubusers.utils.NetworkMonitoringUtil
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkMonitoringUtil(this).apply {
            checkNetworkState()
            registerNetworkCallbackEvents()
        }
    }

}