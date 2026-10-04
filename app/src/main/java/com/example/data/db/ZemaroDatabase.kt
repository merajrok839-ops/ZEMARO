package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BannerDao
import com.example.data.dao.OrderDao
import com.example.data.dao.ProductDao
import com.example.data.model.BannerAd
import com.example.data.model.Order
import com.example.data.model.Product

@Database(
    entities = [Product::class, BannerAd::class, Order::class],
    version = 2,
    exportSchema = false
)
abstract class ZemaroDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun bannerDao(): BannerDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: ZemaroDatabase? = null

        fun getInstance(context: Context): ZemaroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZemaroDatabase::class.java,
                    "zemaro_marketplace.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
