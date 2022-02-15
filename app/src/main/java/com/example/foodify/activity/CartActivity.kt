package com.example.foodify.activity

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.adapter.CartRecyclerAdapter
import com.example.foodify.model.CartItems
import com.example.foodify.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerCart : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var txtViewOrder: TextView
    lateinit var btnPlaceOrder: Button
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var restaurantName: String
    lateinit var restaurantId: String
    lateinit var selectedItemsId: ArrayList<String>
    var totalAmount = 0
    var cartListItems= arrayListOf<CartItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbar)
        layoutManager = LinearLayoutManager(this@CartActivity)
        txtViewOrder = findViewById(R.id.txtViewOrder)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        recyclerCart = findViewById(R.id.recyclerCart)

        restaurantId = intent.getStringExtra("restaurantId")
        restaurantName= intent.getStringExtra("restaurantName")
        selectedItemsId = intent.getStringArrayListExtra("selectedItemsId")

        setToolBar()

        txtViewOrder.text = "Ordering From : $restaurantName"

        fetchData()

        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        btnPlaceOrder.setOnClickListener (View.OnClickListener {
            val sharedPreferences= this.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

            if(ConnectionManager().checkConnectivity(this@CartActivity)){

                try {

                    progressLayout.visibility= View.VISIBLE

                    val foodJsonArray = JSONArray()
                    for (foodItem in selectedItemsId) {
                        val singleFoodItemObject = JSONObject()
                        singleFoodItemObject.put("food_item_id", foodItem)
                        foodJsonArray.put(singleFoodItemObject)
                    }

                    val jsonParams = JSONObject()
                    jsonParams.put("user_id", sharedPreferences.getString("user_id", "0"))
                    jsonParams.put("restaurant_id", restaurantId)
                    jsonParams.put("total_cost", totalAmount)
                    jsonParams.put("food", foodJsonArray)

                    val jsonObjectRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                            println("Response of post from cart is $it")

                            val rjobj = it.getJSONObject("data")
                            val success = rjobj.getBoolean("success")
                            if(success){
                                Toast.makeText(this@CartActivity,"Order Placed", Toast.LENGTH_SHORT).show()
                                createNotification()

                                val intent= Intent(this@CartActivity, OrderPlaceActivity::class.java)
                                startActivity(intent)
                                finishAffinity()

                            }else{
                                val responseMessageServer = rjobj.getString("errorMessage")
                                Toast.makeText(
                                    this@CartActivity,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            progressLayout.visibility = View.INVISIBLE
                        }, Response.ErrorListener {

                             println("Error of post from cart $it")

                             Toast.makeText(this@CartActivity,"Some Error Occurred !!",Toast.LENGTH_SHORT).show()
                             Log.e("Error::::","/post request fail ! Error: ${it.message}")
                        }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="9786fdf7780ee4"
                            return headers
                        }
                    }
                    jsonObjectRequest.setRetryPolicy(DefaultRetryPolicy(15000,1,1f))
                    queue.add(jsonObjectRequest)
                }
                catch (e:JSONException){
                    Toast.makeText(this@CartActivity, "Some JSON Exception Occurred !!", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                //Internet is not available
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage(" No Internet Connection Found")
                dialog.setPositiveButton("Open Settings"){ text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@CartActivity.finish()
                }

                dialog.setNegativeButton("Exit"){ text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }

        })

    }

    fun fetchData(){
        if(ConnectionManager().checkConnectivity(this@CartActivity)){

            progressLayout.visibility= View.VISIBLE


            try{

                val queue = Volley.newRequestQueue(this@CartActivity)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/+${restaurantId}"

                val jsonObjectRequest= object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    println("response get $it")
                    val rjobj = it.getJSONObject("data")
                    val success = rjobj.getBoolean("success")

                    if(success){
                        val data= rjobj.getJSONArray("data")

                        cartListItems.clear()
                        totalAmount=0

                        for(i in 0 until data.length()){
                            val cartItemJsonObject = data.getJSONObject(i)
                            if(selectedItemsId.contains(cartItemJsonObject.getString("id"))){
                                val menuObject = CartItems(
                                    cartItemJsonObject.getString("id"),
                                    cartItemJsonObject.getString("name"),
                                    cartItemJsonObject.getString("cost_for_one"),
                                    cartItemJsonObject.getString("restaurant_id")
                                )
                                totalAmount += cartItemJsonObject.getString("cost_for_one").toInt()
                                cartListItems.add(menuObject)
                            }

                            recyclerAdapter= CartRecyclerAdapter(this@CartActivity, cartListItems)
                            recyclerCart.adapter = recyclerAdapter
                            recyclerCart.layoutManager = layoutManager

                        }
                        btnPlaceOrder.text= "Place Order(Total: Rs. $totalAmount)"
                    }
                    progressLayout.visibility=View.INVISIBLE

                },Response.ErrorListener {

                    println("error get $it")

                    Toast.makeText(this@CartActivity,"Some Error Occurred !!",Toast.LENGTH_SHORT).show()
                    Log.e("Error::::","/post request fail ! Error: ${it.message}")
                    progressLayout.visibility=View.INVISIBLE
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9786fdf7780ee4"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }catch (e:JSONException){
                Toast.makeText(this@CartActivity,"Some JSON Exception Occurred !!",Toast.LENGTH_SHORT).show()
            }

        }
        else{
            //Internet is not available
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("Error")
            dialog.setMessage(" No Internet Connection Found")
            dialog.setPositiveButton("Open Settings"){ text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@CartActivity.finish()
            }

            dialog.setNegativeButton("Exit"){ text, listener ->
                ActivityCompat.finishAffinity(this@CartActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //arrow
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id =item.itemId
        when(id){
            android.R.id.home ->{
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun createNotification(){
        val notificationId=1;
        val channelId="personal_notification"

        val notificationBulider=NotificationCompat.Builder(this,channelId)
        notificationBulider.setSmallIcon(R.drawable.ic_notification_icon)
        notificationBulider.setContentTitle("Order Placed")
        notificationBulider.setContentText("Your order has been successfully placed!")
        notificationBulider.setStyle(NotificationCompat.BigTextStyle().bigText("Ordered from "+restaurantName+" and amounting to Rs."+ totalAmount))
        notificationBulider.setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManagerCompat= NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId,notificationBulider.build())

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)//less than oreo
        {
            val name ="Order Placed"
            val description="Your order has been successfully placed!"
            val importance=NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel= NotificationChannel(channelId,name,importance)

            notificationChannel.description=description

            val notificationManager=  (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

}