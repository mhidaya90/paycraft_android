package com.paycraft.base

import android.app.Application
import com.paycraft.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaycraftApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SpManager.instance().init(this, getString(R.string.app_name))
    }
}