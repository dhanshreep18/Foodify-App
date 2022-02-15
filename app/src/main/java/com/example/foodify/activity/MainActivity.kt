package com.example.foodify.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodify.R
import com.example.foodify.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    lateinit var txtName : TextView
    lateinit var txtMobileNumber: TextView



    var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)

        val sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val headerView = navigationView.getHeaderView(0)
        txtName = headerView.findViewById(R.id.txtName)
        txtMobileNumber = headerView.findViewById(R.id.txtMobileNumber)



        setUpToolbar()

        txtName.text=sharedPreferences.getString("name", "xyz")
        txtMobileNumber.text = sharedPreferences.getString("mobile_number", "1234567890")

        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {



            if(previousMenuItem != null){
                previousMenuItem?.isChecked = false   //if previousMenuItem exists uncheck it
            }
            it.isCheckable=true
            it.isChecked=true                 //check the current item
            previousMenuItem=it

            when(it.itemId){
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }

                R.id.profile ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment()).commit()
                    supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.favourites ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FavouriteRestaurantFragment()).commit()
                    supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }

                R.id.orderHistory ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame, OrderHistoryFragment()).commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                }

                R.id.faqs ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FAQsFragment()).commit()
                    supportActionBar?.title="FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.logout ->{
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Confirmation")
                    alertDialog.setMessage("Are you sure you want to Logout ?")
                    alertDialog.setPositiveButton("Yes") {text, listener ->
                        sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
                        ActivityCompat.finishAffinity(this)
                    }
                    alertDialog.setNegativeButton("No"){text,listener ->

                    }
                    alertDialog.create()
                    alertDialog.show()
                    drawerLayout.closeDrawers()
                }

            }
            return@setNavigationItemSelectedListener true
        }

    }

    fun openHome(){
        supportFragmentManager.beginTransaction().replace(R.id.frame, HomeFragment()).commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }

}