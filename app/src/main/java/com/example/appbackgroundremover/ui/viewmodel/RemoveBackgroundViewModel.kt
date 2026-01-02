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

sealed class RemoveBgUiState {
    object Idle : RemoveBgUiState()
    data class ImageSelected(val imageUri: Uri) : RemoveBgUiState()
    object Loading : RemoveBgUiState()
    data class Success(val resultParams: String) : RemoveBgUiState()
    data class Error(val message: String) : RemoveBgUiState()
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
                val contentResolver = context.contentResolver

                val tempFile = File(context.cacheDir, "upload_temp.jpg")
                val inputStream = contentResolver.openInputStream(imageUri)
                val outputStream = FileOutputStream(tempFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image_file", tempFile.name, requestFile)

                val sizeParam = "auto".toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.instance.removeBackground(API_KEY, body, sizeParam)

                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    if (bitmap != null) {
                        val resultFile = File(context.cacheDir, "result_temp.png")
                        val resultStream = FileOutputStream(resultFile)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, resultStream)
                        resultStream.close()

                        _uiState.value = RemoveBgUiState.Success(resultFile.absolutePath)
                    } else {
                        _uiState.value = RemoveBgUiState.Error("Erro ao processar imagem retornada.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido na API"
                    _uiState.value = RemoveBgUiState.Error("Falha na API: $errorBody")
                }

                tempFile.delete()

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = RemoveBgUiState.Error("Erro de conexão: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = RemoveBgUiState.Idle
    }
}