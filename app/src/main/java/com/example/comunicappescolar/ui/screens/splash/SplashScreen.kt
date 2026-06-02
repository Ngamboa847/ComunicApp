package com.example.comunicappescolar.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.comunicappescolar.ui.navigation.AppRoutes
import kotlinx.coroutines.delay

// ── Colores del Splash ───────────────────────────────────────
private val SplashBlue      = Color(0xFF1565C0)
private val SplashBlueDark  = Color(0xFF0D3F7A)
private val SplashBlueLight = Color(0xFF1976D2)
private val White           = Color(0xFFFFFFFF)

@Composable
fun SplashScreen(navController: NavController) {

    // ── Animación de escala al entrar ────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        delay(3000L) // 3 segundos como en el HTML
        navController.navigate(AppRoutes.Onboarding.route) {
            popUpTo(AppRoutes.Splash.route) { inclusive = true }
        }
    }

    val scale by animateFloatAsState(
        targetValue  = if (visible) 1f else 0.95f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label        = "splashScale"
    )

    // ── Barra de progreso ────────────────────────────────────
    val progressAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progressAnim.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(SplashBlueLight, SplashBlueDark),
                    radius = 1200f
                )
            )
    ) {
        // ── Luces atmosféricas de fondo ──────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .blur(120.dp)
                .background(
                    color = SplashBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50)
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .blur(120.dp)
                .background(
                    color = SplashBlueLight.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50)
                )
        )

        // ── Contenido principal ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(Modifier.weight(1f))

            // ── Logo + Textos ────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Caja del logo con efecto glassmorphism
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model             = "https://lh3.googleusercontent.com/aida-public/AB6AXuD4WWvt07kkb7ZEROMDWtBp8w00ZAqKvVWIM1eEA1ArZPBtd_024NaiQ5sPo_J8xP7hbWNUjSVHbgIAnM4frbDs1xbgZzUgB_lgiA8ZOAaZ4oZv1gTZ8nkqXzI1y7YMqHIVYHDzPE49R64FF1wwtlD-gu5v5-C6IhLCjNUpzN4UEQxdf4FJspedFvahSqT8oNb0mzDSZY18pX1II2mFhqlv8-45h1L2l5e6lZttuZXj6qMpv-w2HSJ9Q3CTmlX7duJgER6K24MXJz0",
                        contentDescription = "Logo Colegio",
                        contentScale       = ContentScale.Fit,
                        modifier           = Modifier
                            .size(84.dp)
                            .padding(8.dp)
                    )
                }

                // Título
                Text(
                    text       = "ComunicApp Escolar",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = White,
                    textAlign  = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )

                // Subtítulo
                Text(
                    text       = "Conectando la escuela y la familia",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color      = White.copy(alpha = 0.8f),
                    textAlign  = TextAlign.Center
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Imagen ilustrativa ───────────────────────────
            AsyncImage(
                model             = "https://lh3.googleusercontent.com/aida-public/AB6AXuAMvtJCXgd9Dt4eAyHc40npXqPaT1LiwpUA2Yjuwk6y68KhXapnf3YZgjkM4YO2dX5OPgeKQZzEWPeSYsOM-OAYi8PneHN9FMu4opRbrjsOdKrXIF2pR-pu9Ky5EdvlWj1h2p01dxCn6H3yMLQfXZ6yeeqKBF6_l_qEJyma-PqOaicNh8NxRTEHMiCLffswxaXaTpGWO67UyNecQjhC_8aGEZr3D3TQRLNkNOLOtisRWplujABLgshTpigPD7TTx2YLyieDXkiT4SI",
                contentDescription = "Familia de estudiantes",
                contentScale       = ContentScale.FillWidth,
                modifier           = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.weight(1f))

            // ── Sección inferior: barra de progreso ──────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Barra de progreso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressAnim.value)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(White)
                    )
                }

                // Texto inferior
                Text(
                    text      = "CARGANDO ENTORNO EDUCATIVO",
                    fontSize  = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color     = White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}