package com.example.data.repository

import android.content.Context
import com.example.data.dao.BannerDao
import com.example.data.dao.OrderDao
import com.example.data.dao.ProductDao
import com.example.data.db.ZemaroDatabase
import com.example.data.model.BannerAd
import com.example.data.model.Order
import com.example.data.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

class ZemaroRepository(
    private val productDao: ProductDao,
    private val bannerDao: BannerDao,
    private val orderDao: OrderDao
) {
    companion object {
        const val OWNER_EMAIL = "merajrok839@gmail.com"
        const val OWNER_NAME = "MD Meraj Ansari"
        const val OWNER_WHATSAPP_NUMBER = "917979864406"
        const val OWNER_UPI_ID = "7979864406-i40c@axl"

        /**
         * Commission Calculation Engine:
         * - ₹500 range: 10% platform commission
         * - ₹1000 range: 20% platform commission
         * - Dynamic modular calculation above ₹1000 adding 10% per tier
         */
        fun calculateCommissionRate(price: Double): Double {
            return when {
                price <= 0.0 -> 10.0
                price <= 600.0 -> 10.0
                price <= 1200.0 -> 20.0
                else -> {
                    val excess = price - 1000.0
                    val tiers = (excess / 1000.0).toInt() + 1
                    val rate = 20.0 + (tiers * 10.0)
                    rate.coerceAtMost(40.0)
                }
            }
        }

        fun calculateCommissionAmount(price: Double): Double {
            val rate = calculateCommissionRate(price)
            return (price * rate) / 100.0
        }

        fun calculateSellerPayout(price: Double): Double {
            return price - calculateCommissionAmount(price)
        }
    }

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val flashDeals: Flow<List<Product>> = productDao.getFlashDeals()
    val activeBanners: Flow<List<BannerAd>> = bannerDao.getActiveBanners()
    val allBanners: Flow<List<BannerAd>> = bannerDao.getAllBanners()
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    fun getProductsByCategory(category: String): Flow<List<Product>> = productDao.getProductsByCategory(category)
    fun getProductsBySeller(sellerId: String): Flow<List<Product>> = productDao.getProductsBySeller(sellerId)

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(id: Long) = productDao.deleteById(id)
    suspend fun updateProductStatus(id: Long, enabled: Boolean) = productDao.updateProductStatus(id, enabled)
    suspend fun updateProductLock(id: Long, locked: Boolean) = productDao.updateProductLock(id, locked)
    suspend fun overrideProductPrice(id: Long, newPrice: Double) = productDao.overrideProductPrice(id, newPrice)

    suspend fun insertBanner(banner: BannerAd): Long = bannerDao.insertBanner(banner)
    suspend fun updateBanner(banner: BannerAd) = bannerDao.updateBanner(banner)
    suspend fun deleteBanner(id: Long) = bannerDao.deleteById(id)

    suspend fun getOrderByTrackingId(trackingId: String): Order? = orderDao.getOrderByTrackingId(trackingId)
    suspend fun updateOrderStatus(orderId: Long, status: String) = orderDao.updateOrderStatus(orderId, status)

    suspend fun createOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        cityVillage: String,
        pinCode: String,
        itemsSummary: String,
        totalAmount: Double,
        paymentMethod: String,
        liveCoordinates: String = ""
    ): Order {
        val trackingId = "ZEM-" + Random.nextInt(100000, 999999)
        val deliveryOtp = String.format("%04d", Random.nextInt(1000, 9999))
        val commission = calculateCommissionAmount(totalAmount)
        val sellerPayout = totalAmount - commission

        val order = Order(
            trackingId = trackingId,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            cityVillage = cityVillage,
            pinCode = pinCode,
            itemsSummary = itemsSummary,
            totalAmount = totalAmount,
            commissionAmount = commission,
            sellerPayoutAmount = sellerPayout,
            paymentMethod = paymentMethod,
            status = "Order Placed",
            deliveryOtp = deliveryOtp,
            orderDate = System.currentTimeMillis(),
            liveCoordinates = liveCoordinates
        )

        val id = orderDao.insertOrder(order)
        return order.copy(id = id)
    }

    /**
     * Formats WhatsApp dispatch message for background routing to the store owner
     */
    fun buildSecretWhatsAppDispatchUrl(order: Order): String {
        val mapsLink = if (order.liveCoordinates.isNotBlank()) {
            "https://maps.google.com/?q=${order.liveCoordinates}"
        } else {
            "Not Available"
        }

        val text = """
            🛍️ *NEW ZEMARO DISPATCH ORDER*
            ══════════════════════
            🆔 *Tracking ID:* #${order.trackingId}
            👤 *Customer:* ${order.customerName}
            📞 *Contact:* ${order.customerPhone}
            📍 *Address:* ${order.deliveryAddress}, ${order.cityVillage} - ${order.pinCode}
            🗺️ *Live Location:* $mapsLink
            
            📦 *Items Ordered:*
            ${order.itemsSummary}
            
            💰 *Total Amount:* ₹${String.format("%.2f", order.totalAmount)}
            📊 *Platform Commission:* ₹${String.format("%.2f", order.commissionAmount)}
            💵 *Net Seller Payout:* ₹${String.format("%.2f", order.sellerPayoutAmount)}
            💳 *Payment Method:* ${order.paymentMethod}
            🔐 *Delivery Verification OTP:* ${order.deliveryOtp}
            ⏰ *Timestamp:* ${java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(order.orderDate))}
            ══════════════════════
            _Processed securely by ZEMARO Core Engine_
        """.trimIndent()

        val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
        return "https://api.whatsapp.com/send?phone=$OWNER_WHATSAPP_NUMBER&text=$encoded"
    }

    suspend fun seedInitialDataIfEmpty() {
        val existingProducts = productDao.getAllProducts().first()
        if (existingProducts.isEmpty()) {
            val initialProducts = listOf(
                Product(
                    title = "ZEMARO CyberBuds Ultra ANC Earphones",
                    category = "Electronics",
                    price = 499.0,
                    originalPrice = 1999.0,
                    rating = 4.8f,
                    reviewsCount = 1420,
                    imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
                    description = "Hyper-bass 45ms ultra low latency wireless gaming earbuds with active noise cancellation and RGB cyber charging case.",
                    stock = 85,
                    brand = "ZEMARO Tech",
                    isFlashDeal = true,
                    commissionPercent = 10.0
                ),
                Product(
                    title = "Titan Chrono Apex Luxury Smartwatch",
                    category = "Electronics",
                    price = 999.0,
                    originalPrice = 3499.0,
                    rating = 4.9f,
                    reviewsCount = 2890,
                    imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    description = "Sapphire glass 1.96-inch AMOLED display, Bluetooth calling, SpO2 & Heart monitor, aerospace titanium casing.",
                    stock = 40,
                    brand = "Apex Luxe",
                    isFlashDeal = true,
                    commissionPercent = 20.0
                ),
                Product(
                    title = "Urban Streetwear Oversized Graphic Hoodie",
                    category = "Fashion",
                    price = 599.0,
                    originalPrice = 1499.0,
                    rating = 4.6f,
                    reviewsCount = 890,
                    imageUrl = "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600&auto=format&fit=crop&q=80",
                    description = "100% combed heavyweight cotton with breathable fleece lining. Acid wash cyberpunk typography graphic back print.",
                    stock = 120,
                    brand = "ZEMARO Drip",
                    isFlashDeal = false,
                    commissionPercent = 10.0
                ),
                Product(
                    title = "AirGlide Nitro Men's Running Sneakers",
                    category = "Footwear",
                    price = 1099.0,
                    originalPrice = 2999.0,
                    rating = 4.7f,
                    reviewsCount = 612,
                    imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                    description = "Energy return nitrogen-infused cushioning sole. Breathable mesh upper engineered for marathon comfort and daily flex.",
                    stock = 65,
                    brand = "GlideLab",
                    isFlashDeal = true,
                    commissionPercent = 20.0
                ),
                Product(
                    title = "Minimalist Matte Black Mechanical Watch",
                    category = "Accessories",
                    price = 1499.0,
                    originalPrice = 3999.0,
                    rating = 4.9f,
                    reviewsCount = 445,
                    imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600&auto=format&fit=crop&q=80",
                    description = "Automatic mechanical skeleton movement with waterproof stainless steel mesh strap and scratch-resistant crystal.",
                    stock = 30,
                    brand = "Vanguard Time",
                    isFlashDeal = false,
                    commissionPercent = 30.0
                ),
                Product(
                    title = "Nova Pro Wireless Studio Headphones",
                    category = "Electronics",
                    price = 1999.0,
                    originalPrice = 4999.0,
                    rating = 4.8f,
                    reviewsCount = 1120,
                    imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                    description = "Hi-Res spatial audio with 40mm beryllium drivers, 60-hour marathon battery, plush memory foam earcups.",
                    stock = 50,
                    brand = "NovaAcoustics",
                    isFlashDeal = true,
                    commissionPercent = 30.0
                ),
                Product(
                    title = "Nordic Ceramic Pour-Over Coffee Maker Set",
                    category = "Home & Kitchen",
                    price = 450.0,
                    originalPrice = 1199.0,
                    rating = 4.7f,
                    reviewsCount = 330,
                    imageUrl = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=600&auto=format&fit=crop&q=80",
                    description = "Handcrafted matte ceramic dripper with borosilicate heat-resistant glass carafe and walnut wood collar.",
                    stock = 75,
                    brand = "NordicCraft",
                    isFlashDeal = false,
                    commissionPercent = 10.0
                ),
                Product(
                    title = "Botanical Glow Vitamin C Brightening Serum",
                    category = "Beauty",
                    price = 499.0,
                    originalPrice = 899.0,
                    rating = 4.8f,
                    reviewsCount = 780,
                    imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600&auto=format&fit=crop&q=80",
                    description = "20% pure L-ascorbic acid with ferulic acid and hyaluronic acid for radiant, glowing, even-toned skin.",
                    stock = 90,
                    brand = "PureBotany",
                    isFlashDeal = false,
                    commissionPercent = 10.0
                )
            )
            productDao.insertAll(initialProducts)
        }

        val existingBanners = bannerDao.getAllBanners().first()
        if (existingBanners.isEmpty()) {
            val initialBanners = listOf(
                BannerAd(
                    title = "ZEMARO FESTIVAL OF DEALS",
                    subtitle = "Mega Flash Sales on Flagship Tech & Luxury Wear",
                    imageUrl = "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=1000&auto=format&fit=crop&q=80",
                    badgeText = "LIGHTNING SALE",
                    discountHighlight = "UP TO 80% OFF",
                    targetCategory = "Electronics",
                    priority = 1
                ),
                BannerAd(
                    title = "NEXT-GEN STREETWEAR DROP",
                    subtitle = "Exclusive Cyber Minimalist Hoodies & Sneakers",
                    imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1000&auto=format&fit=crop&q=80",
                    badgeText = "NEW ARRIVALS",
                    discountHighlight = "FLAT 60% OFF",
                    targetCategory = "Fashion",
                    priority = 2
                ),
                BannerAd(
                    title = "PREMIUM AUDIO EXTRAVAGANZA",
                    subtitle = "Immersive Spatial Sound ANC Earbuds & Cans",
                    imageUrl = "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=1000&auto=format&fit=crop&q=80",
                    badgeText = "LIMITED TIME",
                    discountHighlight = "STARTING @ ₹499",
                    targetCategory = "Electronics",
                    priority = 3
                )
            )
            bannerDao.insertAll(initialBanners)
        }
    }
}
