package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.ui.components.BannerCarousel
import com.example.ui.components.ProductCard
import com.example.ui.components.SearchBarWithVoiceAndVisual
import com.example.ui.components.SupportChatbotModal
import com.example.ui.components.ZemaroLogo
import com.example.ui.theme.ZemaroAccentAmber
import com.example.ui.theme.ZemaroAccentCyan
import com.example.ui.theme.ZemaroAccentGreen
import com.example.ui.theme.ZemaroAccentRose
import com.example.ui.theme.ZemaroPrimary
import com.example.viewmodel.ZemaroViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: ZemaroViewModel,
    onProductClick: (Product) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSeller: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val flashDeals by viewModel.flashDeals.collectAsStateWithLifecycle()
    val activeBanners by viewModel.activeBanners.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val isOwnerLoggedIn by viewModel.isOwnerLoggedIn.collectAsStateWithLifecycle()
    val isSellerRegistered by viewModel.isSellerRegistered.collectAsStateWithLifecycle()
    val customerName by viewModel.customerName.collectAsStateWithLifecycle()

    var showChatbot by remember { mutableStateOf(false) }
    var showCustomerAuthDialog by remember { mutableStateOf(false) }
    var showAddBannerDialog by remember { mutableStateOf(false) }
    var gridColumns by remember { mutableIntStateOf(2) } // Compact 2 or 3-column density

    // Flash sale countdown timer state
    var countdownSeconds by remember { mutableStateOf(10482) }
    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }
    val hours = countdownSeconds / 3600
    val minutes = (countdownSeconds % 3600) / 60
    val seconds = countdownSeconds % 60
    val timerString = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds)

    val cartCount = cartItems.sumOf { it.quantity }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky Top Header with Brand Logo & Strategic Seller Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ZemaroLogo()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Strategic Prominent "Become a Seller / Add Product" Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ZemaroAccentAmber, Color(0xFFF59E0B))
                                )
                            )
                            .clickable { onNavigateToSeller() }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                            .testTag("top_become_seller_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSellerRegistered) "+ Add Product" else "Become a Seller",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    // Optional Customer Profile (Browsing is 100% free)
                    IconButton(
                        onClick = { showCustomerAuthDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("customer_profile_nav_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Customer Account",
                            tint = if (customerName != "Shopper") ZemaroAccentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // My Orders
                    IconButton(
                        onClick = onNavigateToOrders,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("orders_nav_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Orders",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Cart with Badge
                    IconButton(
                        onClick = onNavigateToCart,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("cart_nav_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = ZemaroAccentRose,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$cartCount", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Owner / Admin Portal Key
                    IconButton(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("admin_nav_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Owner Admin",
                            tint = if (isOwnerLoggedIn) ZemaroAccentGreen else ZemaroPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Advanced Multi-Modal Search Bar
            SearchBarWithVoiceAndVisual(
                searchQuery = searchQuery,
                onQueryChanged = { viewModel.updateSearchQuery(it) },
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.updateCategory(it) }
            )

            // Main Scrollable Product & Promo Feed (Amazon/Flipkart Hybrid Scrolling)
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_product_grid")
            ) {
                // Promotional Banner Carousel with Supreme Owner (+) Add Banner Control
                if (searchQuery.isBlank() && selectedCategory == "All") {
                    item(span = { GridItemSpan(gridColumns) }) {
                        BannerCarousel(
                            banners = activeBanners,
                            onBannerClick = { banner ->
                                if (banner.targetCategory != "All") {
                                    viewModel.updateCategory(banner.targetCategory)
                                }
                            },
                            isOwnerLoggedIn = isOwnerLoggedIn,
                            onAddBannerClick = { showAddBannerDialog = true }
                        )
                    }

                    // Flash Deals Countdown Section (Amazon Lightning Deal Header)
                    item(span = { GridItemSpan(gridColumns) }) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = ZemaroAccentAmber,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIGHTNING DEALS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Ends in ",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ZemaroAccentRose)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = timerString,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Amazon-Style Horizontal Swipeable Deals Carousel (Sliding UI)
                    if (flashDeals.isNotEmpty()) {
                        item(span = { GridItemSpan(gridColumns) }) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(flashDeals, key = { "flash_${it.id}" }) { dealProd ->
                                    Box(modifier = Modifier.width(145.dp)) {
                                        ProductCard(
                                            product = dealProd,
                                            isWishlisted = wishlistIds.contains(dealProd.id),
                                            onWishlistToggle = { viewModel.toggleWishlist(dealProd.id) },
                                            onAddToCart = { viewModel.addToCart(dealProd) },
                                            onClick = { onProductClick(dealProd) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section Heading: Deals & Layout Density Toggle
                    item(span = { GridItemSpan(gridColumns) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Trending Marketplace Drops",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${filteredProducts.size} items",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(end = 4.dp)
                                )

                                // Grid Density Switcher (2 or 3 columns)
                                IconButton(
                                    onClick = { gridColumns = if (gridColumns == 2) 3 else 2 },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (gridColumns == 2) Icons.Default.GridView else Icons.Default.ViewAgenda,
                                        contentDescription = "Toggle Grid Density",
                                        tint = ZemaroPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Compact Product Cards Grid
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishlistIds.contains(product.id),
                        onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                        onAddToCart = { viewModel.addToCart(product) },
                        onClick = { onProductClick(product) }
                    )
                }

                if (filteredProducts.isEmpty()) {
                    item(span = { GridItemSpan(gridColumns) }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No products found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Try searching for earbuds, smartwatch, or sneakers",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Floating 24/7 AI Support Chatbot Trigger Button
        FloatingActionButton(
            onClick = { showChatbot = true },
            containerColor = ZemaroPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("ai_support_fab")
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "24/7 AI Customer Support Assistant"
            )
        }
    }

    if (showChatbot) {
        SupportChatbotModal(onDismiss = { showChatbot = false })
    }

    // Owner (+) Add Banner Dialog on Homepage Slider
    if (showAddBannerDialog) {
        var newBannerTitle by remember { mutableStateOf("") }
        var newBannerSubtitle by remember { mutableStateOf("") }
        var newBannerImage by remember { mutableStateOf("") }
        var newBannerBadge by remember { mutableStateOf("EXCLUSIVE DEAL") }
        var newBannerDiscount by remember { mutableStateOf("FLAT 50% OFF") }

        Dialog(onDismissRequest = { showAddBannerDialog = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Add Promotional Banner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Supreme Owner Homepage Slider Control",
                        fontSize = 11.sp,
                        color = ZemaroAccentCyan,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = newBannerTitle,
                        onValueChange = { newBannerTitle = it },
                        label = { Text("Banner Headline") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newBannerSubtitle,
                        onValueChange = { newBannerSubtitle = it },
                        label = { Text("Subtitle") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newBannerImage,
                        onValueChange = { newBannerImage = it },
                        label = { Text("Banner Image URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (newBannerTitle.isNotBlank()) {
                                viewModel.addPromotionalBanner(
                                    title = newBannerTitle,
                                    subtitle = newBannerSubtitle.ifBlank { "Top rated picks on ZEMARO" },
                                    imageUrl = newBannerImage.ifBlank { "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=1000&auto=format&fit=crop&q=80" },
                                    badgeText = newBannerBadge,
                                    discountHighlight = newBannerDiscount,
                                    targetCategory = "All"
                                )
                                showAddBannerDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish to Slider", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Optional Customer Sign-In Modal (Free Guest Browsing)
    if (showCustomerAuthDialog) {
        var inputCustName by remember { mutableStateOf(if (customerName != null && customerName != "Shopper") customerName!! else "") }
        Dialog(onDismissRequest = { showCustomerAuthDialog = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = ZemaroPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Customer Profile (Optional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Browsing on ZEMARO is 100% free and open without signing in.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    OutlinedTextField(
                        value = inputCustName,
                        onValueChange = { inputCustName = it },
                        label = { Text("Your Preferred Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.setCustomerProfile(inputCustName)
                            showCustomerAuthDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                    ) {
                        Text("Save Profile Preferences", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showCustomerAuthDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("Continue as Guest")
                    }
                }
            }
        }
    }
}
