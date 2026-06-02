package com.example.comunicappescolar.ui.screens.registro


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    val context  = LocalContext.current
    val session  = remember { SessionDataStore(context) }
    val viewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(session)
    )
    val uiState by viewModel.uiState.collectAsState()

    // Navegar al Home al registrarse exitosamente
    LaunchedEffect(uiState) {
        if (uiState is RegistroUiState.Success) {
            navController.navigate(AppRoutes.Home.route) {
                popUpTo(AppRoutes.Registro.route) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // ── Fondo degradado mesh ─────────────────────────────
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .blur(100.dp)
                .background(BlueFixed.copy(alpha = 0.5f), RoundedCornerShape(50))
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .blur(100.dp)
                .background(Blue50.copy(alpha = 0.5f), RoundedCornerShape(50))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── TopBar ───────────────────────────────────────
            Surface(
                shadowElevation = 2.dp,
                color           = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Blue900)
                    }
                    Text(
                        text       = "ComunicApp Escolar",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Blue900
                    )
                }
            }

            // ── Contenido scrolleable ────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Imagen decorativa (solo mobile, el HTML
                //    la muestra en desktop en columna izquierda)
                AsyncImage(
                    model              = "https://lh3.googleusercontent.com/aida-public/AB6AXuDmSJZmz5QzA97Mrgkk_wBvaUjYBN9HIB5YOAw5YoPjVfcdx8rL2xl7jNJK3hljX8c8YDKqzAbggwHyz43UkAQfitHSreZJxkseqAlCwf6TO99ASJyAkcVVwYBsXsIFuq04UMAar2OPIXBvrnsguYexguSEhK1A2Qez1IV34JTQFLW6l3KvFv42mFgC81SV_8w5Ke1VKSj6lW4EIR825HYcM-guodbSLJ7lyB_BlDkMMfFBVmD4KoeY4WEKhWnh9SW-3lrlLMO4Uyg",
                    contentDescription = "Aula moderna",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(Modifier.height(8.dp))

                // Badges inferiores de la imagen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Icons.Default.VerifiedUser to "Privacidad segura",
                        Icons.Default.Groups       to "Compromiso directo"
                    ).forEach { (icono, texto) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = CardDefaults.cardColors(containerColor = Color.White),
                            border   = BorderStroke(1.dp, Gray200)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(icono, null, tint = Blue800, modifier = Modifier.size(18.dp))
                                Text(texto, fontSize = 11.sp, color = Gray500, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Tarjeta del formulario ───────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // Encabezado
                        Column {
                            Text("Crear Cuenta", fontSize = 22.sp,
                                fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                            Text("Completa tus datos para comenzar en el portal.",
                                fontSize = 13.sp, color = Gray500)
                        }

                        // ── Selector de rol ──────────────────
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Soy:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                color = Gray500, letterSpacing = 0.5.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("padre" to "Padre", "docente" to "Profesor").forEach { (clave, etiqueta) ->
                                    val sel = viewModel.rolSeleccionado == clave
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (sel) BlueFixed else Color.Transparent)
                                            .border(2.dp,
                                                if (sel) Blue800 else Gray200,
                                                RoundedCornerShape(12.dp))
                                            .clickable { viewModel.rolSeleccionado = clave }
                                            .padding(vertical = 14.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                if (clave == "padre") Icons.Default.FamilyRestroom
                                                else Icons.Default.School,
                                                null,
                                                tint     = if (sel) Blue900 else Gray500,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(etiqueta, fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (sel) Blue900 else Gray500)
                                        }
                                    }
                                }
                            }
                        }

                        // ── Nombre completo ──────────────────
                        CampoTexto(
                            valor     = viewModel.nombre,
                            onChange  = { viewModel.nombre = it },
                            label     = "Nombre completo",
                            placeholder = "Juan Pérez",
                            icono     = Icons.Default.Person,
                            error     = viewModel.nombreError
                        )

                        // ── Email ────────────────────────────
                        CampoTexto(
                            valor       = viewModel.email,
                            onChange    = { viewModel.email = it },
                            label       = "Correo electrónico",
                            placeholder = "juan@ejemplo.com",
                            icono       = Icons.Default.Mail,
                            error       = viewModel.emailError,
                            teclado     = KeyboardType.Email
                        )

                        // ── Colegio ──────────────────────────
                        CampoTexto(
                            valor       = viewModel.colegio,
                            onChange    = { viewModel.colegio = it },
                            label       = "Nombre de la escuela",
                            placeholder = "Colegio Internacional",
                            icono       = Icons.Default.AccountBalance,
                            error       = viewModel.colegioError
                        )

                        // ── Contraseñas en fila ──────────────
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                CampoTexto(
                                    valor       = viewModel.password,
                                    onChange    = { viewModel.password = it },
                                    label       = "Contraseña",
                                    placeholder = "••••••••",
                                    icono       = Icons.Default.Lock,
                                    error       = viewModel.passwordError,
                                    teclado     = KeyboardType.Password,
                                    esPassword  = true
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                CampoTexto(
                                    valor       = viewModel.confirmar,
                                    onChange    = { viewModel.confirmar = it },
                                    label       = "Confirmar",
                                    placeholder = "••••••••",
                                    icono       = Icons.Default.Verified,
                                    error       = viewModel.confirmarError,
                                    teclado     = KeyboardType.Password,
                                    esPassword  = true
                                )
                            }
                        }

                        // ── Términos y condiciones ───────────
                        Column {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked         = viewModel.aceptaTerminos,
                                    onCheckedChange = { viewModel.aceptaTerminos = it },
                                    colors          = CheckboxDefaults.colors(
                                        checkedColor = Blue800
                                    )
                                )
                                Text(
                                    text     = "Acepto los Términos de Servicio y la Política de Privacidad.",
                                    fontSize = 13.sp,
                                    color    = Gray500,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                            viewModel.terminosError?.let {
                                Text(it, fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 48.dp))
                            }
                        }

                        // ── Error general de API ─────────────
                        AnimatedVisibility(
                            visible = uiState is RegistroUiState.Error,
                            enter   = fadeIn(), exit = fadeOut()
                        ) {
                            (uiState as? RegistroUiState.Error)?.let {
                                Card(
                                    colors   = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(it.mensaje,
                                        color    = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(12.dp))
                                }
                            }
                        }

                        // ── Botón Registrarse ────────────────
                        Button(
                            onClick  = viewModel::registrar,
                            enabled  = uiState !is RegistroUiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape  = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue900)
                        ) {
                            if (uiState is RegistroUiState.Loading) {
                                CircularProgressIndicator(
                                    color       = Color.White,
                                    modifier    = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Registrarse", fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, null,
                                    modifier = Modifier.size(18.dp))
                            }
                        }

                        // ── Link a Login ─────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("¿Ya tienes una cuenta? ",
                                fontSize = 13.sp, color = Gray500)
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Iniciar sesión", fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, color = Blue900)
                            }
                        }
                    }
                }

                // Footer
                Spacer(Modifier.height(16.dp))
                Text(
                    text      = "© 2024 ComunicApp Escolar. Todos los derechos reservados.",
                    fontSize  = 11.sp,
                    color     = Gray500.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ── Composable reutilizable de campo de texto ────────────────
@Composable
private fun CampoTexto(
    valor:       String,
    onChange:    (String) -> Unit,
    label:       String,
    placeholder: String,
    icono:       androidx.compose.ui.graphics.vector.ImageVector,
    error:       String?         = null,
    teclado:     KeyboardType    = KeyboardType.Text,
    esPassword:  Boolean         = false
) {
    val Blue800 = Color(0xFF1565C0)
    val Gray500 = Color(0xFF727783)
    val Gray200 = Color(0xFFC2C6D4)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            color = Gray500, letterSpacing = 0.5.sp)
        OutlinedTextField(
            value               = valor,
            onValueChange       = onChange,
            placeholder         = { Text(placeholder, color = Gray200, fontSize = 13.sp) },
            leadingIcon         = { Icon(icono, null, tint = Gray500, modifier = Modifier.size(20.dp)) },
            visualTransformation = if (esPassword) PasswordVisualTransformation()
            else androidx.compose.ui.text.input.VisualTransformation.None,
            isError             = error != null,
            supportingText      = { error?.let { Text(it, color = Color(0xFFBA1A1A), fontSize = 11.sp) } },
            keyboardOptions     = KeyboardOptions(keyboardType = teclado),
            singleLine          = true,
            shape               = RoundedCornerShape(12.dp),
            modifier            = Modifier.fillMaxWidth(),
            colors              = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Blue800,
                unfocusedBorderColor = Gray200
            )
        )
    }
}