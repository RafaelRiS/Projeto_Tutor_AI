package com.example.sic_project // Use o pacote correto

import com.example.sic_project.Models.YourResponseType
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET // Importar se estiver usando o GET

interface ApiService {
    @POST("responder")
    fun getResponse(@Body pergunta: Models.Pergunta): Call<Models.Resposta>

    @GET("sua/endpoint/aqui") // Substitua pela sua URL real
    fun getSomeData(): Call<YourResponseType> // Certifique-se de que YourResponseType está no escopo
}
