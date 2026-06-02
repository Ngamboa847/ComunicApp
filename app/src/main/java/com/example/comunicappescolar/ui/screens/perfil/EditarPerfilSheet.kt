package com.example.comunicappescolar.ui.screens.perfil

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)
private val White     = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilSheet(
    viewModel:  PerfilViewModel,
    fotoUri:    android.net.Uri?,
    onPickFoto: () -> Unit,
    onDismiss:  () -> Unit
) {
    val editarState by viewModel.editarState.collectAsState()

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
                    Text("Editar Perfil", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    Text("Actualiza tu información personal",
                        fontSize = 13.sp, color = Gray500)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Gray500)
                }
            }

            HorizontalDivider(color = Gray200)

            // Selector de foto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BlueFixed)
                            .border(2.dp, Blue800, CircleShape)
                            .clickable { onPickFoto() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoUri != null) {
                            AsyncImage(
                                model              = fotoUri,
                                contentDescription = null,
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Person, null,
                                tint = Blue900, modifier = Modifier.size(36.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Blue900)
                            .clickable { onPickFoto() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, null,
                            tint = White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Text("Toca la foto para cambiarla",
                fontSize = 11.sp, color = Gray500,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth())

            // Nombre
            CampoEditar(
                valor       = viewModel.editNombre,
                onChange    = { viewModel.editNombre = it },
                label       = "Nombre completo",
                placeholder = "Tu nombre completo",
                icono       = Icons.Default.Person,
                error       = viewModel.nombreError
            )

            // Teléfono
            CampoEditar(
                valor       = viewModel.editTelefono,
                onChange    = { viewModel.editTelefono = it },
                label       = "Número de teléfono",
                placeholder = "+57 300 000 0000",
                icono       = Icons.Default.Phone,
                tipo        = KeyboardType.Phone
            )

            // Grado / Materia
            CampoEditar(
                valor       = viewModel.editGrado,
                onChange    = { viewModel.editGrado = it },
                label       = "Grado / Materia",
                placeholder = "Ej: 5° Primaria o Matemáticas",
                icono       = Icons.Default.School
            )

            // Colegio
            CampoEditar(
                valor       = viewModel.editColegio,
                onChange    = { viewModel.editColegio = it },
                label       = "Institución educativa",
                placeholder = "Nombre del colegio",
                icono       = Icons.Default.AccountBalance
            )

            // Error
            AnimatedVisibility(
                visible = editarState is EditarPerfilUiState.Error,
                enter   = fadeIn(), exit = fadeOut()
            ) {
                (editarState as? EditarPerfilUiState.Error)?.let {
                    Card(
                        colors   = CardDefaults.cardColors(containerColor = ErrorCont),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(it.mensaje, color = ErrorRed, fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp))
                    }
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
                    onClick  = viewModel::guardarCambios,
                    enabled  = editarState !is EditarPerfilUiState.Loading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.buttonColors(containerColor = Blue900)
                ) {
                    if (editarState is EditarPerfilUiState.Loading) {
                        CircularProgressIndicator(color = White,
                            modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Guardar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Campo reutilizable ────────────────────────────────────────
@Composable
private fun CampoEditar(
    valor:       String,
    onChange:    (String) -> Unit,
    label:       String,
    placeholder: String,
    icono:       androidx.compose.ui.graphics.vector.ImageVector,
    error:       String?      = null,
    tipo:        KeyboardType = KeyboardType.Text
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
            keyboardOptions = KeyboardOptions(keyboardType = tipo),
            shape           = RoundedCornerShape(12.dp),
            modifier        = Modifier.fillMaxWidth(),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Blue800,
                unfocusedBorderColor = Gray200
            )
        )
    }
}
