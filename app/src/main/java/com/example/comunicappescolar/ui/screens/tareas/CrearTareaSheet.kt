package com.example.comunicappescolar.ui.screens.tareas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Send
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
private val White     = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTareaSheet(viewModel: TareasViewModel, onDismiss: () -> Unit) {
    val crearState by viewModel.crearState.collectAsState()

    LaunchedEffect(crearState) {
        if (crearState is CrearTareaUiState.Success) {
            viewModel.resetCrearState()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = White,
        dragHandle       = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Gray200),
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nueva Tarea",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C21)
                    )
                    Text(
                        text = "Completa los datos de la actividad",
                        fontSize = 13.sp,
                        color = Gray500
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Gray500)
                }
            }

            HorizontalDivider(color = Gray200)

            // Título
            CampoTarea(
                valor       = viewModel.nuevoTitulo,
                onChange    = { viewModel.nuevoTitulo = it },
                label       = "Título de la tarea",
                placeholder = "Ej: Ensayo sobre la Revolución Francesa",
                icono       = Icons.AutoMirrored.Filled.Assignment,
                error       = viewModel.tituloError
            )

            // Descripción
            CampoTarea(
                valor       = viewModel.nuevaDescripcion,
                onChange    = { viewModel.nuevaDescripcion = it },
                label       = "Descripción",
                placeholder = "Instrucciones o detalles de la tarea...",
                icono       = Icons.AutoMirrored.Filled.Notes,
                maxLines    = 3
            )

            // Materia y grado en fila
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    CampoTarea(
                        valor       = viewModel.nuevaMateria,
                        onChange    = { viewModel.nuevaMateria = it },
                        label       = "Materia",
                        placeholder = "Matemáticas",
                        icono       = Icons.AutoMirrored.Filled.MenuBook,
                        error       = viewModel.materiaError
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    CampoTarea(
                        valor       = viewModel.nuevoGrado,
                        onChange    = { viewModel.nuevoGrado = it },
                        label       = "Grado",
                        placeholder = "10° Grado",
                        icono       = Icons.Default.Class
                    )
                }
            }

            // Fecha límite
            CampoTarea(
                valor       = viewModel.nuevaFecha,
                onChange    = { viewModel.nuevaFecha = it },
                label       = "Fecha límite",
                placeholder = "2024-11-30",
                icono       = Icons.Default.CalendarMonth,
                error       = viewModel.fechaError
            )

            // Error general
            if (crearState is CrearTareaUiState.Error) {
                Card(
                    colors   = CardDefaults.cardColors(containerColor = ErrorCont),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text((crearState as CrearTareaUiState.Error).mensaje,
                        color    = ErrorRed,
                        fontSize = 13.sp,
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
                    onClick  = viewModel::crearTarea,
                    enabled  = crearState !is CrearTareaUiState.Loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.buttonColors(containerColor = Blue900)
                ) {
                    if (crearState is CrearTareaUiState.Loading) {
                        CircularProgressIndicator(color = White,
                            modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Crear tarea", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Composable campo reutilizable ─────────────────────────────
@Composable
private fun CampoTarea(
    valor:       String,
    onChange:    (String) -> Unit,
    label:       String,
    placeholder: String,
    icono:       androidx.compose.ui.graphics.vector.ImageVector,
    error:       String?  = null,
    maxLines:    Int      = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            color = Gray500, letterSpacing = 0.5.sp)
        OutlinedTextField(
            value          = valor,
            onValueChange  = onChange,
            placeholder    = { Text(placeholder, color = Gray200, fontSize = 13.sp) },
            leadingIcon    = { Icon(icono, null, tint = Gray500, modifier = Modifier.size(18.dp)) },
            isError        = error != null,
            supportingText = { error?.let { Text(it, color = ErrorRed, fontSize = 11.sp) } },
            maxLines       = maxLines,
            shape          = RoundedCornerShape(12.dp),
            modifier       = Modifier.fillMaxWidth(),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Blue800,
                unfocusedBorderColor = Gray200
            )
        )
    }
}
