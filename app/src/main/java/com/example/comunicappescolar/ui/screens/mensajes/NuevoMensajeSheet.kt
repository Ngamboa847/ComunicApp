package com.example.comunicappescolar.ui.screens.mensajes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comunicappescolar.data.model.Usuario

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val White     = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMensajeSheet(
    usuarios:  List<Usuario>,
    onContact: (Usuario) -> Unit,
    onDismiss: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    val filtrados = usuarios.filter {
        it.nombre.contains(busqueda, ignoreCase = true)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Nuevo mensaje", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    Text("Selecciona un contacto", fontSize = 13.sp, color = Gray500)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Gray500)
                }
            }

            OutlinedTextField(
                value         = busqueda,
                onValueChange = { busqueda = it },
                placeholder   = { Text("Buscar contacto...", color = Gray200) },
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = Gray500) },
                singleLine    = true,
                shape         = RoundedCornerShape(50),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Blue800,
                    unfocusedBorderColor = Gray200
                )
            )

            if (filtrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron contactos", color = Gray500)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtrados) { usuario ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onContact(usuario) }
                                .padding(10.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BlueFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = usuario.nombre.take(2).uppercase(),
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Blue900
                                )
                            }
                            Column {
                                Text(usuario.nombre, fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium, color = Color(0xFF191C21))
                                Text(
                                    text = usuario.rol.replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp, color = Gray500
                                )
                            }
                        }
                        HorizontalDivider(color = Gray200.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}
