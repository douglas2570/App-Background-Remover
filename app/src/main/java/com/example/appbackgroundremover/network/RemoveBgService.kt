package com.exemplo.appbackgroundremover.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RemoveBgService {

    // Documentação: POST https://api.remove.bg/v1.0/removebg
    @Multipart
    @POST("removebg")
    suspend fun removeBackground(
        // Header de autenticação obrigatório
        @Header("X-Api-Key") apiKey: String,

        // Arquivo de imagem (binário)
        @Part imageFile: MultipartBody.Part,

        // Parâmetro de tamanho (size="auto")
        @Part("size") size: RequestBody
    ): Response<ResponseBody>
    // Retornamos Response<ResponseBody> para ter acesso ao status code e ao corpo binário da imagem
}