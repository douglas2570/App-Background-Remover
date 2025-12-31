package com.example.appbackgroundremover.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplo.appbackgroundremover.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

// Definição dos Estados da UI
sealed class RemoveBgUiState {
    object Idle : RemoveBgUiState() // Estado inicial
    data class ImageSelected(val imageUri: Uri) : RemoveBgUiState() // Foto escolhida
    object Loading : RemoveBgUiState() // Enviando...
    data class Success(val resultParams: String) : RemoveBgUiState() // Sucesso (caminho do arquivo)
    data class Error(val message: String) : RemoveBgUiState() // Erro
}

class RemoveBackgroundViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RemoveBgUiState>(RemoveBgUiState.Idle)
    val uiState: StateFlow<RemoveBgUiState> = _uiState.asStateFlow()

    private val API_KEY = "QoHYQyYmtmjoUWKcLWG4amc7"

    fun onImageSelected(uri: Uri) {
        _uiState.value = RemoveBgUiState.ImageSelected(uri)
    }

    fun removeBackground(context: Context, imageUri: Uri) {
        _uiState.value = RemoveBgUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Preparar o arquivo para envio (Multipart)
                val contentResolver = context.contentResolver

                // Cria uma cópia temporária do arquivo selecionado para poder enviar
                val tempFile = File(context.cacheDir, "upload_temp.jpg")
                val inputStream = contentResolver.openInputStream(imageUri)
                val outputStream = FileOutputStream(tempFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image_file", tempFile.name, requestFile)

                val sizeParam = "auto".toRequestBody("text/plain".toMediaTypeOrNull())

                // 2. Chamada de Rede
                val response = RetrofitClient.instance.removeBackground(API_KEY, body, sizeParam)

                if (response.isSuccessful && response.body() != null) {
                    // 3. Converter a resposta binária em Bitmap
                    val bytes = response.body()!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    if (bitmap != null) {
                        // 4. Salvar Bitmap resultante em cache para passar para a próxima tela
                        val resultFile = File(context.cacheDir, "result_temp.png")
                        val resultStream = FileOutputStream(resultFile)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, resultStream)
                        resultStream.close()

                        // Atualiza estado para sucesso com o caminho do arquivo
                        _uiState.value = RemoveBgUiState.Success(resultFile.absolutePath)
                    } else {
                        _uiState.value = RemoveBgUiState.Error("Erro ao processar imagem retornada.")
                    }
                } else {
                    // Tenta ler a mensagem de erro da API (JSON)
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido na API"
                    _uiState.value = RemoveBgUiState.Error("Falha na API: $errorBody")
                }

                // Limpa o arquivo de upload temporário
                tempFile.delete()

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = RemoveBgUiState.Error("Erro de conexão: ${e.message}")
            }
        }
    }

    // Reseta o estado ao sair da tela ou voltar
    fun resetState() {
        _uiState.value = RemoveBgUiState.Idle
    }
}