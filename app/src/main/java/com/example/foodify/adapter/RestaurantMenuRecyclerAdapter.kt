package com.example.foodify.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodify.R
import com.example.foodify.activity.CartActivity
import com.example.foodify.model.RestaurantMenu

class RestaurantMenuRecyclerAdapter(val context: Context, val restaurantId: String, val restaurantName: String, val itemList: ArrayList<RestaurantMenu>, val proceedToCartPassed: RelativeLayout, val btnProceedToCart: Button): RecyclerView.Adapter <RestaurantMenuRecyclerAdapter.RestaurantMenuViewHolder>() {

    var itemSelectedCount: Int =0
    lateinit var proceedToCart: RelativeLayout
    var itemsSelectedId = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_single_row, parent,false)

        return RestaurantMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder:RestaurantMenuViewHolder, position: Int) {
        val restaurantMenu = itemList[position]

        holder.btnAddToCart.setTag(restaurantMenu.itemId+"")
        holder.txtItemSerialNo.text = (position+1).toString()
        holder.txtItemName.text = restaurantMenu.itemName
        holder.txtItemCostForOne.text ="Rs. "+ restaurantMenu.itemCostForOne

        proceedToCart = proceedToCartPassed
        btnProceedToCart.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("restaurantName", restaurantName)
            intent.putExtra("selectedItemsId", itemsSelectedId)
            context.startActivity(intent)
        })
        holder.btnAddToCart.setOnClickListener (View.OnClickListener {

            if(holder.btnAddToCart.text.toString().equals("Remove")){
                itemSelectedCount--
                itemsSelectedId.remove(holder.btnAddToCart.getTag().toString())
                holder.btnAddToCart.text = "Add"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(244,67,54)) //primary to red
            }
            else{
                itemSelectedCount++
                itemsSelectedId.add(holder.btnAddToCart.getTag().toString())
                holder.btnAddToCart.text ="Remove"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(255,196,0)) //yellow to red
            }

            if(itemSelectedCount>0){
                proceedToCart.visibility = View.VISIBLE
            }
            else{
                proceedToCart.visibility = View.INVISIBLE
            }

        })


    }

    class RestaurantMenuViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val txtItemName : TextView = view.findViewById(R.id.txtItemName)
        val txtItemCostForOne : TextView = view.findViewById(R.id.txtItemCostForOne)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val txtItemSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
    }

    fun getSelectedItemCount():Int{
        return itemSelectedCount
    }

}