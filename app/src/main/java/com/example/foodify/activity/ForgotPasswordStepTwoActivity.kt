package com.example.foodify.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordStepTwoActivity() : AppCompatActivity() {

    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var btnSubmit: Button

    var mobileNumber: String?="No mobile number"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_step_two)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        if(intent!=null){
            mobileNumber = intent.getStringExtra("mobile_number")
        }

        btnSubmit.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this@ForgotPasswordStepTwoActivity)){

                val jsonParams= JSONObject()

                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", etNewPassword.text)
                jsonParams.put("otp", etOTP.text)

                val queue = Volley.newRequestQueue(this@ForgotPasswordStepTwoActivity)
                val url ="http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonRequest= object : JsonObjectRequest(Request.Method.POST, url, jsonParams,Response.Listener {

                    try{

                        println("Response is $it")
                        val rjobj = it.getJSONObject("data")
                        val success= rjobj.getBoolean("success")
                        if (success){

                            val responseMessageServer = rjobj.getString("successMessage")
                            Toast.makeText(
                                this@ForgotPasswordStepTwoActivity,
                                responseMessageServer.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent= Intent(this@ForgotPasswordStepTwoActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                        else{
                            val responseMessageServer = rjobj.getString("errorMessage")
                            Toast.makeText(
                                this@ForgotPasswordStepTwoActivity,
                                responseMessageServer.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }catch (e: JSONException){
                        e.printStackTrace()
                    }

                },Response.ErrorListener {

                    Log.e("Error::::","/post request fail ! Error: ${it.message}")

                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9786fdf7780ee4"
                        return headers
                    }
                }
                queue.add(jsonRequest)

            }
            else{
                //Internet is not available
                val dialog = AlertDialog.Builder(this@ForgotPasswordStepTwoActivity)
                dialog.setTitle("Error")
                dialog.setMessage(" No Internet Connection Found")
                dialog.setPositiveButton("Open Settings"){ text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)  //provides intent with the path that opens the settings of the phone
                    startActivity(settingsIntent)
                    finish()      //recreates the fragment and refreshes the list after connecting to net and coming back to app
                }

                dialog.setNegativeButton("Exit"){ text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordStepTwoActivity)    //closes the app completely
                }
                dialog.create()
                dialog.show()
            }

        }
    }
}