package com.example.comunicappescolar.ui.screens.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)
private val GreenMid  = Color(0xFF2E7D32)
private val SecColor  = Color(0xFF54A0FE)
private val White     = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEventoSheet(viewModel: CalendarioViewModel, onDismiss: () -> Unit) {
    val crearState by viewModel.crearState.collectAsState()

    LaunchedEffect(crearState) {
        if (crearState is CrearEventoUiState.Success) {
            viewModel.resetCrearState()
            onDismiss()
        }
    }

    val tipos = listOf(
        "academico"  to "Académico",
        "deportivo"  to "Deportivo",
        "reunion"    to "Reunión",
        "cultural"   to "Cultural"
    )

    val colorTipo = when (viewModel.nuevoTipo) {
        "deportivo" -> GreenMid
        "reunion"   -> SecColor
        "cultural"  -> Color(0xFF7C3AED)
        else        -> Blue900
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp).height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Gray200)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Encabezado
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nuevo Evento", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    Text("Agrega un evento al calendario escolar",
                        fontSize = 13.sp, color = Gray500)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Gray500)
                }
            }

            HorizontalDivider(color = Gray200)

            // Selector de tipo
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tipo de evento", fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold, color = Gray500)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tipos.forEach { (clave, etiqueta) ->
                        val sel = viewModel.nuevoTipo == clave
                        val color = when (clave) {
                            "deportivo" -> GreenMid
                            "reunion"   -> SecColor
                            "cultural"  -> Color(0xFF7C3AED)
                            else        -> Blue900
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) color.copy(.12f) else Gray200.copy(.5f))
                                .clickable { viewModel.nuevoTipo = clave }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(etiqueta, fontSize = 11.sp,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                color = if (sel) color else Gray500)
                        }
                    }
                }
            }

            // Título
            CampoEvento(
                valor       = viewModel.nuevoTitulo,
                onChange    = { viewModel.nuevoTitulo = it },
                label       = "Título del evento",
                placeholder = "Ej: Reunión de padres",
                icono       = Icons.Default.Event,
                error       = viewModel.tituloError,
                colorFocus  = colorTipo
            )

            // Descripción
            CampoEvento(
                valor       = viewModel.nuevaDescripcion,
                onChange    = { viewModel.nuevaDescripcion = it },
                label       = "Descripción",
                placeholder = "Detalles del evento...",
                icono       = Icons.Default.Notes,
                maxLines    = 3,
                colorFocus  = colorTipo
            )

            // Fecha
            CampoEvento(
                valor       = viewModel.nuevaFecha,
                onChange    = { viewModel.nuevaFecha = it },
                label       = "Fecha",
                placeholder = "YYYY-MM-DD",
                icono       = Icons.Default.CalendarMonth,
                error       = viewModel.fechaError,
                colorFocus  = colorTipo
            )

            // Hora inicio y fin
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    CampoEvento(
                        valor       = viewModel.nuevaHoraInicio,
                        onChange    = { viewModel.nuevaHoraInicio = it },
                        label       = "Hora inicio",
                        placeholder = "08:30 AM",
                        icono       = Icons.Default.Schedule,
                        colorFocus  = colorTipo
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    CampoEvento(
                        valor       = viewModel.nuevaHoraFin,
                        onChange    = { viewModel.nuevaHoraFin = it },
                        label       = "Hora fin",
                        placeholder = "10:00 AM",
                        icono       = Icons.Default.Schedule,
                        colorFocus  = colorTipo
                    )
                }
            }

            // Lugar y grado
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    CampoEvento(
                        valor       = viewModel.nuevoLugar,
                        onChange    = { viewModel.nuevoLugar = it },
                        label       = "Lugar",
                        placeholder = "Auditorio / Zoom",
                        icono       = Icons.Default.LocationOn,
                        colorFocus  = colorTipo
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    CampoEvento(
                        valor       = viewModel.nuevoGrado,
                        onChange    = { viewModel.nuevoGrado = it },
                        label       = "Grado",
                        placeholder = "Todos los Grados",
                        icono       = Icons.Default.Class,
                        colorFocus  = colorTipo
                    )
                }
            }

            // Error general
            if (crearState is CrearEventoUiState.Error) {
                Card(
                    colors   = CardDefaults.cardColors(containerColor = ErrorCont),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text((crearState as CrearEventoUiState.Error).mensaje,
                        color = ErrorRed, fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp))
                }
            }

            // Botones
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick  = onDismiss,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Blue900)
                ) { Text("Cancelar", fontWeight = FontWeight.SemiBold) }

                Button(
                    onClick  = viewModel::crearEvento,
                    enabled  = crearState !is CrearEventoUiState.Loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.buttonColors(containerColor = colorTipo)
                ) {
                    if (crearState is CrearEventoUiState.Loading) {
                        CircularProgressIndicator(color = White,
                            modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.EventAvailable, null,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Crear evento", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Campo reutilizable ────────────────────────────────────────
@Composable
private fun CampoEvento(
    valor:       String,
    onChange:    (String) -> Unit,
    label:       String,
    placeholder: String,
    icono:       androidx.compose.ui.graphics.vector.ImageVector,
    error:       String?  = null,
    maxLines:    Int      = 1,
    colorFocus:  Color    = Blue800
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            color = Gray500, letterSpacing = 0.5.sp)
        OutlinedTextField(
            value          = valor,
            onValueChange  = onChange,
            placeholder    = { Text(placeholder, color = Gray200, fontSize = 12.sp) },
            leadingIcon    = { Icon(icono, null, tint = Gray500, modifier = Modifier.size(18.dp)) },
            isError        = error != null,
            supportingText = { error?.let { Text(it, color = ErrorRed, fontSize = 11.sp) } },
            maxLines       = maxLines,
            shape          = RoundedCornerShape(12.dp),
            modifier       = Modifier.fillMaxWidth(),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = colorFocus,
                unfocusedBorderColor = Gray200
            )
        )
    }
}
