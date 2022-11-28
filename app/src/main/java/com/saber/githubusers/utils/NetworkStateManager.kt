package com.saber.githubusers.utils

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


object NetworkStateManager {

    private val activeNetworkStatusMLD = MutableLiveData<Boolean>()

    /**
     * Updates the active network status live-data
     */
    fun setNetworkConnectivityStatus(connectivityStatus: Boolean) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            activeNetworkStatusMLD.setValue(connectivityStatus)
        } else {
            activeNetworkStatusMLD.postValue(connectivityStatus)
        }
    }

    /**
     * Returns the current network status
     */
    fun getNetworkConnectivityStatus(): LiveData<Boolean?> {
        return activeNetworkStatusMLD
    }
}