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

class ForgotPasswordStepOneActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_step_one)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this@ForgotPasswordStepOneActivity)){

                val jsonParams= JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text)
                jsonParams.put("email", etEmail.text)

                val queue = Volley.newRequestQueue(this@ForgotPasswordStepOneActivity)
                val url ="http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonRequest = object: JsonObjectRequest(Request.Method.POST , url, jsonParams, Response.Listener {

                    try{
                        println("Response is $it")

                        val rjobj = it.getJSONObject("data")
                        val success= rjobj.getBoolean("success")


                        if(success){

                            val firstTry = rjobj.getBoolean("first_try")

                            if(firstTry==true){

                                Toast.makeText(this@ForgotPasswordStepOneActivity,"OTP is sent to registered Email ID", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@ForgotPasswordStepOneActivity, ForgotPasswordStepTwoActivity::class.java)
                                intent.putExtra("mobile_number", etMobileNumber.text.toString())
                                startActivity(intent)
                                finish()

                            }else{

                                Toast.makeText(this@ForgotPasswordStepOneActivity,"OTP is already sent to registered Email ID", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@ForgotPasswordStepOneActivity, ForgotPasswordStepTwoActivity::class.java)
                                val bundle = Bundle()
                                intent.putExtra("mobile_number", etMobileNumber.text.toString())
                                startActivity(intent)
                                finish()

                            }

                        }
                        else{
                            val responseMessageServer = rjobj.getString("errorMessage")
                            Toast.makeText(
                                this@ForgotPasswordStepOneActivity,
                                responseMessageServer.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }catch (e:JSONException){
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
                val dialog = AlertDialog.Builder(this@ForgotPasswordStepOneActivity)
                dialog.setTitle("Error")
                dialog.setMessage(" No Internet Connection Found")
                dialog.setPositiveButton("Open Settings"){ text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)  //provides intent with the path that opens the settings of the phone
                    startActivity(settingsIntent)
                    finish()      //recreates the fragment and refreshes the list after connecting to net and coming back to app
                }

                dialog.setNegativeButton("Exit"){ text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPasswordStepOneActivity)    //closes the app completely
                }
                dialog.create()
                dialog.show()
            }

        }
    }
}