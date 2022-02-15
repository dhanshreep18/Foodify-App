package com.example.foodify.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.foodify.adapter.OrderHistoryRecyclerAdapter
import com.example.foodify.model.OrderHistoryRestaurant
import com.example.foodify.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerViewOrder: RecyclerView

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerViewOrder = view.findViewById(R.id.recyclerOrderHistory)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar= view.findViewById(R.id.progressBar)
        layoutManager= LinearLayoutManager(context as Context)

        val sharedPreferences = getActivity()?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val user_id = sharedPreferences?.getString("user_id","000")

        val orderedRestaurantList = ArrayList<OrderHistoryRestaurant>()

        if(ConnectionManager().checkConnectivity(context as Context)){
            progressLayout.visibility=View.VISIBLE
           try {

               val queue = Volley.newRequestQueue(activity as Context)
               val url = "http://13.235.250.119/v2/orders/fetch_result/+${user_id}"

               val jsonObjectRequest=object :JsonObjectRequest(Request.Method.GET ,url,null, Response.Listener {

                   val rjobj = it.getJSONObject("data")
                   val success = rjobj.getBoolean("success")

                   if(success){
                       println("Response is $it")
                       val data= rjobj.getJSONArray("data")
                       if(data.length()==0){
                           Toast.makeText(activity as Context,"No Orders Placed yet !!",Toast.LENGTH_SHORT ).show()
                           progressLayout.visibility=View.VISIBLE
                       }
                       else{
                           progressLayout.visibility=View.INVISIBLE

                           for(i in 0 until data.length()){
                               val restaurantItemJsonObject = data.getJSONObject(i)

                               val eachRestaurantObject = OrderHistoryRestaurant(
                                   restaurantItemJsonObject.getString("order_id"),
                                   restaurantItemJsonObject.getString("restaurant_name"),
                                   restaurantItemJsonObject.getString("total_cost"),
                                   restaurantItemJsonObject.getString("order_placed_at").substring(0,10)
                               )
                               orderedRestaurantList.add(eachRestaurantObject)
                               recyclerAdapter= OrderHistoryRecyclerAdapter(activity as Context,orderedRestaurantList)
                               recyclerViewOrder.adapter = recyclerAdapter
                               recyclerViewOrder.layoutManager=layoutManager

                           }
                       }
                   }
                   progressLayout.visibility=View.INVISIBLE

               },Response.ErrorListener {
                   progressLayout.visibility=View.INVISIBLE
                   println("Error is $it")
                   Toast.makeText(activity as Context,"Some Error occurred !!", Toast.LENGTH_SHORT).show()
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
               Toast.makeText(activity as Context, "Some JSON Exception Occurred !!", Toast.LENGTH_SHORT).show()
           }
        }
        else{
            //Internet is not available
            val dialog = AlertDialog.Builder(context as Context)
            dialog.setTitle("Error")
            dialog.setMessage(" No Internet Connection Found")
            dialog.setPositiveButton("Open Settings"){ text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){ text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

}