package com.example.appbackgroundremover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appbackgroundremover.ui.AppNavigation // Certifique-se de importar o AppNavigation criado na Parte 4
import com.example.appbackgroundremover.ui.theme.AppBackgroundRemoverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa o modo Edge-to-Edge (barra de status transparente/integrada)
        enableEdgeToEdge()

        setContent {
            AppBackgroundRemoverTheme {

                AppNavigation()
            }
        }
    }
}