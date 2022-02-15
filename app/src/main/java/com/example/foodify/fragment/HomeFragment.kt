/*package com.example.foodify.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.adapter.HomeRecyclerAdapter
import com.example.foodify.model.Restaurant
import com.example.foodify.util.ConnectionManager
import org.json.JSONException


class HomeFragment : Fragment() {

    lateinit var recyclerHome : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val restaurantList= arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressBar= view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                  println("Response is $it")
                try {
                    progressLayout.visibility = View.GONE
                    val success = it.getBoolean("success")

                    if(success){
                        val data = it.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val restaurantJsonObject = data.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )

                            restaurantList.add(restaurantObject)

                            recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantList)
                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager
                        }

                    }
                    else{
                        Toast.makeText(activity as Context, "Some Error Occurred !!", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: JSONException){
                    Toast.makeText(activity as Context, "Some JSON Exception Occurred !!", Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {
                println("Error is $it ")
                if(activity!=null){
                    Toast.makeText(activity as Context, "Volley error occurred !!", Toast.LENGTH_SHORT).show()
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


} */
package com.example.foodify.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.adapter.HomeRecyclerAdapter
import com.example.foodify.model.Restaurant
import com.example.foodify.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var recyclerHome : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var radioButtonView: View

    val restaurantList= arrayListOf<Restaurant>()

    var costComparator = Comparator<Restaurant>{ rest1, rest2 ->
        rest1.restaurantCostForOne.compareTo(rest2.restaurantCostForOne , true)
    }

    var ratingComparator = Comparator<Restaurant> { rest1, rest2 ->
        if(rest1.restaurantRating.compareTo(rest2.restaurantRating, true)==0){
            rest1.restaurantName.compareTo(rest2.restaurantName, true)
        }
        else{
            rest1.restaurantRating.compareTo(rest2.restaurantRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)
        val view= inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressBar= view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                println("Response is $it")
                try {
                    progressLayout.visibility = View.GONE
                    val jobj=it.getJSONObject("data");
                    val success = jobj.getBoolean("success")

                    if(success){
                        val data = jobj.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val restaurantJsonObject = data.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )

                            restaurantList.add(restaurantObject)

                            recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantList)
                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager
                        }

                    }
                    else{
                        Toast.makeText(activity as Context, "Some Error Occurred !!", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e: JSONException){
                    Toast.makeText(activity as Context, "Some JSON Exception Occurred !!", Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {
                println("Error is $it ")
                if(activity!=null){
                    Toast.makeText(activity as Context, "Volley error occurred !!", Toast.LENGTH_SHORT).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id= item?.itemId
        when(id){
            R.id.action_sort ->{
                radioButtonView = View.inflate(context as Activity, R.layout.sort_radio_button, null)
                AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By ?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK"){text, listener ->
                         if (radioButtonView.radioHighToLow.isChecked){
                             Collections.sort(restaurantList, costComparator)
                             restaurantList.reverse()
                             recyclerAdapter.notifyDataSetChanged()
                         }
                        if(radioButtonView.radioLowToHigh.isChecked){
                            Collections.sort(restaurantList,costComparator)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButtonView.radioRating.isChecked){
                            Collections.sort(restaurantList, ratingComparator)
                            restaurantList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                        }

                    }.setNegativeButton("CANCEL"){ text, listener ->

                    }
                    .create()
                    .show()

            }
        }
        return super.onOptionsItemSelected(item)
    }


}

