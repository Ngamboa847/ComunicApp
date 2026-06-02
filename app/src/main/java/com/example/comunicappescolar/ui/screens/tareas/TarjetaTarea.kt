package com.example.comunicappescolar.ui.screens.tareas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comunicappescolar.data.model.Tarea

// ── Colores ──────────────────────────────────────────────────
private val Blue900    = Color(0xFF004D99)
private val Blue800    = Color(0xFF1565C0)
private val BlueFixed  = Color(0xFFD6E3FF)
private val Gray500    = Color(0xFF727783)
private val Gray200    = Color(0xFFC2C6D4)
private val ErrorRed   = Color(0xFFBA1A1A)
private val ErrorCont  = Color(0xFFFFDAD6)
private val GreenDark  = Color(0xFF25752B)
private val GreenLight = Color(0xFFA3F69C)
private val White      = Color.White

@Composable
fun TarjetaTarea(tarea: Tarea, onRevisar: (Boolean) -> Unit) {

    // Config visual por estado
    val colorBorde: Color
    val colorBadgeFondo: Color
    val colorBadgeTexto: Color
    val labelEstado: String
    val colorFecha: Color

    when (tarea.estado_usuario) {
        "entregada" -> {
            colorBorde      = GreenDark
            colorBadgeFondo = GreenLight
            colorBadgeTexto = Color(0xFF002204)
            labelEstado     = "ENTREGADA"
            colorFecha      = Gray500
        }
        "vencida" -> {
            colorBorde      = ErrorRed
            colorBadgeFondo = ErrorCont
            colorBadgeTexto = Color(0xFF93000A)
            labelEstado     = "VENCIDA"
            colorFecha      = ErrorRed
        }
        else -> {
            colorBorde      = Blue800
            colorBadgeFondo = BlueFixed
            colorBadgeTexto = Blue900
            labelEstado     = "PENDIENTE"
            colorFecha      = Blue900
        }
    }

    val revisada      = (tarea.estado_usuario == "vista" || tarea.estado_usuario == "entregada")
    val esPendiente   = tarea.estado_usuario == "pendiente"
    val colorFondoCard by animateColorAsState(
        targetValue   = if (revisada) Color(0xFFF9F9FF) else White,
        animationSpec = tween(300), label = "fondoTarea"
    )

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = colorFondoCard),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (esPendiente) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Borde lateral de color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        color = colorBorde,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                // Badge estado + fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorBadgeFondo)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(labelEstado, fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorBadgeTexto, letterSpacing = 0.8.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (tarea.estado_usuario) {
                                "entregada" -> Icons.Default.CheckCircle
                                "vencida"   -> Icons.Default.CalendarMonth
                                else        -> Icons.Default.Schedule
                            },
                            contentDescription = null,
                            tint     = colorFecha,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = tarea.fecha_limite ?: "Sin fecha",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = colorFecha
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Título
                Text(
                    text       = tarea.titulo,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF191C21),
                )

                Spacer(Modifier.height(4.dp))

                // Materia y grado
                Text(
                    text       = "${tarea.materia ?: "Sin materia"} - ${tarea.grado ?: ""}",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Blue800
                )

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Gray200.copy(alpha = 0.5f))
                Spacer(Modifier.height(10.dp))

                // Checkbox revisión del padre + flecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onRevisar(!revisada) }
                    ) {
                        Checkbox(
                            checked         = revisada,
                            onCheckedChange = { onRevisar(it) },
                            colors          = CheckboxDefaults.colors(
                                checkedColor   = Blue800,
                                uncheckedColor = Gray200
                            )
                        )
                        Text(
                            text = "Revisión del padre",
                            fontSize = 13.sp,
                            color = if (revisada) Blue800 else Gray500,
                            fontWeight = if (revisada) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                    Icon(Icons.Default.ChevronRight, null,
                        tint = Gray200, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
