package com.task.paypal_integration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

import android.widget.EditText

import com.paypal.android.sdk.payments.PayPalConfiguration
import androidx.core.app.ActivityCompat.startActivityForResult

import com.paypal.android.sdk.payments.PaymentActivity

import com.paypal.android.sdk.payments.PayPalService

import android.content.Intent

import com.paypal.android.sdk.payments.PayPalPayment
import java.math.BigDecimal
import android.app.Activity
import android.util.Log
import androidx.annotation.Nullable

import org.json.JSONException

import org.json.JSONObject

import com.paypal.android.sdk.payments.PaymentConfirmation

class MainActivity : AppCompatActivity() {
    val clientKey = "YOUR KEY CLIENT HERE"
    val PAYPAL_REQUEST_CODE = 123

    // Paypal Configuration Object
    private val config = PayPalConfiguration() // Start with mock environment.  When ready,
        // switch to sandbox (ENVIRONMENT_SANDBOX)
        // or live (ENVIRONMENT_PRODUCTION)
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // on below line we are passing a client id.
        .clientId(clientKey)
    private var amountEdt: EditText? = null
    private var paymentTV: TextView? = null
    private var paymentButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amountEdt = findViewById(R.id.idEdtAmount)
        paymentTV = findViewById(R.id.idTVStatus)
        paymentButton = findViewById(R.id.idBtnPay)
        paymentButton?.setOnClickListener {
            getPayment();
        }

    }

    private fun getPayment() {
        // Getting the amount from editText
        val amount = amountEdt!!.text.toString()

        // Creating a paypal payment on below line.

        // Creating a paypal payment on below line.
        val payment = PayPalPayment(
            BigDecimal(amount), "USD", "Course Fees",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        // Creating Paypal Payment activity intent

        // Creating Paypal Payment activity intent
        val intent = Intent(this, PaymentActivity::class.java)

        //putting the paypal configuration to the intent

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        // Putting paypal payment to the intent

        // Putting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        // Starting the intent activity for result
        // the request code will be used on the method onActivityResult

        // Starting the intent activity for result
        // the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            // If the result is OK i.e. user has not canceled the payment
            if (resultCode == RESULT_OK) {

                // Getting the payment confirmation
                val confirm: PaymentConfirmation =
                    data?.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)!!

                // if confirmation is not null
                if (confirm != null) {
                    try {
                        // Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        // on below line we are extracting json response and displaying it in a text view.
                        val payObj = JSONObject(paymentDetails)
                        val payID = payObj.getJSONObject("response").getString("id")
                        val state = payObj.getJSONObject("response").getString("state")
                        paymentTV!!.text = "Payment $state\n with payment id is $payID"
                    } catch (e: JSONException) {
                        // handling json exception on below line
                        Log.e("Error", "an extremely unlikely failure occurred: ", e)
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                // on below line we are checking the payment status.
                Log.i("paymentExample", "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // on below line when the invalid paypal config is submitted.
                Log.i(
                    "paymentExample",
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        }
    }
}