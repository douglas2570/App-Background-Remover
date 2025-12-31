package com.example.appbackgroundremover.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appbackgroundremover.ui.navigation.Screen
import com.example.appbackgroundremover.ui.viewmodel.RemoveBackgroundViewModel
import com.example.appbackgroundremover.ui.viewmodel.RemoveBgUiState
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveBackgroundScreen(
    navController: NavController,
    viewModel: RemoveBackgroundViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Launcher para selecionar foto da galeria (Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onImageSelected(uri)
            }
        }
    )

    // Observa o estado para navegação ou erro
    LaunchedEffect(uiState) {
        when (uiState) {
            is RemoveBgUiState.Success -> {
                val filePath = (uiState as RemoveBgUiState.Success).resultParams
                // Codifica o path para passar na URL de navegação de forma segura
                val encodedPath = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString())

                navController.navigate(Screen.SaveResult.createRoute(encodedPath))
                viewModel.resetState() // Reseta para não navegar de novo ao voltar
            }
            is RemoveBgUiState.Error -> {
                Toast.makeText(context, (uiState as RemoveBgUiState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voltar", color = Color.White) }, // Mockup TELA 2
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B1B2F)) // Azul escuro do tema
            )
        },
        containerColor = Color(0xFF1B1B2F) // Fundo geral escuro
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Área de Seleção de Imagem (Placeholder pontilhado)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .dashedBorder(2.dp, Color.White, 16.dp) // Função auxiliar abaixo
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val currentUri = (uiState as? RemoveBgUiState.ImageSelected)?.imageUri

                    if (currentUri != null) {
                        // Mostra a imagem selecionada
                        AsyncImage(
                            model = currentUri,
                            contentDescription = "Imagem selecionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Placeholder (Ícone + Texto)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add, // Pode substituir por painterResource se tiver o ícone de galeria
                                contentDescription = "Selecionar",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Clique aqui para\nselecionar uma foto",
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botão Remover Background
                Button(
                    onClick = {
                        val uri = (uiState as? RemoveBgUiState.ImageSelected)?.imageUri
                        if (uri != null) {
                            viewModel.removeBackground(context, uri)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = uiState is RemoveBgUiState.ImageSelected, // Só habilita se tiver foto
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF304FFE), // Azul do botão (TELA 2)
                        disabledContainerColor = Color.Gray // Cinza quando desativado
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Remover Background", color = Color.White)
                }
            }

            // Overlay de Carregamento (TELA CARREGANDO)
            if (uiState is RemoveBgUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1B1B2F).copy(alpha = 0.9f)) // Fundo escuro semi-transparente
                        .clickable(enabled = false) {}, // Bloqueia cliques
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

// Extensão Helper para borda pontilhada
fun Modifier.dashedBorder(width: androidx.compose.ui.unit.Dp, color: Color, cornerRadius: androidx.compose.ui.unit.Dp) = drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
        ),
        cornerRadius = CornerRadius(cornerRadius.toPx())
    )
}