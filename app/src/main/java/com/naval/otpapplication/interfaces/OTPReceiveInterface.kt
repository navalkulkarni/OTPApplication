package com.naval.otpapplication.interfaces

interface OTPReceiveInterface {
    fun onOtpReceived(otp : String)
    fun onOtpTimeout()
}