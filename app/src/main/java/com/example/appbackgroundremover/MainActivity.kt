package com.example.appbackgroundremover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appbackgroundremover.ui.AppNavigation
import com.example.appbackgroundremover.ui.theme.AppBackgroundRemoverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppBackgroundRemoverTheme {

                AppNavigation()
            }
        }
    }
}