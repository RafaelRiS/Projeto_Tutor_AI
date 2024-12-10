package com.example.sic_project

class Models {

    data class Pergunta(
        val pergunta: String
    )

    data class Resposta(
        val resposta: String
    )

    data class YourResponseType(
        val field1: String,
        val field2: Int
        // Adicione mais campos conforme necessário
    )

}