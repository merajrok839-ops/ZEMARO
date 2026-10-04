package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackingId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val cityVillage: String,
    val pinCode: String,
    val itemsSummary: String,
    val totalAmount: Double,
    val commissionAmount: Double,
    val sellerPayoutAmount: Double,
    val paymentMethod: String,
    val status: String = "Order Placed",
    val deliveryOtp: String,
    val orderDate: Long = System.currentTimeMillis(),
    val liveCoordinates: String = ""
)
