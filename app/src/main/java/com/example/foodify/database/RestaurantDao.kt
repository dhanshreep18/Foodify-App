package com.example.foodify.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntities: RestaurantEntities)

    @Delete
    fun deleteRestaurant(restaurantEntities: RestaurantEntities)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<RestaurantEntities>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantId")
    fun getRestaurantById(restaurantId : String):RestaurantEntities
}