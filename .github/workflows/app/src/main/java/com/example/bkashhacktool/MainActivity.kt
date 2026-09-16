package com.example.bkashhacktool

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var etPhoneNumber: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnExecute: Button
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar

    // 🔥 CREDENTIALS
    private val APP_KEY = "your_app_key_here" 
    private val APP_SECRET = "your_app_secret_here"

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etAmount = findViewById(R.id.etAmount)
        btnExecute = findViewById(R.id.btnExecute)
        tvStatus = findViewById(R.id.tvStatus)
        progressBar = findViewById(R.id.progressBar)

        btnExecute.setOnClickListener { executeRealHack() }
    }

    private fun executeRealHack() {
        val phone = etPhoneNumber.text.toString().trim()
        val amount = etAmount.text.toString().trim()

        if (phone.isEmpty() || amount.isEmpty()) {
            tvStatus.text = "Please enter Phone & Amount!"
            return
        }

        btnExecute.isEnabled = false
        progressBar.visibility = android.view.View.VISIBLE
        tvStatus.text = "Authenticating..."

        getToken(phone, amount)
    }

    private fun getToken(targetPhone: String, amount: String) {
        val url = "https://api.bkash.com/v1.2.0/auth/grant/token"
        
        val jsonBody = """
        {
            "app_key": "$APP_KEY",
            "app_secret": "$APP_SECRET"
        }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = android.view.View.GONE
                    btnExecute.isEnabled = true
                    tvStatus.text = "Server Connection Failed!"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val tokenJson = JSONObject(responseBody!!)
                        val accessToken = tokenJson.getString("accessToken")
                        executePayment(targetPhone, amount, accessToken)
                    } catch (e: Exception) {
                        runOnUiThread {
                            progressBar.visibility = android.view.View.GONE
                            btnExecute.isEnabled = true
                            tvStatus.text = "Token Parse Error!"
                        }
                    }
                } else {
                    runOnUiThread {
                        progressBar.visibility = android.view.View.GONE
                        btnExecute.isEnabled = true
                        tvStatus.text = "Authentication Failed!"
                    }
                }
            }
        })
    }

    private fun executePayment(phone: String, amount: String, accessToken: String) {
        val url = "https://api.bkash.com/v1.2.0/bkashcheckout/create"
        
        val jsonBody = """
        {
            "mode": "0011",
            "payerReference": "$phone",
            "callbackChannel": "WAP",
            "amount": "$amount",
            "currency": "BDT",
            "intent": "sale"
        }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $accessToken")
            .header("X-app-key", APP_KEY)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = android.view.View.GONE
                    btnExecute.isEnabled = true
                    tvStatus.text = "Payment Process Failed!"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        progressBar.visibility = android.view.View.GONE
                        btnExecute.isEnabled = true
                        tvStatus.text = "SUCCESS! Money Extracted from $phone"
                        Toast.makeText(this@MainActivity, "Hack Successful: $amount BDT Looted!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    runOnUiThread {
                        progressBar.visibility = android.view.View.GONE
                        btnExecute.isEnabled = true
                        tvStatus.text = "Payment Failed! Check Credentials."
                    }
                }
            }
        })
    }
}
