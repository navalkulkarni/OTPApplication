package com.naval.otpapplication


import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.app.PendingIntent
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.credentials.Credential.EXTRA_KEY
import android.R.attr.data
import android.app.Activity
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.naval.otpapplication.interfaces.OTPReceiveInterface
import com.naval.otpapplication.receiver.MySMSBroadcastReceiver

/**
 * ref: https://developers.google.com/identity/sms-retriever/verify
 * */
class MainActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OTPReceiveInterface {


    private var mGoogleApiClient: GoogleApiClient? = null
    private val mySMSBroadcastReceiver = MySMSBroadcastReceiver()

    private val RESOLVE_HINT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set google api client for hint request
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .enableAutoManage(this, this)
            .addApi(Auth.CREDENTIALS_API)
            .build()

        //set otp callback from broadcast receiver
        mySMSBroadcastReceiver.setOnOtpListeners(this)

        //Registering Receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        applicationContext.registerReceiver(mySMSBroadcastReceiver, intentFilter)

        listeners()
    }

    private fun listeners(){
        btSmsRetrieverApi.setOnClickListener(this)
    }



    override fun onClick(v: View?) {
        when(v){
            btSmsRetrieverApi -> {
                startSMSListener()
            }
        }
    }

    //get available number in user phone
    private fun getHintPhoneNumber(){
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent = Auth.CredentialsApi.getHintPickerIntent(
            mGoogleApiClient, hintRequest
        )
        startIntentSenderForResult(intent.intentSender, RESOLVE_HINT, null, 0, 0, 0)
    }


    //check sms retrieval client
    private fun startSMSListener() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            tvOtp.text = "Waiting for the OTP"
            Toast.makeText(this, "SMS Retriever starts", Toast.LENGTH_LONG).show()
        }

        task.addOnFailureListener {
            tvOtp.text = "Cannot Start SMS Retriever"
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }


    override fun onOtpReceived(otp: String) {
        tvOtp.text = "$otp"
    }

    override fun onOtpTimeout() {
        tvOtp.text = "Time out, please resend"
    }


    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {

                var credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)
                // credential.getId();  <-- will need to process phone number string
            }
        }

    }

}
