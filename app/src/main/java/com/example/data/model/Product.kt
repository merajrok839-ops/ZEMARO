package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Float = 4.5f,
    val reviewsCount: Int = 120,
    val imageUrl: String,
    val description: String,
    val stock: Int = 50,
    val brand: String = "ZEMARO Luxe",
    val isFlashDeal: Boolean = false,
    val sellerId: String = "store-official",
    val sellerName: String = "ZEMARO Flagship Store",
    val commissionPercent: Double = 10.0,
    val dateAdded: Long = System.currentTimeMillis(),
    val isEnabledByOwner: Boolean = true,
    val isLockedByOwner: Boolean = false
) {
    val discountPercent: Int
        get() = if (originalPrice > price && originalPrice > 0) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else {
            0
        }
}
