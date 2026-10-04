package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    val color: Color,
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFF6366F1), // Indigo
            Color(0xFFF59E0B), // Amber Gold
            Color(0xFF06B6D4), // Cyan
            Color(0xFF10B981), // Emerald
            Color(0xFFEC4899), // Pink
            Color(0xFFF43F5E), // Rose
            Color(0xFF8B5CF6)  // Purple
        )
        List(100) {
            ConfettiParticle(
                color = colors[Random.nextInt(colors.size)],
                startX = Random.nextFloat() * 1000f,
                startY = -50f - Random.nextFloat() * 300f,
                velocityX = (Random.nextFloat() - 0.5f) * 450f,
                velocityY = 700f + Random.nextFloat() * 800f,
                size = 12f + Random.nextFloat() * 16f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        val alpha = if (t > 0.7f) (1f - (t - 0.7f) / 0.3f).coerceIn(0f, 1f) else 1f

        particles.forEach { p ->
            val currentX = (p.startX + p.velocityX * t).mod(size.width)
            val currentY = p.startY + p.velocityY * t
            val rotation = p.rotationSpeed * t

            if (currentY in 0f..size.height) {
                rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size / 2,
                            center = Offset(currentX, currentY)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(currentX - p.size / 2, currentY - p.size / 3),
                            size = Size(p.size, p.size * 0.6f)
                        )
                    }
                }
            }
        }
    }
}
