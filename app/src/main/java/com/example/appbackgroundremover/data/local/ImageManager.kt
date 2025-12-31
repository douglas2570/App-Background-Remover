package com.example.appbackgroundremover.data.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageManager(private val context: Context) {

    /**
     * Salva o Bitmap na pasta privada do aplicativo.
     * Retorna a Uri do arquivo salvo ou null em caso de erro.
     */
    fun saveImageToAppStorage(bitmap: Bitmap, filename: String): Uri? {
        // Garante que o nome do arquivo tenha a extensão .png
        val finalFilename = if (filename.endsWith(".png", ignoreCase = true)) {
            filename
        } else {
            "$filename.png"
        }

        // filesDir retorna o caminho para a pasta interna/privada do app
        val file = File(context.filesDir, finalFilename)

        return try {
            val stream = FileOutputStream(file)
            // Usamos PNG para manter a transparência do fundo removido
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            // Retorna a Uri do arquivo (file://...)
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Lista todas as imagens salvas na pasta privada do app.
     */
    fun getSavedImages(): List<File> {
        val directory = context.filesDir

        // Filtra apenas arquivos que são imagens (extensão .png)
        val files = directory.listFiles { _, name ->
            name.lowercase().endsWith(".png")
        }

        // Retorna a lista ou uma lista vazia se for null
        return files?.toList() ?: emptyList()
    }

    /**
     * Helper para deletar imagem se necessário no futuro
     */
    fun deleteImage(filename: String): Boolean {
        val file = File(context.filesDir, filename)
        return if (file.exists()) file.delete() else false
    }
}