package com.example.appbackgroundremover.data.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageManager(private val context: Context) {

    fun saveImageToAppStorage(bitmap: Bitmap, filename: String): Uri? {

        val finalFilename = if (filename.endsWith(".png", ignoreCase = true)) {
            filename
        } else {
            "$filename.png"
        }

        val file = File(context.filesDir, finalFilename)

        return try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getSavedImages(): List<File> {
        val directory = context.filesDir

        val files = directory.listFiles { _, name ->
            name.lowercase().endsWith(".png")
        }

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