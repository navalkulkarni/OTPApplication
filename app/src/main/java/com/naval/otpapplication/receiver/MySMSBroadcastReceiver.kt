package com.naval.otpapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.naval.otpapplication.interfaces.OTPReceiveInterface
import java.util.regex.Pattern


class MySMSBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiveInterface: OTPReceiveInterface? = null

    fun setOnOtpListeners(otpReceiveInterface: OTPReceiveInterface){
        this.otpReceiveInterface = otpReceiveInterface
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    var otp2 = otp
                    if (otpReceiveInterface != null) {
                        otp = otp.split(Regex(" ")).get(4).toString()
                        Log.d("OTP",otp)
                        otp = "Your OTP Code is $otp"
                        //otp = otp.replace("<#> Your otp code is : ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        //otp = otp.replace("<#> Your otp code is: ", "")
                        otpReceiveInterface!!.onOtpReceived(otp)
                    }
                }

                CommonStatusCodes.TIMEOUT ->
                    if (otpReceiveInterface != null) {
                    otpReceiveInterface!!.onOtpTimeout()}
            }
        }

    }
}