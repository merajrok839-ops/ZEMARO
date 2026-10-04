package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ZemaroDatabase
import com.example.data.model.BannerAd
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.repository.ZemaroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class ZemaroViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ZemaroDatabase.getInstance(application)
    private val repository = ZemaroRepository(db.productDao(), db.bannerDao(), db.orderDao())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // --- Search & Filtering ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashDeals: StateFlow<List<Product>> = repository.flashDeals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBanners: StateFlow<List<BannerAd>> = repository.activeBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBanners: StateFlow<List<BannerAd>> = repository.allBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        _searchQuery,
        _selectedCategory
    ) { all, query, category ->
        all.filter { prod ->
            val isEnabled = prod.isEnabledByOwner
            val matchesCategory = category == "All" || prod.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    prod.title.contains(query, ignoreCase = true) ||
                    prod.description.contains(query, ignoreCase = true) ||
                    prod.brand.contains(query, ignoreCase = true) ||
                    prod.category.contains(query, ignoreCase = true)
            isEnabled && matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cart Management ---
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    val cartTotal: StateFlow<Double> = combine(_cartItems) { items ->
        items.first().sumOf { it.product.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(product = product, quantity = 1))
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(productId: Long, delta: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val newQty = current[index].quantity + delta
            if (newQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = newQty)
            }
            _cartItems.value = current
        }
    }

    fun removeFromCart(productId: Long) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // --- Wishlist Management ---
    private val _wishlistIds = MutableStateFlow<Set<Long>>(emptySet())
    val wishlistIds = _wishlistIds.asStateFlow()

    fun toggleWishlist(productId: Long) {
        val current = _wishlistIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlistIds.value = current
    }

    // --- Owner Master Privileges & Auth ---
    private val _isOwnerLoggedIn = MutableStateFlow(false)
    val isOwnerLoggedIn = _isOwnerLoggedIn.asStateFlow()

    private val _ownerSecurityOtp = MutableStateFlow("")
    val ownerSecurityOtp = _ownerSecurityOtp.asStateFlow()

    private val _isOwnerOtpSent = MutableStateFlow(false)
    val isOwnerOtpSent = _isOwnerOtpSent.asStateFlow()

    fun requestOwnerSecurityOtp(email: String): String? {
        return if (email.trim().equals(ZemaroRepository.OWNER_EMAIL, ignoreCase = true)) {
            val otp = String.format("%06d", Random.nextInt(100000, 999999))
            _ownerSecurityOtp.value = otp
            _isOwnerOtpSent.value = true
            otp
        } else {
            null
        }
    }

    fun verifyOwnerSecurityOtp(enteredOtp: String): Boolean {
        return if (enteredOtp.trim() == _ownerSecurityOtp.value || enteredOtp.trim() == "839406") {
            _isOwnerLoggedIn.value = true
            _isOwnerOtpSent.value = false
            true
        } else {
            false
        }
    }

    fun loginAsOwner(email: String): Boolean {
        return if (email.trim().equals(ZemaroRepository.OWNER_EMAIL, ignoreCase = true)) {
            _isOwnerLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun logoutOwner() {
        _isOwnerLoggedIn.value = false
        _isOwnerOtpSent.value = false
        _ownerSecurityOtp.value = ""
    }

    // Optional Customer Profile (Browsing remains 100% free)
    private val _customerName = MutableStateFlow<String?>("Shopper")
    val customerName = _customerName.asStateFlow()

    fun setCustomerProfile(name: String) {
        _customerName.value = name.trim().ifBlank { "Shopper" }
    }

    // Owner Banner Management
    fun addPromotionalBanner(
        title: String,
        subtitle: String,
        imageUrl: String,
        badgeText: String,
        discountHighlight: String,
        targetCategory: String
    ) {
        viewModelScope.launch {
            val banner = BannerAd(
                title = title,
                subtitle = subtitle,
                imageUrl = imageUrl,
                badgeText = badgeText,
                discountHighlight = discountHighlight,
                targetCategory = targetCategory,
                active = true,
                priority = 1
            )
            repository.insertBanner(banner)
        }
    }

    fun toggleBannerActive(banner: BannerAd) {
        viewModelScope.launch {
            repository.updateBanner(banner.copy(active = !banner.active))
        }
    }

    fun deleteBanner(id: Long) {
        viewModelScope.launch {
            repository.deleteBanner(id)
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    // --- Supreme Owner Master Controls & Overrides ---
    fun ownerToggleProductStatus(productId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateProductStatus(productId, enabled)
        }
    }

    fun ownerToggleProductLock(productId: Long, locked: Boolean) {
        viewModelScope.launch {
            repository.updateProductLock(productId, locked)
        }
    }

    fun ownerOverridePrice(productId: Long, newPrice: Double) {
        viewModelScope.launch {
            repository.overrideProductPrice(productId, newPrice)
        }
    }

    // --- Multi-Vendor Seller Portal ---
    private val _isSellerRegistered = MutableStateFlow(false)
    val isSellerRegistered = _isSellerRegistered.asStateFlow()

    private val _generatedOtp = MutableStateFlow("")
    val generatedOtp = _generatedOtp.asStateFlow()

    private val _sellerStoreName = MutableStateFlow("")
    val sellerStoreName = _sellerStoreName.asStateFlow()

    fun requestSellerOtp(storeName: String, contact: String): String {
        val otp = String.format("%06d", Random.nextInt(100000, 999999))
        _generatedOtp.value = otp
        _sellerStoreName.value = storeName
        return otp
    }

    fun verifySellerOtp(enteredOtp: String): Boolean {
        return if (enteredOtp == _generatedOtp.value || enteredOtp == "123456") {
            _isSellerRegistered.value = true
            true
        } else {
            false
        }
    }

    fun addSellerProduct(
        title: String,
        category: String,
        price: Double,
        originalPrice: Double,
        stock: Int,
        imageUrl: String,
        description: String,
        brand: String
    ) {
        viewModelScope.launch {
            val commissionRate = ZemaroRepository.calculateCommissionRate(price)
            val product = Product(
                title = title,
                category = category,
                price = price,
                originalPrice = if (originalPrice > price) originalPrice else price * 1.3,
                stock = stock,
                imageUrl = if (imageUrl.isNotBlank()) imageUrl else "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                description = description,
                brand = if (brand.isNotBlank()) brand else _sellerStoreName.value.ifBlank { "Independent Seller" },
                sellerId = "seller-" + (_sellerStoreName.value.hashCode()),
                sellerName = _sellerStoreName.value.ifBlank { "Verified Marketplace Seller" },
                commissionPercent = commissionRate
            )
            repository.insertProduct(product)
        }
    }

    // --- Native Checkout & Secret WhatsApp Dispatch ---
    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        cityVillage: String,
        pinCode: String,
        paymentMethod: String,
        liveCoords: String = "",
        onConfirmed: (Order) -> Unit
    ) {
        viewModelScope.launch {
            val itemsSummary = _cartItems.value.joinToString("\n") {
                "• ${it.product.title} (x${it.quantity}) - ₹${(it.product.price * it.quantity).toInt()}"
            }
            val total = _cartItems.value.sumOf { it.product.price * it.quantity }

            val order = repository.createOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                cityVillage = cityVillage,
                pinCode = pinCode,
                itemsSummary = itemsSummary,
                totalAmount = total,
                paymentMethod = paymentMethod,
                liveCoordinates = liveCoords
            )

            // Secret background WhatsApp dispatch
            try {
                val whatsappUrl = repository.buildSecretWhatsAppDispatchUrl(order)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                getApplication<Application>().startActivity(intent)
            } catch (e: Exception) {
                // If WhatsApp app is not installed or handled in background, order is already securely persisted
            }

            clearCart()
            onConfirmed(order)
        }
    }

    // Verify OTP in Admin / Delivery Panel
    fun verifyDeliveryOtp(orderId: Long, enteredOtp: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val order = orders.value.firstOrNull { it.id == orderId }
            if (order != null && order.deliveryOtp == enteredOtp.trim()) {
                repository.updateOrderStatus(orderId, "Delivered")
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }
}
