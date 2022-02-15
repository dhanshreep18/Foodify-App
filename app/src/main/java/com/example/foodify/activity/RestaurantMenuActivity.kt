package com.example.foodify.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.adapter.HomeRecyclerAdapter
import com.example.foodify.adapter.RestaurantMenuRecyclerAdapter
import com.example.foodify.model.RestaurantMenu
import com.example.foodify.util.ConnectionManager
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var recyclerRestaurantMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantMenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var proceedToCartPassed: RelativeLayout
    lateinit var btnProceedToCart : Button


    lateinit var restaurantId: String
    lateinit var restaurantName: String
    val restaurantMenuList = arrayListOf<RestaurantMenu>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        recyclerRestaurantMenu=findViewById(R.id.recyclerRestaurantMenu)
        layoutManager =LinearLayoutManager(this@RestaurantMenuActivity)
        toolbar = findViewById(R.id.toolbar)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        proceedToCartPassed=findViewById(R.id.relativeLayoutProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        progressLayout.visibility= View.VISIBLE
        progressBar.visibility = View.VISIBLE

        if(intent!=null){
            setSupportActionBar(toolbar)
            supportActionBar?.title =intent.getStringExtra("name")
            restaurantId = intent.getStringExtra("id")
            restaurantName = intent.getStringExtra("name")
        }
        else{
            finish()
            Toast.makeText(this@RestaurantMenuActivity, "Intent null", Toast.LENGTH_LONG).show()
        }

        val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/+${restaurantId}"

        if(ConnectionManager().checkConnectivity(this@RestaurantMenuActivity)){
            val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                println("Response is $it")
                try{
                    progressLayout.visibility = View.GONE
                    val jobj = it.getJSONObject("data")
                    val success = jobj.getBoolean("success")

                    if(success){
                        val data = jobj.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val restaurantMenuJsonObject = data.getJSONObject(i)
                            val restaurantMenuObject = RestaurantMenu(
                                restaurantMenuJsonObject.getString("id"),
                                restaurantMenuJsonObject.getString("name"),
                                restaurantMenuJsonObject.getString("cost_for_one")
                            )
                            restaurantMenuList.add(restaurantMenuObject)
                            recyclerAdapter = RestaurantMenuRecyclerAdapter(this@RestaurantMenuActivity , restaurantId, restaurantName, restaurantMenuList, proceedToCartPassed, btnProceedToCart)
                            recyclerRestaurantMenu.adapter = recyclerAdapter
                            recyclerRestaurantMenu.layoutManager = layoutManager

                        }
                    }
                    else{
                        Toast.makeText(this@RestaurantMenuActivity, "Some Error Occurred !!",Toast.LENGTH_SHORT ).show()
                    }

                }
                catch (e: JSONException){
                    Toast.makeText(this@RestaurantMenuActivity, "Some JSON Exception Occurred !!", Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {
                println("Error is $it")
                if(this@RestaurantMenuActivity!=null){
                    Toast.makeText(this@RestaurantMenuActivity, "Volley Error Occurred !!", Toast.LENGTH_SHORT).show()
                }

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="9786fdf7780ee4"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{

            //Internet is not available
            val dialog = AlertDialog.Builder(this@RestaurantMenuActivity)
            dialog.setTitle("Error")
            dialog.setMessage(" No Internet Connection Found")
            dialog.setPositiveButton("Open Settings"){ text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@RestaurantMenuActivity.finish()
            }

            dialog.setNegativeButton("Exit"){ text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantMenuActivity)
            }
            dialog.create()
            dialog.show()
        }

    }


}