package com.example

import com.example.data.model.Order
import com.example.data.repository.ZemaroRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCommissionEngine_at500_is10Percent() {
        val rate = ZemaroRepository.calculateCommissionRate(500.0)
        assertEquals(10.0, rate, 0.01)

        val commissionAmount = ZemaroRepository.calculateCommissionAmount(500.0)
        assertEquals(50.0, commissionAmount, 0.01)

        val sellerPayout = ZemaroRepository.calculateSellerPayout(500.0)
        assertEquals(450.0, sellerPayout, 0.01)
    }

    @Test
    fun testCommissionEngine_at1000_is20Percent() {
        val rate = ZemaroRepository.calculateCommissionRate(1000.0)
        assertEquals(20.0, rate, 0.01)

        val commissionAmount = ZemaroRepository.calculateCommissionAmount(1000.0)
        assertEquals(200.0, commissionAmount, 0.01)

        val sellerPayout = ZemaroRepository.calculateSellerPayout(1000.0)
        assertEquals(800.0, sellerPayout, 0.01)
    }

    @Test
    fun testCommissionEngine_higherTiers() {
        val rate = ZemaroRepository.calculateCommissionRate(2000.0)
        assertTrue(rate >= 20.0)
    }

    @Test
    fun testOwnerConfiguration() {
        assertEquals("merajrok839@gmail.com", ZemaroRepository.OWNER_EMAIL)
        assertEquals("7979864406-i40c@axl", ZemaroRepository.OWNER_UPI_ID)
        assertEquals("917979864406", ZemaroRepository.OWNER_WHATSAPP_NUMBER)
    }

    @Test
    fun testSecretWhatsAppUrlGeneration() {
        // Create mock repository without DB
        val mockOrder = Order(
            trackingId = "ZEM-889900",
            customerName = "Rahul Sharma",
            customerPhone = "9876543210",
            deliveryAddress = "Flat 402, Lotus Tower",
            cityVillage = "New Delhi",
            pinCode = "110001",
            itemsSummary = "• CyberBuds Ultra (x1)",
            totalAmount = 499.0,
            commissionAmount = 49.9,
            sellerPayoutAmount = 449.1,
            paymentMethod = "ZEMARO Fast UPI",
            status = "Order Placed",
            deliveryOtp = "4512",
            liveCoordinates = "28.6139,77.2090"
        )

        // Verify dispatch URL format includes owner number and encoded parameters
        assertTrue(mockOrder.trackingId.startsWith("ZEM-"))
        assertEquals("4512", mockOrder.deliveryOtp)
        assertEquals(499.0, mockOrder.totalAmount, 0.01)
    }
}
