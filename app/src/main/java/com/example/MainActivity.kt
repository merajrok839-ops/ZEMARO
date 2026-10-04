package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.ui.screens.AdminOwnerScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SellerPortalScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ZemaroViewModel

enum class Screen {
    SPLASH,
    HOME,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    ORDERS,
    SELLER_PORTAL,
    ADMIN_OWNER
}

class MainActivity : ComponentActivity() {

    private val viewModel: ZemaroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                ZemaroApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ZemaroApp(viewModel: ZemaroViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val cartTotal by viewModel.cartTotal.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            Screen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = Screen.HOME
                    }
                )
            }

            Screen.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    onProductClick = { product ->
                        selectedProduct = product
                        currentScreen = Screen.PRODUCT_DETAIL
                    },
                    onNavigateToCart = { currentScreen = Screen.CART },
                    onNavigateToOrders = { currentScreen = Screen.ORDERS },
                    onNavigateToSeller = { currentScreen = Screen.SELLER_PORTAL },
                    onNavigateToAdmin = { currentScreen = Screen.ADMIN_OWNER },
                    modifier = modifier
                )
            }

            Screen.PRODUCT_DETAIL -> {
                selectedProduct?.let { product ->
                    ProductDetailScreen(
                        product = product,
                        isWishlisted = wishlistIds.contains(product.id),
                        onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                        onAddToCart = { viewModel.addToCart(product) },
                        onBuyNow = {
                            viewModel.addToCart(product)
                            currentScreen = Screen.CHECKOUT
                        },
                        onBack = { currentScreen = Screen.HOME },
                        modifier = modifier
                    )
                } ?: run {
                    currentScreen = Screen.HOME
                }
            }

            Screen.CART -> {
                CartScreen(
                    cartItems = cartItems,
                    onUpdateQuantity = { id, delta -> viewModel.updateCartQuantity(id, delta) },
                    onRemoveItem = { id -> viewModel.removeFromCart(id) },
                    onProceedToCheckout = { currentScreen = Screen.CHECKOUT },
                    onBack = { currentScreen = Screen.HOME },
                    modifier = modifier
                )
            }

            Screen.CHECKOUT -> {
                CheckoutScreen(
                    totalAmount = if (cartTotal > 0) cartTotal else 499.0,
                    onConfirmOrder = { name, phone, address, city, pin, paymentMethod, liveCoords, onSuccess ->
                        viewModel.placeOrder(
                            customerName = name,
                            customerPhone = phone,
                            deliveryAddress = address,
                            cityVillage = city,
                            pinCode = pin,
                            paymentMethod = paymentMethod,
                            liveCoords = liveCoords,
                            onConfirmed = onSuccess
                        )
                    },
                    onNavigateToHome = { currentScreen = Screen.HOME },
                    onNavigateToOrders = { currentScreen = Screen.ORDERS },
                    onBack = { currentScreen = Screen.CART },
                    modifier = modifier
                )
            }

            Screen.ORDERS -> {
                OrdersScreen(
                    orders = orders,
                    onBack = { currentScreen = Screen.HOME },
                    modifier = modifier
                )
            }

            Screen.SELLER_PORTAL -> {
                SellerPortalScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = Screen.HOME },
                    modifier = modifier
                )
            }

            Screen.ADMIN_OWNER -> {
                AdminOwnerScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = Screen.HOME },
                    modifier = modifier
                )
            }
        }
    }
}
