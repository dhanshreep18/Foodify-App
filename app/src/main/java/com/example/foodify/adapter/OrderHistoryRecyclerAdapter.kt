package com.example.foodify.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.model.CartItems
import com.example.foodify.model.OrderHistoryRestaurant
import com.example.foodify.util.ConnectionManager
import kotlinx.android.synthetic.main.recycler_order_history_single_row.view.*
import org.json.JSONException

class OrderHistoryRecyclerAdapter(val context: Context, val orderedRestaurantList:ArrayList<OrderHistoryRestaurant>): RecyclerView.Adapter <OrderHistoryRecyclerAdapter.ViewHolderOrderHistoryRestaurant>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOrderHistoryRestaurant {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_row, parent, false)

        return ViewHolderOrderHistoryRestaurant(view)
    }

    override fun getItemCount(): Int {
        return  orderedRestaurantList.size
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistoryRestaurant, position: Int) {

        val restaurantObject = orderedRestaurantList[position]
        holder.txtViewRestaurantName.text = restaurantObject.restaurantName
        var formatDate = restaurantObject.orderPlacedAt
        formatDate = formatDate.replace("-","/")
        formatDate=formatDate.substring(0,6)+"20"+formatDate.substring(6,8)
        holder.txtViewOrderDate.text = formatDate

        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter : CartRecyclerAdapter

        if(ConnectionManager().checkConnectivity(context)){

            try{
                val orderItemsPerRestaurant = ArrayList<CartItems>()
                val sharedPreferences= context.getSharedPreferences(context.getString(R.string.preference_file_name),Context.MODE_PRIVATE)
                val user_id = sharedPreferences.getString("user_id","0")

                val queue = Volley.newRequestQueue(context)
                val url = "http://13.235.250.119/v2/orders/fetch_result/+${user_id}"
                val jsonObjectRequest=object: JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {

                    val rjobj = it.getJSONObject("data")
                    val success = rjobj.getBoolean("success")
                    if(success){
                        println("Response is $it")
                        val data= rjobj.getJSONArray("data")
                        val fetchedRestaurantJsonObject = data.getJSONObject(position)
                        orderItemsPerRestaurant.clear()
                        val foodOrderedJsonArray = fetchedRestaurantJsonObject.getJSONArray("food_items")
                        for(j in 0 until foodOrderedJsonArray.length()){
                            val eachFoodItem = foodOrderedJsonArray.getJSONObject(j)
                            val itemObject = CartItems(
                                eachFoodItem.getString("food_item_id"),
                                eachFoodItem.getString("name"),
                                eachFoodItem.getString("cost"),
                                "000"
                            )
                            orderItemsPerRestaurant.add(itemObject)
                        }

                        orderedItemAdapter = CartRecyclerAdapter(context, orderItemsPerRestaurant)
                        holder.recyclerOrderHistory.adapter = orderedItemAdapter
                        holder.recyclerOrderHistory.layoutManager = layoutManager
                    }

                },Response.ErrorListener {
                    println("Error is $ $it")
                    Toast.makeText( context,"Some Error occurred !!", Toast.LENGTH_SHORT).show()

                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="9786fdf7780ee4"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            }catch (e:JSONException){
                Toast.makeText(context,"Some JSON Exception occurred !!", Toast.LENGTH_SHORT).show()

            }
        }

    }

    class ViewHolderOrderHistoryRestaurant(view: View):RecyclerView.ViewHolder(view){
        val txtViewRestaurantName: TextView = view.findViewById(R.id.txtViewOrderRestaurantName)
        val txtViewOrderDate : TextView=view.findViewById(R.id.txtViewOrderDate)
        val recyclerOrderHistory: RecyclerView= view.findViewById(R.id.recyclerOrderHistory)
    }
}