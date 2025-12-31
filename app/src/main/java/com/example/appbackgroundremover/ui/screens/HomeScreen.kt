package com.example.appbackgroundremover.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.appbackgroundremover.ui.navigation.Screen
import com.example.appbackgroundremover.ui.viewmodel.HomeViewModel

import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val savedImages by viewModel.savedImages.collectAsState()
    val context = LocalContext.current

    // Recarrega as imagens sempre que a tela entra em foco (útil ao voltar da tela de salvar)
    LaunchedEffect(Unit) {
        viewModel.loadImages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "App Background Remover",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.RemoveBackground.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nova foto")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nova foto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Fotos Salvas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (savedImages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma foto salva ainda.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 3 colunas conforme o mockup TELA 1
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedImages) { file ->
                        ImageCard(file = file, onImageClick = {
                            openImageInGallery(context, file)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCard(file: File, onImageClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f) // Formato retangular vertical
            .clickable { onImageClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exibe a miniatura da imagem
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(file)
                    .crossfade(true)
                    .build(),
                contentDescription = "Foto salva",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            // Nome do arquivo
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// Função auxiliar para abrir a Intent da Galeria
// Função auxiliar para abrir a Intent com seletor de aplicativos
fun openImageInGallery(context: android.content.Context, file: File) {
    try {
        // Gera uma URI segura (content://) ao invés de file://
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Deve bater com o authority do Manifest
            file
        )

        // Cria a intenção de visualização
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/png") // Define que é uma imagem PNG
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) // Dá permissão de leitura temporária
        }

        // Cria o "Chooser" (a janela que pergunta "Abrir com...")
        val chooser = android.content.Intent.createChooser(intent, "Abrir imagem com...")

        // Inicia a activity
        context.startActivity(chooser)

    } catch (e: Exception) {
        e.printStackTrace()
        android.widget.Toast.makeText(context, "Erro ao abrir imagem.", android.widget.Toast.LENGTH_SHORT).show()
    }
}