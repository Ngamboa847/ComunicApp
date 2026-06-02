package com.example.comunicappescolar.ui.screens.asistencia

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
private val Blue900    = Color(0xFF004D99)
private val Blue800    = Color(0xFF1565C0)
private val Gray500    = Color(0xFF727783)
private val Gray200    = Color(0xFFC2C6D4)
private val ErrorRed   = Color(0xFFBA1A1A)
private val ErrorCont  = Color(0xFFFFDAD6)
private val GreenMid   = Color(0xFF2E7D32)
private val SecondCol  = Color(0xFF54A0FE)
private val White      = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarAsistenciaSheet(
    viewModel: AsistenciaViewModel,
    onDismiss: () -> Unit
) {
    val registrarState by viewModel.registrarState.collectAsState()

    LaunchedEffect(registrarState) {
        if (registrarState is RegistrarAsistenciaUiState.Success) {
            viewModel.resetRegistrarState()
            onDismiss()
        }
    }

    val estados = listOf(
        "presente"  to "Presente",
        "ausente"   to "Ausente",
        "tardanza"  to "Tardanza"
    )

    val colorEstado = when (viewModel.estadoRegistro) {
        "presente" -> GreenMid
        "ausente"  -> ErrorRed
        else       -> SecondCol
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Registrar Asistencia", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    Text("Registra la asistencia del estudiante",
                        fontSize = 13.sp, color = Gray500)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Gray500)
                }
            }

            HorizontalDivider(color = Gray200)

            // ID Estudiante
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("ID del Estudiante", fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold, color = Gray500)
                OutlinedTextField(
                    value         = viewModel.estudianteId,
                    onValueChange = { viewModel.estudianteId = it },
                    placeholder   = { Text("Ej: 2", color = Gray200) },
                    leadingIcon   = { Icon(Icons.Default.Person, null, tint = Gray500) },
                    isError       = viewModel.estudianteError != null,
                    supportingText = { viewModel.estudianteError?.let {
                        Text(it, color = ErrorRed, fontSize = 11.sp) } },
                    singleLine    = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Blue800,
                        unfocusedBorderColor = Gray200
                    )
                )
            }

            // Fecha
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Fecha", fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold, color = Gray500)
                OutlinedTextField(
                    value         = viewModel.fechaRegistro,
                    onValueChange = { viewModel.fechaRegistro = it },
                    placeholder   = { Text("YYYY-MM-DD", color = Gray200) },
                    leadingIcon   = { Icon(Icons.Default.CalendarMonth, null, tint = Gray500) },
                    isError       = viewModel.fechaError != null,
                    supportingText = { viewModel.fechaError?.let {
                        Text(it, color = ErrorRed, fontSize = 11.sp) } },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Blue800,
                        unfocusedBorderColor = Gray200
                    )
                )
            }

            // Selector de estado
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Estado", fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold, color = Gray500)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    estados.forEach { (clave, etiqueta) ->
                        val sel = viewModel.estadoRegistro == clave
                        val color = when (clave) {
                            "presente" -> GreenMid
                            "ausente"  -> ErrorRed
                            else       -> SecondCol
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (sel) color.copy(.12f) else Gray200.copy(.5f))
                                .clickable { viewModel.estadoRegistro = clave }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = when (clave) {
                                        "presente" -> Icons.Default.Check
                                        "ausente"  -> Icons.Default.Close
                                        else       -> Icons.Default.Schedule
                                    },
                                    contentDescription = null,
                                    tint     = if (sel) color else Gray500,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(etiqueta, fontSize = 12.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sel) color else Gray500)
                            }
                        }
                    }
                }
            }

            // Error general
            if (registrarState is RegistrarAsistenciaUiState.Error) {
                Card(
                    colors   = CardDefaults.cardColors(containerColor = ErrorCont),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text((registrarState as RegistrarAsistenciaUiState.Error).mensaje,
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
                    onClick  = viewModel::registrarAsistencia,
                    enabled  = registrarState !is RegistrarAsistenciaUiState.Loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.buttonColors(containerColor = colorEstado)
                ) {
                    if (registrarState is RegistrarAsistenciaUiState.Loading) {
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
