package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZemaroAccentAmber
import com.example.ui.theme.ZemaroAccentCyan
import com.example.ui.theme.ZemaroPrimary

@Composable
fun ZemaroLogo(
    modifier: Modifier = Modifier,
    isLarge: Boolean = false,
    showTagline: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val badgeSize = if (isLarge) 68.dp else 36.dp
    val cornerRadius = if (isLarge) 18.dp else 10.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Futuristic Cyber-Glass Monogram Jewel
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .shadow(
                        elevation = if (isLarge) 24.dp else 8.dp,
                        shape = RoundedCornerShape(cornerRadius),
                        ambientColor = ZemaroAccentCyan.copy(alpha = glowAlpha),
                        spotColor = ZemaroAccentAmber.copy(alpha = glowAlpha)
                    )
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E1B4B),
                                Color(0xFF312E81)
                            )
                        )
                    )
                    .border(
                        width = if (isLarge) 1.8.dp else 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                ZemaroAccentAmber,
                                ZemaroAccentCyan,
                                Color(0xFF818CF8),
                                ZemaroAccentAmber
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // High-precision custom cyber-Z geometric glyph
                Canvas(modifier = Modifier.size(if (isLarge) 42.dp else 22.dp)) {
                    val w = size.width
                    val h = size.height

                    // Dynamic Z Path with speed facets
                    val zPath = Path().apply {
                        moveTo(w * 0.15f, h * 0.18f)
                        lineTo(w * 0.85f, h * 0.18f)
                        lineTo(w * 0.90f, h * 0.32f)
                        lineTo(w * 0.42f, h * 0.70f)
                        lineTo(w * 0.85f, h * 0.70f)
                        lineTo(w * 0.85f, h * 0.84f)
                        lineTo(w * 0.15f, h * 0.84f)
                        lineTo(w * 0.10f, h * 0.70f)
                        lineTo(w * 0.58f, h * 0.32f)
                        lineTo(w * 0.15f, h * 0.32f)
                        close()
                    }

                    drawPath(
                        path = zPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                ZemaroAccentAmber,
                                Color(0xFFF43F5E),
                                ZemaroAccentCyan
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(w, h)
                        )
                    )
                }

                // Sparkle jewel micro-accent
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(if (isLarge) 6.dp else 3.dp)
                        .size(if (isLarge) 8.dp else 4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.width(if (isLarge) 14.dp else 8.dp))

            // Premium Cyber Typography
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ZEMARO",
                        fontWeight = FontWeight.Black,
                        fontSize = if (isLarge) 34.sp else 21.sp,
                        letterSpacing = if (isLarge) 4.sp else 1.8.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.SansSerif
                    )

                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Next-Gen",
                        tint = ZemaroAccentAmber,
                        modifier = Modifier
                            .size(if (isLarge) 22.dp else 15.dp)
                            .padding(start = 3.dp)
                    )
                }

                if (!isLarge) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(ZemaroAccentCyan)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SMART MARKETPLACE",
                            fontSize = 7.8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.4.sp,
                            color = ZemaroAccentCyan
                        )
                    }
                }
            }
        }

        if (isLarge && showTagline) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .width(28.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, ZemaroAccentCyan)))
                )
                Text(
                    text = "  PARAGON OF NEXT-GEN COMMERCE  ",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = ZemaroAccentCyan
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .width(28.dp)
                        .background(Brush.horizontalGradient(listOf(ZemaroAccentCyan, Color.Transparent)))
                )
            }
        }
    }
}
