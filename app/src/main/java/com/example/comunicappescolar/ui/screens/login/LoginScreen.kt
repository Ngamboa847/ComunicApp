package com.example.comunicappescolar.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.ui.navigation.AppRoutes

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val Blue50    = Color(0xFFE3F2FD)
private val BlueFixed = Color(0xFFD6E3FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val Surface   = Color(0xFFF9F9FF)

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val session = remember { SessionDataStore(context) }
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(session)
    )
    val uiState by viewModel.uiState.collectAsState()

    // Navegar al Home cuando login sea exitoso
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            navController.navigate(AppRoutes.Home.route) {
                popUpTo(AppRoutes.Login.route) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // ── Luces decorativas de fondo ───────────────────────
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = 150.dp, y = (-80).dp)
                .blur(100.dp)
                .background(BlueFixed.copy(alpha = 0.2f), RoundedCornerShape(50))
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .blur(100.dp)
                .background(Blue50.copy(alpha = 0.2f), RoundedCornerShape(50))
        )

        // ── Contenido principal ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Header: ícono + título ───────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Blue800),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.School,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text       = "ComunicApp Escolar",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Blue900
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = "Conectando escuelas y familias con facilidad.",
                    fontSize = 14.sp,
                    color    = Gray500,
                    textAlign = TextAlign.Center
                )
            }

            // ── Tarjeta del formulario ───────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // ── Selector de rol ──────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text       = "SOY...",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Gray500,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("padre" to "Padre", "docente" to "Profesor").forEach { (clave, etiqueta) ->
                                val seleccionado = viewModel.rolSeleccionado == clave
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (seleccionado) BlueFixed else Color.Transparent)
                                        .border(
                                            width = 2.dp,
                                            color = if (seleccionado) Blue800 else Gray200,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .clickable { viewModel.rolSeleccionado = clave }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (clave == "padre")
                                                Icons.Default.FamilyRestroom
                                            else
                                                Icons.Default.Person,
                                            contentDescription = null,
                                            tint     = if (seleccionado) Blue900 else Gray500,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text       = etiqueta,
                                            fontSize   = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color      = if (seleccionado) Blue900 else Gray500
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Campo Email ──────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Correo electrónico", fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold, color = Gray500, letterSpacing = 0.5.sp)
                        OutlinedTextField(
                            value         = viewModel.email,
                            onValueChange = { viewModel.email = it },
                            placeholder   = { Text("nombre@escuela.edu", color = Gray200) },
                            leadingIcon   = { Icon(Icons.Default.Mail, null, tint = Gray500) },
                            isError       = viewModel.emailError != null,
                            supportingText = { viewModel.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine    = true,
                            shape         = RoundedCornerShape(12.dp),
                            modifier      = Modifier.fillMaxWidth(),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Blue800,
                                unfocusedBorderColor = Gray200
                            )
                        )
                    }

                    // ── Campo Contraseña ─────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Contraseña", fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold, color = Gray500, letterSpacing = 0.5.sp)
                        OutlinedTextField(
                            value         = viewModel.password,
                            onValueChange = { viewModel.password = it },
                            placeholder   = { Text("••••••••", color = Gray200) },
                            leadingIcon   = { Icon(Icons.Default.Lock, null, tint = Gray500) },
                            trailingIcon  = {
                                IconButton(onClick = { viewModel.passwordVisible = !viewModel.passwordVisible }) {
                                    Icon(
                                        imageVector = if (viewModel.passwordVisible)
                                            Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Gray500
                                    )
                                }
                            },
                            visualTransformation = if (viewModel.passwordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            isError        = viewModel.passwordError != null,
                            supportingText = { viewModel.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine     = true,
                            shape          = RoundedCornerShape(12.dp),
                            modifier       = Modifier.fillMaxWidth(),
                            colors         = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Blue800,
                                unfocusedBorderColor = Gray200
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { }) {
                                Text("¿Olvidaste tu contraseña?", fontSize = 12.sp, color = Blue800)
                            }
                        }
                    }

                    // ── Error de API ─────────────────────────
                    AnimatedVisibility(
                        visible = uiState is LoginUiState.Error,
                        enter   = fadeIn(),
                        exit    = fadeOut()
                    ) {
                        (uiState as? LoginUiState.Error)?.let {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text     = it.mensaje,
                                    color    = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    // ── Botón Login ──────────────────────────
                    Button(
                        onClick  = viewModel::login,
                        enabled  = uiState !is LoginUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape  = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue900)
                    ) {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(
                                color    = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Iniciar sesión", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ── Footer: link a registro ──────────────────────
            Spacer(Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("¿No tienes una cuenta? ", fontSize = 14.sp, color = Gray500)
                TextButton(onClick = { navController.navigate(AppRoutes.Registro.route) }) {
                    Text("Regístrate", fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold, color = Blue900)
                }
            }
        }
    }
}