package com.example.foodify.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [RestaurantEntities::class], version= 1)
abstract class RestaurantDatabase: RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao

}