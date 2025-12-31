package com.example.appbackgroundremover.data.local

import android.content.Context
import com.example.appbackgroundremover.data.model.ImageMetadata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MetadataManager(context: Context) {

    private val prefs = context.getSharedPreferences("image_metadata_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val keyMetadataList = "saved_images_metadata"

    /**
     * Salva um novo metadado na lista existente.
     */
    fun saveMetadata(metadata: ImageMetadata) {
        // 1. Recupera a lista atual
        val currentList = getAllMetadata().toMutableList()

        // 2. Adiciona o novo item
        // (Opcional: Verifica se já existe um arquivo com esse nome e atualiza)
        currentList.removeAll { it.filename == metadata.filename }
        currentList.add(metadata)

        // 3. Converte para JSON e salva
        val jsonString = gson.toJson(currentList)
        prefs.edit().putString(keyMetadataList, jsonString).apply()
    }

    /**
     * Recupera a lista completa de metadados salvos.
     */
    fun getAllMetadata(): List<ImageMetadata> {
        val jsonString = prefs.getString(keyMetadataList, null)

        return if (jsonString != null) {
            val type = object : TypeToken<List<ImageMetadata>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            emptyList()
        }
    }

    /**
     * Recupera metadado específico pelo nome do arquivo.
     */
    fun getMetadataForFile(filename: String): ImageMetadata? {
        val allData = getAllMetadata()
        return allData.find { it.filename == filename }
    }
}