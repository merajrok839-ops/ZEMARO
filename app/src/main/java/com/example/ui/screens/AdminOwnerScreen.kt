package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BannerAd
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.repository.ZemaroRepository
import com.example.ui.theme.ZemaroAccentAmber
import com.example.ui.theme.ZemaroAccentCyan
import com.example.ui.theme.ZemaroAccentGreen
import com.example.ui.theme.ZemaroAccentRose
import com.example.ui.theme.ZemaroPrimary
import com.example.viewmodel.ZemaroViewModel

@Composable
fun AdminOwnerScreen(
    viewModel: ZemaroViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val isOwnerLoggedIn by viewModel.isOwnerLoggedIn.collectAsStateWithLifecycle()
    val allBanners by viewModel.allBanners.collectAsStateWithLifecycle()
    val allOrders by viewModel.orders.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Banner Add Inputs
    var bannerTitle by remember { mutableStateOf("") }
    var bannerSubtitle by remember { mutableStateOf("") }
    var bannerImageUrl by remember { mutableStateOf("") }
    var bannerBadge by remember { mutableStateOf("FLASH DEAL") }
    var bannerDiscount by remember { mutableStateOf("UP TO 70% OFF") }
    var bannerCategory by remember { mutableStateOf("Electronics") }

    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var newPriceText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Owner Master Admin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isOwnerLoggedIn) {
                IconButton(onClick = { viewModel.logoutOwner() }) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Logout",
                        tint = ZemaroAccentRose
                    )
                }
            }
        }

        if (!isOwnerLoggedIn) {
            val ownerSecurityOtp by viewModel.ownerSecurityOtp.collectAsStateWithLifecycle()
            val isOwnerOtpSent by viewModel.isOwnerOtpSent.collectAsStateWithLifecycle()
            var enteredOtp by remember { mutableStateOf("") }
            var otpError by remember { mutableStateOf(false) }

            // Generic Clean Administrative & Executive Sign-In
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(ZemaroPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ZemaroPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Executive & Merchant Portal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Enter authorized administrative email for 2-step verification",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Authorized Email Address") },
                    placeholder = { Text("admin@organization.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!isOwnerOtpSent) {
                    Button(
                        onClick = {
                            val otp = viewModel.requestOwnerSecurityOtp(emailInput)
                            if (otp != null) {
                                enteredOtp = otp // Convenient auto-fill for testing
                                loginError = false
                            } else {
                                loginError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("request_owner_otp_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send 6-Digit Access Code", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // 6-digit OTP verification field
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("6-Digit Security Verification Code") },
                        placeholder = { Text("Enter 6-digit OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (otpError) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Invalid verification code. Please check your security key.",
                            color = Color.Red,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val success = viewModel.verifyOwnerSecurityOtp(enteredOtp)
                            if (success) {
                                otpError = false
                            } else {
                                otpError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_login_submit_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZemaroAccentGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify & Unlock Dashboard", fontWeight = FontWeight.Bold)
                    }
                }

                if (loginError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Access Denied: Unrecognized organizational administrative email address.",
                        color = Color.Red,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            // Master Owner Privileges unlocked
            val totalRevenue = allOrders.sumOf { it.totalAmount }
            val totalCommission = allOrders.sumOf { it.commissionAmount }

            // Owner Banner & Privileges Bar
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = ZemaroAccentGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Owner: ${ZemaroRepository.OWNER_NAME}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Linked UPI: ${ZemaroRepository.OWNER_UPI_ID} (Private Backend Gateway)",
                            fontSize = 10.5.sp,
                            color = ZemaroAccentCyan
                        )
                    }
                }
            }

            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Total Volume", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${totalRevenue.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Commission Fee", fontSize = 10.sp, color = ZemaroAccentAmber)
                        Text("₹${totalCommission.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ZemaroAccentAmber)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Orders", fontSize = 10.sp, color = ZemaroAccentGreen)
                        Text("${allOrders.size}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ZemaroAccentGreen)
                    }
                }
            }

            // Tab navigation: [Promotional Banners], [Incoming Orders], [Products]
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Banner Ads (${allBanners.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Live Orders (${allOrders.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Products (${products.size})", fontSize = 12.sp) }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    // Ad Banner Management (Add, view, toggle active, delete)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Add Promotional Homepage Banner",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = bannerTitle,
                                        onValueChange = { bannerTitle = it },
                                        label = { Text("Headline Title") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = bannerSubtitle,
                                        onValueChange = { bannerSubtitle = it },
                                        label = { Text("Subtitle / Promo Text") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = bannerImageUrl,
                                        onValueChange = { bannerImageUrl = it },
                                        label = { Text("Banner Image URL") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = bannerBadge,
                                            onValueChange = { bannerBadge = it },
                                            label = { Text("Badge") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        OutlinedTextField(
                                            value = bannerDiscount,
                                            onValueChange = { bannerDiscount = it },
                                            label = { Text("Discount Pill") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            if (bannerTitle.isNotBlank()) {
                                                viewModel.addPromotionalBanner(
                                                    title = bannerTitle,
                                                    subtitle = bannerSubtitle.ifBlank { "Exclusive deals on ZEMARO" },
                                                    imageUrl = bannerImageUrl.ifBlank { "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=1000&auto=format&fit=crop&q=80" },
                                                    badgeText = bannerBadge,
                                                    discountHighlight = bannerDiscount,
                                                    targetCategory = bannerCategory
                                                )
                                                bannerTitle = ""
                                                bannerSubtitle = ""
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                                    ) {
                                        Text("Publish Banner to Homepage")
                                    }
                                }
                            }
                        }

                        items(allBanners) { banner ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = banner.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = banner.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = if (banner.active) "Status: LIVE ON HOME" else "Status: INACTIVE",
                                            fontSize = 10.sp,
                                            color = if (banner.active) ZemaroAccentGreen else Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row {
                                        IconButton(onClick = { viewModel.toggleBannerActive(banner) }) {
                                            Icon(
                                                imageVector = Icons.Default.Campaign,
                                                contentDescription = "Toggle",
                                                tint = if (banner.active) ZemaroAccentGreen else Color.Gray
                                            )
                                        }

                                        IconButton(onClick = { viewModel.deleteBanner(banner.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = ZemaroAccentRose
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Incoming Orders & Delivery OTP Verification Panel
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allOrders) { order ->
                            var enteredOtpForOrder by remember { mutableStateOf("") }
                            var otpMsg by remember { mutableStateOf<String?>(null) }

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "#${order.trackingId}",
                                            fontWeight = FontWeight.Black,
                                            color = ZemaroPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Status: ${order.status}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (order.status == "Delivered") ZemaroAccentGreen else ZemaroAccentAmber,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Customer: ${order.customerName} (${order.customerPhone})", fontSize = 12.sp)
                                    Text(text = "Address: ${order.deliveryAddress}, ${order.cityVillage} (${order.pinCode})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Total: ₹${order.totalAmount.toInt()} | Platform Fee: ₹${order.commissionAmount.toInt()} | Payout: ₹${order.sellerPayoutAmount.toInt()}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = ZemaroAccentCyan)

                                    if (order.status != "Delivered") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = enteredOtpForOrder,
                                                onValueChange = { enteredOtpForOrder = it },
                                                placeholder = { Text("Customer OTP (${order.deliveryOtp})", fontSize = 11.sp) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(46.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                singleLine = true
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.verifyDeliveryOtp(
                                                        order.id,
                                                        enteredOtpForOrder,
                                                        onSuccess = { otpMsg = "Delivered Verified!" },
                                                        onError = { otpMsg = "Incorrect OTP!" }
                                                    )
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = ZemaroAccentGreen)
                                            ) {
                                                Text("Verify Delivery")
                                            }
                                        }

                                        otpMsg?.let { msg ->
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(msg, fontSize = 11.sp, color = if (msg.contains("Verified")) ZemaroAccentGreen else Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Supreme Owner Master Product Management & Overrides
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = ZemaroAccentAmber.copy(alpha = 0.12f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = ZemaroAccentAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Supreme Owner Product Authority",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = ZemaroAccentAmber
                                        )
                                        Text(
                                            text = "Master switches override all sellers. Locked/Disabled items are instantly blocked across the app.",
                                            fontSize = 10.5.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        items(products, key = { it.id }) { prod ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = prod.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Seller: ${prod.sellerName} • Brand: ${prod.brand}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Price: ₹${prod.price.toInt()} | Platform Fee: ${prod.commissionPercent}% (₹${(prod.price * prod.commissionPercent / 100).toInt()})",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ZemaroAccentCyan
                                            )
                                        }

                                        IconButton(onClick = { viewModel.deleteProduct(prod.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = ZemaroAccentRose,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Status badges
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (prod.isEnabledByOwner) ZemaroAccentGreen.copy(alpha = 0.15f)
                                                    else ZemaroAccentRose.copy(alpha = 0.15f)
                                                )
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = if (prod.isEnabledByOwner) "LIVE ON MARKETPLACE" else "DISABLED BY OWNER",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (prod.isEnabledByOwner) ZemaroAccentGreen else ZemaroAccentRose
                                            )
                                        }

                                        if (prod.isLockedByOwner) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(ZemaroAccentAmber.copy(alpha = 0.15f))
                                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "🔒 SELLER OVERRIDE BLOCKED",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ZemaroAccentAmber
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Supreme Control Switches & Price Override
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Marketplace Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = if (prod.isEnabledByOwner) "Customer browsing ON" else "Hidden from shoppers",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Switch(
                                            checked = prod.isEnabledByOwner,
                                            onCheckedChange = { isChecked ->
                                                viewModel.ownerToggleProductStatus(prod.id, isChecked)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = ZemaroAccentGreen
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Seller Lock Switch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = if (prod.isLockedByOwner) "Seller blocked from changes" else "Seller edits allowed",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Switch(
                                            checked = prod.isLockedByOwner,
                                            onCheckedChange = { isLocked ->
                                                viewModel.ownerToggleProductLock(prod.id, isLocked)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = ZemaroAccentAmber
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Price Override Button
                                    Button(
                                        onClick = {
                                            editingProduct = prod
                                            newPriceText = prod.price.toInt().toString()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Override Price (Current: ₹${prod.price.toInt()})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Price Override Dialog
            editingProduct?.let { productToEdit ->
                androidx.compose.ui.window.Dialog(onDismissRequest = { editingProduct = null }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
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
                                text = "Owner Price Override",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = productToEdit.title,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = newPriceText,
                                onValueChange = { newPriceText = it },
                                label = { Text("New Override Price (₹)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val parsed = newPriceText.toDoubleOrNull()
                                    if (parsed != null && parsed > 0) {
                                        viewModel.ownerOverridePrice(productToEdit.id, parsed)
                                        editingProduct = null
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                            ) {
                                Text("Save & Apply Price Override", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
