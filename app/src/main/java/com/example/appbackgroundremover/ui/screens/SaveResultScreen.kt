package com.example.appbackgroundremover.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appbackgroundremover.ui.navigation.Screen
import com.example.appbackgroundremover.ui.viewmodel.SaveResultViewModel
import com.example.appbackgroundremover.ui.viewmodel.SaveResultViewModelFactory
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveResultScreen(
    navController: NavController,
    filePathArg: String?
) {
    val context = LocalContext.current

    // Inicializa ViewModel com Factory
    val viewModel: SaveResultViewModel = viewModel(
        factory = SaveResultViewModelFactory(context)
    )

    // Decodifica o caminho do arquivo recebido da navegação
    val decodedPath = remember(filePathArg) {
        try {
            URLDecoder.decode(filePathArg ?: "", StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            ""
        }
    }

    // Estados da UI
    var filename by remember { mutableStateOf("foto_editada") }
    val currentDate = remember { viewModel.getCurrentDate() }

    // Layout Principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voltar", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B1B2F))
            )
        },
        containerColor = Color(0xFF1B1B2F) // Fundo escuro (Tema do App)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Imagem Recortada em Destaque
            Box(
                modifier = Modifier
                    .weight(1f) // Ocupa o espaço disponível superior
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Exibe a imagem do arquivo temporário
                Image(
                    painter = rememberAsyncImagePainter(model = File(decodedPath)),
                    contentDescription = "Imagem sem fundo",
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // 2. Campos de Metadados (Conforme TELA 3)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo: Nome da Foto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nome da foto:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = filename,
                        onValueChange = { filename = it },
                        singleLine = true,
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.Blue,
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }

                // Campo: Data de Criação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Data de criação:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = currentDate,
                        color = Color.White,
                        modifier = Modifier.weight(2f).padding(start = 14.dp),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Botão Salvar Foto
            Button(
                onClick = {
                    if (filename.isNotBlank()) {
                        viewModel.saveFinalResult(
                            context = context,
                            tempFilePath = decodedPath,
                            userFileName = filename,
                            creationDate = currentDate,
                            onSuccess = {
                                // Navega de volta para Home limpando a pilha
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF304FFE) // Azul vibrante
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar Foto", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}