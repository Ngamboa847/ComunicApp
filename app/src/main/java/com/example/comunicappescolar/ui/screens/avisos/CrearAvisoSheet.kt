package com.example.comunicappescolar.ui.screens.avisos

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comunicappescolar.data.model.Aviso

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)
private val SecCont   = Color(0xFF54A0FE)
private val TertFixed = Color(0xFFA3F69C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearAvisoSheet(
    viewModel: AvisosViewModel,
    onDismiss: () -> Unit
) {
    val crearState by viewModel.crearState.collectAsState()

    // Navegar/cerrar al crear exitosamente
    LaunchedEffect(crearState) {
        if (crearState is CrearAvisoUiState.Success) {
            viewModel.resetCrearState()
            onDismiss()
        }
    }

    val categorias = listOf(
        "info"    to "Informativo",
        "urgente" to "Urgente",
        "evento"  to "Evento"
    )

    val colorCategoria = when (viewModel.nuevaCategoria) {
        "urgente" -> ErrorRed
        "evento"  -> Color(0xFF25752B)
        else      -> Blue800
    }

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        containerColor    = Color.White,
        dragHandle        = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nuevo Aviso", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    Text("Publica un aviso para toda la comunidad",
                        fontSize = 13.sp, color = Gray500)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Gray500)
                }
            }

            HorizontalDivider(color = Gray200)

            // Selector de categoría
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Categoría", fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold, color = Gray500,
                    letterSpacing = 0.5.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categorias.forEach { (clave, etiqueta) ->
                        val sel = viewModel.nuevaCategoria == clave
                        val color = when (clave) {
                            "urgente" -> ErrorRed
                            "evento"  -> Color(0xFF25752B)
                            else      -> Blue800
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (sel) color.copy(.12f) else Gray200.copy(.3f))
                                .clickable { viewModel.nuevaCategoria = clave }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(etiqueta, fontSize = 12.sp,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                color = if (sel) color else Gray500)
                        }
                    }
                }
            }

            // Título
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Título", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    color = Gray500, letterSpacing = 0.5.sp)
                OutlinedTextField(
                    value         = viewModel.nuevoTitulo,
                    onValueChange = { viewModel.nuevoTitulo = it },
                    placeholder   = { Text("Ej: Reunión de padres", color = Gray200) },
                    isError       = viewModel.tituloError != null,
                    supportingText = { viewModel.tituloError?.let {
                        Text(it, color = ErrorRed, fontSize = 11.sp) } },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = colorCategoria,
                        unfocusedBorderColor = Gray200
                    )
                )
            }

            // Descripción corta
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Descripción corta", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    color = Gray500, letterSpacing = 0.5.sp)
                OutlinedTextField(
                    value         = viewModel.nuevaDescripcion,
                    onValueChange = { viewModel.nuevaDescripcion = it },
                    placeholder   = { Text("Resumen breve del aviso", color = Gray200) },
                    isError       = viewModel.descripcionError != null,
                    supportingText = { viewModel.descripcionError?.let {
                        Text(it, color = ErrorRed, fontSize = 11.sp) } },
                    maxLines      = 2,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = colorCategoria,
                        unfocusedBorderColor = Gray200
                    )
                )
            }

            // Contenido completo
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Contenido completo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    color = Gray500, letterSpacing = 0.5.sp)
                OutlinedTextField(
                    value         = viewModel.nuevoContenido,
                    onValueChange = { viewModel.nuevoContenido = it },
                    placeholder   = { Text("Escribe el contenido detallado del aviso...", color = Gray200) },
                    isError       = viewModel.contenidoError != null,
                    supportingText = { viewModel.contenidoError?.let {
                        Text(it, color = ErrorRed, fontSize = 11.sp) } },
                    minLines      = 4,
                    maxLines      = 6,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = colorCategoria,
                        unfocusedBorderColor = Gray200
                    )
                )
            }

            // Error general
            if (crearState is CrearAvisoUiState.Error) {
                Card(
                    colors   = CardDefaults.cardColors(containerColor = ErrorCont),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text((crearState as CrearAvisoUiState.Error).mensaje,
                        color    = Color(0xFF93000A),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp))
                }
            }

            // Botones
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = onDismiss,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Blue900)
                ) { Text("Cancelar", fontWeight = FontWeight.SemiBold) }

                Button(
                    onClick  = viewModel::crearAviso,
                    enabled  = crearState !is CrearAvisoUiState.Loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.buttonColors(containerColor = colorCategoria)
                ) {
                    if (crearState is CrearAvisoUiState.Loading) {
                        CircularProgressIndicator(color = Color.White,
                            modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Publicar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}


// ============================================================
// 4. TarjetaAviso (composable reutilizable)
// ============================================================
@Composable
fun TarjetaAviso(aviso: Aviso, onClick: () -> Unit) {
    val esLeido = aviso.leido == 1

    val colorBorde: Color
    val colorBadgeFondo: Color
    val colorBadgeTexto: Color
    val labelCategoria: String

    when (aviso.categoria) {
        "urgente" -> {
            colorBorde = ErrorRed
            colorBadgeFondo = ErrorCont
            colorBadgeTexto = Color(0xFF93000A)
            labelCategoria = "URGENTE"
        }
        "evento" -> {
            colorBorde = Color(0xFF25752B)
            colorBadgeFondo = TertFixed
            colorBadgeTexto = Color(0xFF002204)
            labelCategoria = "EVENTO"
        }
        else -> {
            colorBorde = Color(0xFF005FAF)
            colorBadgeFondo = SecCont.copy(.3f)
            colorBadgeTexto = Color(0xFF003567)
            labelCategoria = "INFO"
        }
    }

    val colorFondo by animateColorAsState(
        targetValue   = if (esLeido) Color(0xFFF9F9FF) else Color.White,
        animationSpec = tween(300), label = "fondoAviso"
    )

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (esLeido) 1.dp else 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        color = if (esLeido) colorBorde.copy(.4f) else colorBorde,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )
            Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorBadgeFondo)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(labelCategoria, fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorBadgeTexto, letterSpacing = 0.8.sp)
                    }
                    if (!esLeido) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape)
                            .background(Color(0xFF6750A4)))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(aviso.titulo, fontSize = 15.sp,
                    fontWeight = if (esLeido) FontWeight.Medium else FontWeight.Bold,
                    color = Color(0xFF191C21), maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(aviso.descripcion ?: "", fontSize = 13.sp,
                    color = Gray500, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null,
                            tint = Gray500, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(aviso.autor_nombre ?: "Administración",
                            fontSize = 11.sp, color = Gray500)
                    }
                    Text(aviso.fecha, fontSize = 11.sp, color = Gray500)
                }
            }
        }
    }
}
