package com.example.appbackgroundremover.data.local

import android.content.Context
import com.example.appbackgroundremover.data.model.ImageMetadata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MetadataManager(context: Context) {
    private val prefs = context.getSharedPreferences("image_metadata_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val keyMetadataList = "saved_images_metadata"


    fun saveMetadata(metadata: ImageMetadata) {
        val currentList = getAllMetadata().toMutableList()

        currentList.removeAll { it.filename == metadata.filename }
        currentList.add(metadata)

        val jsonString = gson.toJson(currentList)
        prefs.edit().putString(keyMetadataList, jsonString).apply()
    }


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