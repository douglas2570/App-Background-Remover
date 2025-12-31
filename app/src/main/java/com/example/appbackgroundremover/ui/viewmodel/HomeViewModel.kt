package com.example.appbackgroundremover.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appbackgroundremover.data.local.ImageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(private val imageManager: ImageManager) : ViewModel() {

    private val _savedImages = MutableStateFlow<List<File>>(emptyList())
    val savedImages: StateFlow<List<File>> = _savedImages.asStateFlow()

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            // Recarrega a lista de arquivos da pasta do app
            _savedImages.value = imageManager.getSavedImages()
        }
    }
}

// Factory para injetar o ImageManager no ViewModel
class HomeViewModelFactory(private val imageManager: ImageManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(imageManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}