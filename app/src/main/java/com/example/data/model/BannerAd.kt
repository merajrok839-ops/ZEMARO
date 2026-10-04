package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banners")
data class BannerAd(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val badgeText: String = "MEGA SALE",
    val discountHighlight: String = "UP TO 80% OFF",
    val targetCategory: String = "All",
    val active: Boolean = true,
    val priority: Int = 1
)
