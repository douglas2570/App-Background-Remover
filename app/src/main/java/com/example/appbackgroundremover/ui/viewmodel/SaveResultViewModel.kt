package com.example.appbackgroundremover.ui.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appbackgroundremover.data.local.ImageManager
import com.example.appbackgroundremover.data.local.MetadataManager
import com.example.appbackgroundremover.data.model.ImageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveResultViewModel(
    private val imageManager: ImageManager,
    private val metadataManager: MetadataManager
) : ViewModel() {

    fun saveFinalResult(
        context: Context,
        tempFilePath: String,
        userFileName: String,
        creationDate: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Carrega o Bitmap do arquivo temporário (cache)
                val tempFile = File(tempFilePath)
                if (!tempFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Erro: Arquivo temporário não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)

                if (bitmap != null) {
                    // 2. Salva o Bitmap na pasta definitiva do App (ImageManager)
                    // O ImageManager já adiciona a extensão .png se necessário
                    val savedUri = imageManager.saveImageToAppStorage(bitmap, userFileName)

                    if (savedUri != null) {
                        // 3. Salva os Metadados (MetadataManager)
                        // Garante que o nome salvo no metadado seja igual ao do arquivo (com .png)
                        val finalName = if (userFileName.endsWith(".png", true)) userFileName else "$userFileName.png"

                        val metadata = ImageMetadata(
                            filename = finalName,
                            date = creationDate
                        )
                        metadataManager.saveMetadata(metadata)

                        // 4. Feedback e Navegação
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Foto salva com sucesso!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Erro ao salvar imagem.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Gera a data atual formatada (Ex: 30/12/2025)
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}

// Factory para injetar dependências
class SaveResultViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveResultViewModel::class.java)) {
            val imageManager = ImageManager(context)
            val metadataManager = MetadataManager(context)
            return SaveResultViewModel(imageManager, metadataManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}