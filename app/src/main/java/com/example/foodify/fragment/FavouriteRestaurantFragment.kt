package com.example.foodify.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.adapter.FavouriteRecyclerAdapter
import com.example.foodify.adapter.HomeRecyclerAdapter
import com.example.foodify.database.RestaurantDatabase
import com.example.foodify.database.RestaurantEntities
import com.example.foodify.model.Restaurant
import com.example.foodify.util.ConnectionManager
import org.json.JSONException

class FavouriteRestaurantFragment : Fragment() {

    lateinit var recyclerFavourite : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar: ProgressBar

    var restaurantInfoList= arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = LinearLayoutManager(activity as Context)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"


        if(ConnectionManager().checkConnectivity(activity as Context)) {
            try {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                        println("Response is $it")

                        progressLayout.visibility = View.GONE
                        val jobj = it.getJSONObject("data")
                        val success = jobj.getBoolean("success")

                        if (success) {
                            val data = jobj.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)

                                val restaurantEntities = RestaurantEntities(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                if (DBAsyncTask(
                                        activity as Context,
                                        restaurantEntities,
                                        1
                                    ).execute()
                                        .get()
                                ) {
                                    val restaurant = Restaurant(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("image_url")
                                    )
                                    restaurantInfoList.add(restaurant)
                                    recyclerAdapter =
                                        HomeRecyclerAdapter(activity as Context, restaurantInfoList)
                                    recyclerFavourite.adapter = recyclerAdapter
                                    recyclerFavourite.layoutManager = layoutManager
                                }
                            }
                            if (restaurantInfoList.size == 0) {
                                Toast.makeText(
                                    activity as Context,
                                    "Favourites list Empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }, Response.ErrorListener {
                        println("Error is $it")
                        Toast.makeText(
                            activity as Context,
                            "Some Volley Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressLayout.visibility = View.GONE
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9786fdf7780ee4"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)

            }catch (e:JSONException){
                Toast.makeText(activity as Context, "Some JSON Exception occurred !!", Toast.LENGTH_SHORT).show()
            }

        }
        else{
            val dialog = AlertDialog.Builder(activity as Context)
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

    class DBAsyncTask(val context: Context, val restaurantEntities: RestaurantEntities, val mode: Int): AsyncTask<Void, Void, Boolean>(){
        /* mode1: check
           mode2: save
           mode3: remove */

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1->{
                    val restaurant: RestaurantEntities = db.restaurantDao().getRestaurantById(restaurantEntities.id.toString())
                    db.close()
                    return restaurant !=null
                }
                2->{
                    db.restaurantDao().insertRestaurant(restaurantEntities)
                    db.close()
                    return true
                }
                3->{
                    db.restaurantDao().deleteRestaurant(restaurantEntities)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}