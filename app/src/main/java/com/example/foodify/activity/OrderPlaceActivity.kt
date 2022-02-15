package com.example.foodify.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.foodify.R

class OrderPlaceActivity : AppCompatActivity() {

    lateinit var btnOkay : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_place)

        btnOkay=findViewById(R.id.btnOkay)
        btnOkay.setOnClickListener (View.OnClickListener {

            val intent = Intent(this@OrderPlaceActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()

        })
    }

    override fun onBackPressed() {

    }
}