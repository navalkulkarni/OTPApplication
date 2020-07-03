package com.naval.otpapplication.global

import android.app.Application
import com.naval.otpapplication.helper.AppSignatureHelper


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        /*Following will generate the hash code*/
        var appSignature = AppSignatureHelper(this)
        appSignature.getAppSignatures()
    }
}