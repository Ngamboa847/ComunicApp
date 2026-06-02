package com.example.comunicappescolar.ui.screens.calificaciones

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
import java.util.Locale

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val Blue50    = Color(0xFFE3F2FD)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)
private val White     = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarNotaSheet(viewModel: CalificacionesViewModel, onDismiss: () -> Unit) {
    val registrarState by viewModel.registrarState.collectAsState()

    LaunchedEffect(registrarState) {
        if (registrarState is RegistrarNotaUiState.Success) {
            viewModel.resetRegistrarState()
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
                    Text("Registrar Nota", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    Text("Ingresa la calificación del estudiante",
                        fontSize = 13.sp, color = Gray500)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Gray500)
                }
            }

            HorizontalDivider(color = Gray200)

            // ID Estudiante
            CampoNota(
                valor       = viewModel.estudianteId,
                onChange    = { viewModel.estudianteId = it },
                label       = "ID del Estudiante",
                placeholder = "Ej: 2",
                icono       = Icons.Default.Person,
                error       = viewModel.estudianteError,
                tipo        = androidx.compose.ui.text.input.KeyboardType.Number
            )

            // Materia
            CampoNota(
                valor       = viewModel.nuevaMateria,
                onChange    = { viewModel.nuevaMateria = it },
                label       = "Materia",
                placeholder = "Ej: Matemáticas",
                icono       = Icons.Default.MenuBook,
                error       = viewModel.materiaError
            )

            // Nota y Periodo en fila
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    CampoNota(
                        valor       = viewModel.nuevaNota,
                        onChange    = { viewModel.nuevaNota = it },
                        label       = "Nota (0.0 - 5.0)",
                        placeholder = "4.5",
                        icono       = Icons.Default.Star,
                        error       = viewModel.notaError,
                        tipo        = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    // Selector de periodo
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Periodo", fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold, color = Gray500)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (1..4).forEach { p ->
                                val sel = viewModel.nuevoPeriodo == p.toString()
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (sel) Blue900 else Gray200)
                                        .clickable { viewModel.nuevoPeriodo = p.toString() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$p", fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sel) White else Gray500)
                                }
                            }
                        }
                    }
                }
            }

            // Vista previa de la nota
            if (viewModel.nuevaNota.toDoubleOrNull() != null) {
                val nota = viewModel.nuevaNota.toDouble()
                val (colorN, labelN) = viewModel.badgeInfo(nota)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = CardDefaults.cardColors(containerColor = Blue50)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vista previa:", fontSize = 13.sp, color = Gray500)
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(String.format(Locale.getDefault(), "%.1f / 5.0", nota),
                                fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                color = colorN)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(colorN.copy(.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(labelN, fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold, color = colorN)
                            }
                        }
                    }
                }
            }

            // Error general
            if (registrarState is RegistrarNotaUiState.Error) {
                Card(
                    colors   = CardDefaults.cardColors(containerColor = ErrorCont),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text((registrarState as RegistrarNotaUiState.Error).mensaje,
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
                    onClick  = viewModel::registrarNota,
                    enabled  = registrarState !is RegistrarNotaUiState.Loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.buttonColors(containerColor = Blue900)
                ) {
                    if (registrarState is RegistrarNotaUiState.Loading) {
                        CircularProgressIndicator(color = White,
                            modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Registrar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Campo reutilizable ────────────────────────────────────────
@Composable
private fun CampoNota(
    valor:       String,
    onChange:    (String) -> Unit,
    label:       String,
    placeholder: String,
    icono:       androidx.compose.ui.graphics.vector.ImageVector,
    error:       String? = null,
    tipo:        androidx.compose.ui.text.input.KeyboardType =
        androidx.compose.ui.text.input.KeyboardType.Text
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            color = Gray500, letterSpacing = 0.5.sp)
        OutlinedTextField(
            value           = valor,
            onValueChange   = onChange,
            placeholder     = { Text(placeholder, color = Gray200, fontSize = 13.sp) },
            leadingIcon     = { Icon(icono, null, tint = Gray500, modifier = Modifier.size(18.dp)) },
            isError         = error != null,
            supportingText  = { error?.let { Text(it, color = ErrorRed, fontSize = 11.sp) } },
            singleLine      = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = tipo),
            shape           = RoundedCornerShape(12.dp),
            modifier        = Modifier.fillMaxWidth(),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Blue800,
                unfocusedBorderColor = Gray200
            )
        )
    }
}
