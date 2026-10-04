package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BannerAd
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao {
    @Query("SELECT * FROM banners WHERE active = 1 ORDER BY priority ASC, id DESC")
    fun getActiveBanners(): Flow<List<BannerAd>>

    @Query("SELECT * FROM banners ORDER BY id DESC")
    fun getAllBanners(): Flow<List<BannerAd>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerAd): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(banners: List<BannerAd>)

    @Update
    suspend fun updateBanner(banner: BannerAd)

    @Delete
    suspend fun deleteBanner(banner: BannerAd)

    @Query("DELETE FROM banners WHERE id = :id")
    suspend fun deleteById(id: Long)
}
