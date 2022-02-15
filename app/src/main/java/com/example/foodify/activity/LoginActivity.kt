package com.example.foodify.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword : TextView
    lateinit var txtRegister: TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name) , Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        setContentView(R.layout.activity_login)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword= findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtSignUp)



        if(isLoggedIn){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this@LoginActivity)) {

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text)
                jsonParams.put("password", etPassword.text)

                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"

                val jsonRequest = object :
                    JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try {
                            println("Response is $it")

                            val rjobj = it.getJSONObject("data")
                            val success = rjobj.getBoolean("success")

                            if (success) {
                                val data = rjobj.getJSONObject("data")
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).commit()
                                sharedPreferences.edit().putString("user_id", data.getString("user_id")).commit()
                                sharedPreferences.edit().putString("name", data.getString("name")).apply()
                                sharedPreferences.edit().putString("email", data.getString("email")).apply()
                                sharedPreferences.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("address", data.getString("address")).apply()



                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                btnLogin.visibility = View.VISIBLE
                                txtForgotPassword.visibility = View.VISIBLE
                                btnLogin.visibility = View.VISIBLE
                                val responseMessageServer = rjobj.getString("errorMessage")
                                Toast.makeText(
                                    this@LoginActivity,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e:JSONException) {
                            btnLogin.visibility = View.VISIBLE
                            txtForgotPassword.visibility = View.VISIBLE
                            txtRegister.visibility = View.VISIBLE
                            e.printStackTrace()
                        }

                    }, Response.ErrorListener {

                        btnLogin.visibility=View.VISIBLE
                        txtForgotPassword.visibility=View.VISIBLE
                        txtRegister.visibility=View.VISIBLE
                        Log.e("Error::::", "/post request fail ! Error: ${it.message}")

                    }) {
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage(" No Internet Connection Found")
                dialog.setPositiveButton("Open Settings"){ text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)  //provides intent with the path that opens the settings of the phone
                    startActivity(settingsIntent)
                    finish()      //recreates the fragment and refreshes the list after connecting to net and coming back to app
                }

                dialog.setNegativeButton("Exit"){ text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity)    //closes the app completely
                }
                dialog.create()
                dialog.show()
            }

        }

        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordStepOneActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}


