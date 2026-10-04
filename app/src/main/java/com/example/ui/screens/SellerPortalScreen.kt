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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.ZemaroRepository
import com.example.ui.theme.ZemaroAccentAmber
import com.example.ui.theme.ZemaroAccentCyan
import com.example.ui.theme.ZemaroAccentGreen
import com.example.ui.theme.ZemaroAccentRose
import com.example.ui.theme.ZemaroPrimary
import com.example.viewmodel.ZemaroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerPortalScreen(
    viewModel: ZemaroViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val isSellerRegistered by viewModel.isSellerRegistered.collectAsStateWithLifecycle()
    val generatedOtp by viewModel.generatedOtp.collectAsStateWithLifecycle()
    val sellerStoreName by viewModel.sellerStoreName.collectAsStateWithLifecycle()
    val allProducts by viewModel.products.collectAsStateWithLifecycle()

    // Registration inputs
    var regStoreName by remember { mutableStateOf("") }
    var regContact by remember { mutableStateOf("") }
    var showOtpDialog by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }

    // Product Listing Form inputs
    var productTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Electronics") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var productPriceStr by remember { mutableStateOf("999") }
    var productOriginalPriceStr by remember { mutableStateOf("2499") }
    var productStockStr by remember { mutableStateOf("50") }
    var productImageUrl by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productBrand by remember { mutableStateOf("") }

    var productAddedMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Electronics", "Fashion", "Footwear", "Accessories", "Home & Kitchen", "Beauty")

    // Dynamic Live Commission Calculation Engine:
    val priceVal = productPriceStr.toDoubleOrNull() ?: 0.0
    val commissionRate = ZemaroRepository.calculateCommissionRate(priceVal)
    val commissionAmount = ZemaroRepository.calculateCommissionAmount(priceVal)
    val sellerNetPayout = ZemaroRepository.calculateSellerPayout(priceVal)

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("seller_back_button")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ZEMARO Multi-Vendor Portal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isSellerRegistered) {
                // Seller Registration & OTP Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ZemaroAccentAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Register as Verified Seller",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "List products across India with instant OTP verification",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = regStoreName,
                            onValueChange = { regStoreName = it },
                            label = { Text("Store / Brand Name") },
                            placeholder = { Text("e.g. Apex Electronics Hub") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regContact,
                            onValueChange = { regContact = it },
                            label = { Text("Mobile Number / Email") },
                            placeholder = { Text("e.g. 9876543210 or seller@store.com") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val store = regStoreName.ifBlank { "Verified Independent Merchant" }
                                val contact = regContact.ifBlank { "seller@zemaro.com" }
                                val otp = viewModel.requestSellerOtp(store, contact)
                                enteredOtp = otp
                                showOtpDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("request_otp_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                        ) {
                            Text("Request Secure OTP", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Active Seller Status Badge
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ZemaroAccentGreen.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = ZemaroAccentGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Store Verified: $sellerStoreName",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ZemaroAccentGreen
                            )
                            Text(
                                text = "Multi-vendor merchant privileges active • Live automated commission payouts",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Add New Product Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = ZemaroPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "List New Product on ZEMARO",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = productTitle,
                            onValueChange = { productTitle = it },
                            label = { Text("Product Title (e.g. Wireless ANC Earbuds)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            selectedCategory = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = productPriceStr,
                                onValueChange = { productPriceStr = it },
                                label = { Text("Selling Price (₹)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = productOriginalPriceStr,
                                onValueChange = { productOriginalPriceStr = it },
                                label = { Text("M.R.P (₹)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live Automated Commission Calculation Engine Card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = ZemaroAccentCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Automated Commission Engine",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = ZemaroAccentCyan
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Customer Price:", fontSize = 12.sp)
                                    Text("₹${priceVal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("ZEMARO Platform Fee (${commissionRate.toInt()}%):", fontSize = 12.sp, color = ZemaroAccentAmber)
                                    Text("-₹${commissionAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ZemaroAccentAmber)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Your Net Seller Payout:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZemaroAccentGreen)
                                    Text("₹${sellerNetPayout.toInt()}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ZemaroAccentGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = productStockStr,
                            onValueChange = { productStockStr = it },
                            label = { Text("Available Stock Units") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = productImageUrl,
                            onValueChange = { productImageUrl = it },
                            label = { Text("Image URL (or leave blank for HD preset)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = productDescription,
                            onValueChange = { productDescription = it },
                            label = { Text("Description & Specifications") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (productTitle.isNotBlank() && priceVal > 0) {
                                    viewModel.addSellerProduct(
                                        title = productTitle,
                                        category = selectedCategory,
                                        price = priceVal,
                                        originalPrice = productOriginalPriceStr.toDoubleOrNull() ?: (priceVal * 1.4),
                                        stock = productStockStr.toIntOrNull() ?: 25,
                                        imageUrl = productImageUrl,
                                        description = productDescription.ifBlank { "Exclusive multi-vendor drop listed on ZEMARO Marketplace." },
                                        brand = productBrand.ifBlank { sellerStoreName }
                                    )
                                    productAddedMessage = "Product '$productTitle' listed successfully on ZEMARO marketplace!"
                                    productTitle = ""
                                    productDescription = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("list_product_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                        ) {
                            Text("Publish Product to Marketplace", fontWeight = FontWeight.Bold)
                        }

                        productAddedMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = msg,
                                color = ZemaroAccentGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Seller Inventory & Supreme Owner Governance Display
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = ZemaroAccentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "My Listed Inventory & Owner Lock Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val myProducts = allProducts.filter {
                            it.sellerName == sellerStoreName || it.brand.equals(sellerStoreName, ignoreCase = true) || it.sellerId.contains(sellerStoreName.hashCode().toString())
                        }

                        if (myProducts.isEmpty()) {
                            Text(
                                text = "No products listed yet by $sellerStoreName. Use the form above to add your first product.",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                myProducts.forEach { myProd ->
                                    val isBlocked = !myProd.isEnabledByOwner || myProd.isLockedByOwner
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isBlocked) ZemaroAccentAmber.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = myProd.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "₹${myProd.price.toInt()}",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            if (isBlocked) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(ZemaroAccentRose.copy(alpha = 0.12f))
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Lock,
                                                            contentDescription = null,
                                                            tint = ZemaroAccentRose,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "🔒 LOCKED BY SUPREME OWNER (MD Meraj Ansari) - Seller override is strictly blocked. Only the Supreme Owner can modify or reactivate this product.",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ZemaroAccentRose
                                                        )
                                                    }
                                                }
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = ZemaroAccentGreen,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Active on Marketplace • Net Payout: ₹${(myProd.price * (100 - myProd.commissionPercent) / 100).toInt()}",
                                                        fontSize = 11.sp,
                                                        color = ZemaroAccentGreen,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Realistic OTP Verification Dialog
    if (showOtpDialog) {
        Dialog(onDismissRequest = { showOtpDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
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
                    Text(
                        text = "Verify Seller Identity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Enter the 6-digit OTP sent to $regContact",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    // Display OTP banner for instant testing
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZemaroAccentCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "OTP Generated: $generatedOtp",
                            fontWeight = FontWeight.Bold,
                            color = ZemaroAccentCyan,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("6-Digit OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (otpError) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Invalid OTP. Try again.", color = Color.Red, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val success = viewModel.verifySellerOtp(enteredOtp)
                            if (success) {
                                showOtpDialog = false
                                otpError = false
                            } else {
                                otpError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("verify_otp_submit_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZemaroAccentGreen)
                    ) {
                        Text("Confirm & Launch Store", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
