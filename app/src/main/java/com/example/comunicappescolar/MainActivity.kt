package com.example.comunicappescolar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat.enableEdgeToEdge
import com.example.comunicappescolar.ui.navigation.AppNavigation
import com.example.comunicappescolar.ui.navigation.AppRoutes
import com.example.comunicappescolar.ui.theme.ComunicAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComunicAppTheme {
                AppNavigation()
            }
        }
    }
}
