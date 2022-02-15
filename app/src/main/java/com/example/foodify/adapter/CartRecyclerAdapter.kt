package com.example.foodify.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodify.R
import com.example.foodify.model.CartItems
import kotlinx.android.synthetic.main.recycler_cart_single_row.view.*

class CartRecyclerAdapter(val context: Context, val cartItems:ArrayList<CartItems>): RecyclerView.Adapter<CartRecyclerAdapter.ViewHolderCart>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject = cartItems[position]
        holder.txtViewOrderItem.text = cartItemObject.itemName
        holder.txtViewOrderItemPrice.text = "Rs. "+cartItemObject.itemPrice
    }

    class ViewHolderCart(view: View):RecyclerView.ViewHolder(view){
        val txtViewOrderItem: TextView = view.findViewById(R.id.txtViewOrderItem)
        val txtViewOrderItemPrice: TextView= view.findViewById(R.id.txtViewOrderItemPrice)
    }
}

