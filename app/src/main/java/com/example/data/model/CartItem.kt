package com.example.data.model

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "zemaro_ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
