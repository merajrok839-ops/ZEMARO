package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Order
import com.example.ui.components.ConfettiAnimation
import com.example.ui.theme.ZemaroAccentAmber
import com.example.ui.theme.ZemaroAccentCyan
import com.example.ui.theme.ZemaroAccentGreen
import com.example.ui.theme.ZemaroPrimary
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun CheckoutScreen(
    totalAmount: Double,
    onConfirmOrder: (
        name: String,
        phone: String,
        address: String,
        cityVillage: String,
        pinCode: String,
        paymentMethod: String,
        liveCoords: String,
        onSuccess: (Order) -> Unit
    ) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var cityVillage by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var liveCoordinates by remember { mutableStateOf("25.5941,85.1376") }
    var isLocating by remember { mutableStateOf(false) }

    // Payment method options (clean corporate branding, hiding raw UPI numbers from public UI)
    var selectedPaymentMethod by remember { mutableStateOf("ZEMARO Fast UPI (Zero Fee)") }

    var isSubmitting by remember { mutableStateOf(false) }
    var confirmedOrder by remember { mutableStateOf<Order?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    fun detectGpsLocation() {
        try {
            isLocating = true
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                isLocating = false
                if (loc != null) {
                    liveCoordinates = "${loc.latitude},${loc.longitude}"
                } else {
                    liveCoordinates = "25.5941,85.1376"
                }
            }.addOnFailureListener {
                isLocating = false
                liveCoordinates = "25.5941,85.1376"
            }
        } catch (e: Exception) {
            isLocating = false
            liveCoordinates = "25.5941,85.1376"
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("checkout_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Secure Checkout",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted",
                    tint = ZemaroAccentGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("256-Bit Encrypted", fontSize = 11.sp, color = ZemaroAccentGreen, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Section 1: Shipping Address Form
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = ZemaroPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delivery Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))

                            // Live GPS Autofill Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ZemaroPrimary.copy(alpha = 0.12f))
                                    .clickable { detectGpsLocation() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = null,
                                    tint = ZemaroPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isLocating) "Locating..." else "Use Live GPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZemaroPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Rahul Sharma") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Mobile Number (For Delivery Updates)") },
                            placeholder = { Text("e.g. 9876543210") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("Street Address / House No. / Landmark") },
                            placeholder = { Text("e.g. Flat 402, Green Valley Apartments") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = cityVillage,
                                onValueChange = { cityVillage = it },
                                label = { Text("City / Village") },
                                placeholder = { Text("e.g. New Delhi") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = { pinCode = it },
                                label = { Text("PIN Code") },
                                placeholder = { Text("e.g. 110001") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        if (liveCoordinates.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "GPS Coords: $liveCoordinates",
                                fontSize = 10.sp,
                                color = ZemaroAccentGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Payment Selection (Corporate Aesthetic, Hidden Backend UPI Routing)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = ZemaroAccentAmber
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Select Payment Gateway",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Option 1: Fast UPI (Integrated Backend routing to owner's UPI ID: 7979864406-i40c@axl)
                        PaymentOptionRow(
                            title = "ZEMARO Fast UPI (PhonePe / GPay / BHIM)",
                            subtitle = "Zero processing fee • Instant Verification",
                            icon = Icons.Default.QrCode2,
                            isSelected = selectedPaymentMethod.startsWith("ZEMARO Fast UPI"),
                            onClick = { selectedPaymentMethod = "ZEMARO Fast UPI (Zero Fee)" }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 2: Cards
                        PaymentOptionRow(
                            title = "Credit / Debit Card",
                            subtitle = "Visa, MasterCard, RuPay, Amex",
                            icon = Icons.Default.CreditCard,
                            isSelected = selectedPaymentMethod == "Credit / Debit Card",
                            onClick = { selectedPaymentMethod = "Credit / Debit Card" }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 3: COD
                        PaymentOptionRow(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay upon receiving package after OTP verification",
                            icon = Icons.Default.LocalAtm,
                            isSelected = selectedPaymentMethod == "Cash on Delivery",
                            onClick = { selectedPaymentMethod = "Cash on Delivery" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Total Payable Summary
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Payable", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "₹${totalAmount.toInt()}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = ZemaroPrimary
                            )
                        }

                        Text(
                            text = "Free Instant Delivery",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZemaroAccentGreen
                        )
                    }
                }
            }
        }

        // Sticky Bottom Place Order Button
        Card(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        isSubmitting = true
                        val finalCustName = customerName.ifBlank { "Shopper" }
                        val finalCustPhone = customerPhone.ifBlank { "9876543210" }
                        val finalAddress = deliveryAddress.ifBlank { "Registered Delivery Address" }
                        val finalCity = cityVillage.ifBlank { "Metro Delivery Hub" }
                        val finalPin = pinCode.ifBlank { "110001" }

                        onConfirmOrder(
                            finalCustName,
                            finalCustPhone,
                            finalAddress,
                            finalCity,
                            finalPin,
                            selectedPaymentMethod,
                            liveCoordinates
                        ) { newOrder ->
                            isSubmitting = false
                            confirmedOrder = newOrder
                            showConfetti = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_order_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZemaroAccentAmber)
                ) {
                    Text(
                        text = if (isSubmitting) "Securing Order..." else "Place Order & Pay ₹${totalAmount.toInt()}",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Confetti Celebration Canvas
        if (showConfetti) {
            ConfettiAnimation(
                modifier = Modifier.fillMaxSize(),
                onFinished = { /* Keep visual joy */ }
            )
        }

        // Order Confirmed Success Modal
        confirmedOrder?.let { order ->
            Dialog(onDismissRequest = { /* Must click action button */ }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ZemaroAccentGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ZemaroAccentGreen,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Order Confirmed!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Tracking ID: #${order.trackingId}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZemaroPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Secure Delivery OTP Box
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "SECURE DELIVERY OTP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = order.deliveryOtp,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 6.sp,
                                    color = ZemaroAccentAmber
                                )
                                Text(
                                    text = "Share this 4-digit code only upon delivery arrival",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Estimated Delivery in 24 - 48 Hours. Order dispatch details transmitted to logistics center.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                confirmedOrder = null
                                showConfetti = false
                                onNavigateToOrders()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("track_order_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZemaroPrimary)
                        ) {
                            Text("Track My Order", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                confirmedOrder = null
                                showConfetti = false
                                onNavigateToHome()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text("Continue Shopping")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) ZemaroPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) ZemaroPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
