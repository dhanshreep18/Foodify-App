package com.example.foodify.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var etName : EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword : EditText
    lateinit var btnRegister: Button
    lateinit var toolbar :androidx.appcompat.widget.Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name) , Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        if(isLoggedIn){
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this@RegisterActivity)){
                val jsonParams = JSONObject()
                jsonParams.put("name", etName.text)
                jsonParams.put("mobile_number", etMobileNumber.text)
                jsonParams.put("password", etPassword.text)
                jsonParams.put("address", etAddress.text)
                jsonParams.put("email", etEmail.text)

                val queue = Volley.newRequestQueue(this@RegisterActivity)
                val url ="http://13.235.250.119/v2/register/fetch_result"

                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try{
                        println("Response of Registration is $it")

                        val rjobj = it.getJSONObject("data")
                        val success= rjobj.getBoolean("success")
                        if(success){
                            val data = rjobj.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("isLoggedIn", true).commit()
                            sharedPreferences.edit().putString("user_id", data.getString("user_id")).commit()
                            sharedPreferences.edit().putString("name", data.getString("name")).apply()
                            sharedPreferences.edit().putString("email", data.getString("email")).apply()
                            sharedPreferences.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("address", data.getString("address")).apply()

                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            val responseMessageServer = rjobj.getString("errorMessage")
                            Toast.makeText(
                                this@RegisterActivity,
                                responseMessageServer.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }catch(e: JSONException){
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
                val dialog = AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Error")
                dialog.setMessage(" No Internet Connection Found")
                dialog.setPositiveButton("Open Settings"){ text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)  //provides intent with the path that opens the settings of the phone
                    startActivity(settingsIntent)
                    finish()      //recreates the fragment and refreshes the list after connecting to net and coming back to app
                }

                dialog.setNegativeButton("Exit"){ text, listener ->
                    ActivityCompat.finishAffinity(this@RegisterActivity)    //closes the app completely
                }
                dialog.create()
                dialog.show()
            }
        }

    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}