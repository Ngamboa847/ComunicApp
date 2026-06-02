package com.example.comunicappescolar.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.comunicappescolar.ui.navigation.AppRoutes
import kotlinx.coroutines.launch

// ── Colores ──────────────────────────────────────────────────
private val Blue800  = Color(0xFF1565C0)
private val Blue900  = Color(0xFF004D99)
private val Blue50   = Color(0xFFE3F2FD)
private val Gray500  = Color(0xFF727783)

// ── Modelo de cada paso del onboarding ──────────────────────
data class OnboardingStep(
    val imageUrl:    String,
    val titulo:      String,
    val descripcion: String
)

val pasos = listOf(
    OnboardingStep(
        imageUrl    = "https://lh3.googleusercontent.com/aida-public/AB6AXuB1PNmGoRj35ZL6ov7bIFn5XLF-nzajKHaCKJu4yURlJRiiGfB9maaT5ZIqjPdO18NnShBky809NnvZd77SDjtZhzy5ahhJ_2_4JS5hnXRFX2mqpyByl3TNYkh2P5wbnBENTS8_mHiRGcvblwtd0s3eDkjw6BHewh9WMkAr9RAGNEfX6A3wxiGylpuQ34R6PSmTfjP568piYt_VZFdD85zNGHr0FbmhTIUWw1Vxw7-UPavrjTf5bCXrV2aICA6lbOs6lN28NE_wqkQ",
        titulo      = "Mantente informado",
        descripcion = "Recibe avisos escolares importantes y actualizaciones administrativas al instante en tu smartphone."
    ),
    OnboardingStep(
        imageUrl    = "https://lh3.googleusercontent.com/aida-public/AB6AXuDfOaUWqCllnvCmJ2Mzr0eDRLsI1iMhdtzC2Hb6EAeRys-92vAIqDFuKQ-8iJ032LdSaILS4ow21pt5NvypXcsZp6sXEWlZnq8wwOuuIpTq5xRGkOY8cIGB8eHvZ54l5OePiW1Zl8wezHRZoGSFkVvz41FO6JUMnz3AgrRxEI3dtAzD3QJhXeljPKw9pqYGu-o3EjeS7xVFyRQjruj_jWX8phtKyvV1z-mh4qjQVwD_Uzwh-EwHKMRu9edhd7V3tkippk2J4DGzV8k",
        titulo      = "Comunicación directa",
        descripcion = "Conéctate con profesores y personal administrativo a través de nuestra plataforma de mensajería segura."
    ),
    OnboardingStep(
        imageUrl    = "https://lh3.googleusercontent.com/aida-public/AB6AXuBnVHk4vj2WW0JkNs38dz1LemabL9RzBYhuhbm2kcjet8nsD65gnnRSvq9XJmcaspUGELwnxh-cXofeCWUcuRlt_Nu8ta1BnZMS7fpqiPD4O3z1YN8z_X1RdHOtDKexzEPJl77cOo3RmPTvptN4rZuwWfTpynGT8zE2UUZc5wLSliV9BpuHHfyu15my2jDB6a1zY8YEtpZ0zNKl-2MSvb1nt-wHBXh0gYTZCzLtHxVQZhlF1l179bWyggqN70YPvBKfB5GeD_QIOHg",
        titulo      = "Sigue el progreso",
        descripcion = "Monitorea la asistencia, calificaciones y logros académicos en tiempo real durante todo el año escolar."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState  = rememberPagerState(pageCount = { pasos.size })
    val scope       = rememberCoroutineScope()
    val ultimaPagina = pagerState.currentPage == pasos.size - 1

    fun navegar() = navController.navigate(AppRoutes.Login.route) {
        popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue50)
            .statusBarsPadding()
    ) {
        // ── Top App Bar ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "ComunicApp Escolar",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = Blue900
            )
            TextButton(onClick = { navegar() }) {
                Text(
                    text     = "Saltar",
                    fontSize = 12.sp,
                    color    = Gray500,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // ── Carousel con HorizontalPager ─────────────────────
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val paso = pasos[page]
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Imagen del paso
                AsyncImage(
                    model              = paso.imageUrl,
                    contentDescription = paso.titulo,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(Modifier.height(24.dp))

                // Tarjeta de texto glass
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(32.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier            = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = paso.titulo,
                            fontSize   = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Blue900,
                            textAlign  = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text      = paso.descripcion,
                            fontSize  = 15.sp,
                            color     = Gray500,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // ── Footer: dots + botón ─────────────────────────────
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .background(Blue50)
                .navigationBarsPadding()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Dots indicadores
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pasos.size) { index ->
                    val esActivo = pagerState.currentPage == index

                    val ancho by animateDpAsState(
                        targetValue   = if (esActivo) 24.dp else 8.dp,
                        animationSpec = tween(300),
                        label         = "dotWidth"
                    )
                    val color by animateColorAsState(
                        targetValue   = if (esActivo) Blue900 else Gray500.copy(alpha = 0.3f),
                        animationSpec = tween(300),
                        label         = "dotColor"
                    )

                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(ancho)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Botón Siguiente / Comenzar
            Button(
                onClick = {
                    if (ultimaPagina) {
                        navegar()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape  = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Blue900)
            ) {
                Text(
                    text       = if (ultimaPagina) "Comenzar" else "Siguiente",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector        = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}