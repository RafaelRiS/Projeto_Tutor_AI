import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import android.util.Log
import com.example.sic_project.ApiService
import com.example.sic_project.Models

object RetrofitClientInstance {
    // Substitua pelo URL do ngrok
    private const val BASE_URL = "https://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.app"

    private var retrofit: Retrofit? = null

    val retrofitInstance: Retrofit
        get() {
            if (retrofit == null) {
                val httpClient = OkHttpClient.Builder()
                    .connectTimeout(700, TimeUnit.SECONDS)  // Aumenta o tempo de conexão
                    .writeTimeout(700, TimeUnit.SECONDS)    // Aumenta o tempo de escrita
                    .readTimeout(700, TimeUnit.SECONDS)     // Aumenta o tempo de leitura
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)  // Adiciona o OkHttpClient ao Retrofit
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

    // Função para fazer uma chamada de API
    fun getSomeData() {
        val apiService = retrofitInstance.create(ApiService::class.java) // Crie sua interface API
        val call = apiService.getSomeData() // Chame a função que faz a requisição

        call.enqueue(object : Callback<Models.YourResponseType> {
            override fun onResponse(call: Call<Models.YourResponseType>, response: Response<Models.YourResponseType>) {
                if (response.isSuccessful) {
                    // Manipule a resposta
                    val data = response.body()
                    // Faça algo com os dados recebidos
                } else {
                    Log.e("AppError", "Erro na resposta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Models.YourResponseType>, t: Throwable) {
                Log.e("AppError", "Erro de conexão: ${t.message}")
            }
        })
    }
}
