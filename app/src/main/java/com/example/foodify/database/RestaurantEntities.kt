package com.example.foodify.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="restaurants" )
data class RestaurantEntities(
    @PrimaryKey val id: String,
    @ColumnInfo(name ="name") val restaurantName: String,
    @ColumnInfo(name = "rating") val restaurantRating : String,
    @ColumnInfo(name = "cost_for_one") val restaurantCostForOne: String,
    @ColumnInfo(name = "image_url") val restaurantImage: String
)
