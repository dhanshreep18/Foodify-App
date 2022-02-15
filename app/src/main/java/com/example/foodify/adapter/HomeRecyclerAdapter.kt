package com.example.foodify.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.toolbox.Volley
import com.example.foodify.R
import com.example.foodify.activity.RestaurantMenuActivity
import com.example.foodify.database.RestaurantDatabase
import com.example.foodify.database.RestaurantEntities
import com.example.foodify.model.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_home_single_row.view.*
import kotlin.collections.ArrayList

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>): RecyclerView . Adapter <HomeRecyclerAdapter.HomeViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant= itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        holder.txtRestaurantCostForOne.text = "\u20B9 "+restaurant.restaurantCostForOne
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.default_restaurant_img).into(holder.imgRestaurantImage)

        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("id", restaurant.restaurantId)
            intent.putExtra("name", restaurant.restaurantName)
            context.startActivity(intent)
        }

        val restaurantEntity =RestaurantEntities(restaurant.restaurantId, restaurant.restaurantName, restaurant.restaurantRating,restaurant.restaurantCostForOne,restaurant.restaurantImage)

        holder.txtFavBorderIcon.setOnClickListener {
            if(!DBAsyncTask(context, restaurantEntity,1).execute().get()){
                val async = DBAsyncTask(context,restaurantEntity,2).execute()
                val result = async.get()

                if(result) {
                    Toast.makeText(context, "Restaurant Added to Favourites", Toast.LENGTH_SHORT).show()
                    holder.txtFavBorderIcon.background = context.resources.getDrawable(R.drawable.ic_heart)
                }
                else{
                    Toast.makeText(context, "Some error occurred !!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                val async= DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result){
                    Toast.makeText(context,"Removed from Favourites", Toast.LENGTH_SHORT).show()
                    holder.txtFavBorderIcon.background = context.resources.getDrawable(R.drawable.ic_heart_border_new)
                }
                else{
                    Toast.makeText(context,"Some Error Occurred !!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val checkFav = DBAsyncTask(context, restaurantEntity , 1).execute()
        val isFav = checkFav.get()

        if(isFav){
            holder.txtFavBorderIcon.setTag("liked")
            holder.txtFavBorderIcon.background = context.resources.getDrawable(R.drawable.ic_heart)
        }
        else{
            holder.txtFavBorderIcon.setTag("unliked")
            holder.txtFavBorderIcon.background = context.resources.getDrawable(R.drawable.ic_heart_border_new)
        }

    }

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView= view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating: TextView= view.findViewById(R.id.txtRestaurantRating)
        val txtRestaurantCostForOne: TextView= view.findViewById(R.id.txtRestaurantCostForOne)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val txtFavBorderIcon : TextView = view.findViewById(R.id.txtFavBorderIcon)
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