package com.example.sic_project

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.sic_project.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastResponse: String? = null
    private var isWebViewVisible = true
    private var isWebViewLoaded = false
    private val handler = Handler()
    private var stopGeneration = false
    private var currentCall: Call<Models.Resposta>? = null // Chamada atual da API
    private var currentIndex = 0 // Índice atual para animação
    private var currentResponse: String? = null // Resposta atual para animação
    private lateinit var textToSpeech: TextToSpeech // Variável para TextToSpeech
    private var currentSpeechIndex = 0 // Índice atual da fala
    private var filteredResponse: String? = null // Resposta filtrada sem emojis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchSomeData()

        // Inicializa o TextToSpeech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale("pt", "BR"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Linguagem não suportada", Toast.LENGTH_SHORT).show()
                } else {
                    // Adicione uma chamada aqui para ativar o TTS se a inicialização for bem-sucedida
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }

        binding.voiceButton.setOnClickListener {
            startVoiceRecognition()
        }

        // Clique do botão de fala
        binding.speakButton.setOnClickListener {
            currentResponse?.let { response ->
                if (textToSpeech.isSpeaking) {
                    stopSpeech() // Para a fala
                } else {
                    speakResponse(response) // Começa a falar
                }
            } ?: run {
                Toast.makeText(this, "Nenhuma resposta para falar.", Toast.LENGTH_SHORT).show()
            }
            // Aumenta a velocidade da fala
            textToSpeech.setSpeechRate(1.5f) // Aumenta a velocidade para 1.5
        }

        // Configurações do WebView
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        binding.webView.webViewClient = WebViewClient()

        // Chame a função para fazer a requisição inicial
        RetrofitClientInstance.getSomeData()

        // Configurando o botão de envio
        binding.sendButton.setOnClickListener {
            val pergunta = binding.questionEditText.text.toString()
            if (pergunta.isNotEmpty()) {
                try {
                    fetchResponse(pergunta)
                    binding.questionEditText.text.clear()
                    hideKeyboard()
                } catch (e: Exception) {
                    Toast.makeText(this, "Ocorreu um erro: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Error", "Erro ao enviar pergunta", e)
                }
            } else {
                Toast.makeText(this, "Por favor, digite uma pergunta.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurando o botão de parar
        binding.stopButton.setOnClickListener {
            stopGeneration = true
            binding.stopButton.visibility = View.GONE
            binding.sendButton.visibility = View.VISIBLE
            currentCall?.cancel() // Cancela a chamada à API
        }

        // Configurando o botão de alternância de visualização
        binding.toggleViewButton.setOnClickListener {
            toggleView()
        }

        setupDrawer()
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setupDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    private fun saveResponseToLocal(pergunta: String, resposta: String) {
        val sharedPreferences = getSharedPreferences("local_responses", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(pergunta, resposta)
        editor.apply()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.allNetworks.mapNotNull {
            connectivityManager.getNetworkInfo(it)
        }
        return networkInfo.any { it.isConnected }
    }

    companion object {
        private const val REQUEST_CODE_VOICE_RECOGNITION = 100
    }


    private lateinit var vibrator: Vibrator

    private fun fetchResponse(pergunta: String) {
        // Limpa a resposta anterior
        binding.responseTextView.text = ""

        // Verifique a conexão antes de chamar a API
        if (isNetworkAvailable()) {
            // Faça a chamada à API
            binding.progressBar.visibility = View.VISIBLE
            binding.loadingMessageTextView.visibility = View.VISIBLE
            binding.sendButton.visibility = View.GONE
            binding.stopButton.visibility = View.VISIBLE
            stopGeneration = false
            currentIndex = 0

            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

            val apiService = RetrofitClientInstance.retrofitInstance?.create(ApiService::class.java)
            currentCall = apiService?.getResponse(Models.Pergunta(pergunta))
            currentCall?.enqueue(object : Callback<Models.Resposta> {
                override fun onResponse(call: Call<Models.Resposta>, response: Response<Models.Resposta>) {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingMessageTextView.visibility = View.GONE

                    if (response.isSuccessful) {
                        lastResponse = response.body()?.resposta
                        currentResponse = lastResponse
                        currentSpeechIndex = 0 // Reseta a posição da fala
                        saveResponseToLocal(pergunta, lastResponse!!)
                        Log.d("API Response", lastResponse ?: "Nenhuma resposta encontrada.")
                        startResponseAnimation()
                        showResponse(currentResponse)

                        // Vibre ao receber a resposta
                        vibrator.vibrate(500) // vibra por 500 ms

                        // Ativa a fala automaticamente
                        speakResponse(currentResponse ?: "")
                    } else {
                        handleError(response.code(), response.message())
                    }
                }

                override fun onFailure(call: Call<Models.Resposta>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingMessageTextView.visibility = View.GONE
                    showResponse("Erro: ${t.message}")
                }
            })
        } else {
            // Se não estiver conectado, não faz nada e exibe a resposta armazenada se existir
            val savedResponse = getSavedResponse(pergunta)
            if (savedResponse != null) {
                binding.responseTextView.text = savedResponse // Exibe a resposta salva sem animação
                showResponse(savedResponse) // Apenas exibe a resposta
            } else {
                showResponse("Nenhuma resposta disponível.")
            }
        }
    }

    private fun getSavedResponse(pergunta: String): String? {
        val sharedPreferences = getSharedPreferences("local_responses", MODE_PRIVATE)
        return sharedPreferences.getString(pergunta, null) // Retorna null se não houver resposta salva
    }

    private fun handleError(code: Int, message: String) {
        val errorMessage = when (code) {
            404 -> "Vídeo não encontrado."
            500 -> "Erro no servidor. Tente novamente mais tarde."
            else -> "Desculpe, tivemos dificuldades ao carregar os vídeos."
        }
        showResponse(errorMessage)
    }

    private fun showResponse(resposta: String?) {
        binding.responseTextView.visibility = View.VISIBLE
        binding.responseLinearLayout.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE
        binding.toggleViewButton.text = "Ver Site"

        // Exibir o botão de fala apenas quando houver uma resposta
        binding.speakButton.visibility = if (resposta != null) View.VISIBLE else View.GONE

        // Ativa a fala automaticamente ao mostrar a resposta
        resposta?.let { speakResponse(it) }
    }

    private fun removeSpecialCharacters(input: String): String {
        return input.replace(Regex("[*]"), "") // Remove os asteriscos
    }

    private fun speakResponse(response: String) {
        filteredResponse = removeEmojis(response) // Filtra emojis

        if (textToSpeech.isSpeaking) {
            textToSpeech.stop() // Para a fala se já estiver falando
            Log.d("TTS", "Fala interrompida.")
            return
        }

        // Verifica se há texto para falar
        if (filteredResponse.isNullOrEmpty()) {
            Log.d("TTS", "Resposta vazia, nada para falar.")
            return
        }

        Log.d("TTS", "Falando: $filteredResponse")
        textToSpeech.speak(filteredResponse, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Método para parar a fala
    private fun stopSpeech() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop() // Para a fala
            Log.d("TTS", "Fala parada.")
        }
    }

    private fun removeEmojis(input: String): String {
        return input.replace(Regex("[^\\p{L}\\p{N}\\p{P}\\p{Z}]"), "")
    }

    private fun fetchSomeData() {
        val apiService = RetrofitClientInstance.retrofitInstance?.create(ApiService::class.java)
        val call = apiService?.getSomeData()

        call?.enqueue(object : Callback<Models.YourResponseType> {
            override fun onResponse(
                call: Call<Models.YourResponseType>,
                response: Response<Models.YourResponseType>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    // Faça algo com os dados recebidos, por exemplo:
                    Log.d("API Data", data.toString())
                } else {
                    Log.e("AppError", "Erro na resposta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Models.YourResponseType>, t: Throwable) {
                Log.e("AppError", "Erro de conexão: ${t.message}")
            }
        })
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale agora...")
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_VOICE_RECOGNITION)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Seu dispositivo não suporta reconhecimento de voz.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startResponseAnimation() {
        if (!currentResponse.isNullOrEmpty()) {
            binding.responseTextView.text = binding.responseTextView.text.toString() // Mantém o que já foi impresso
            stopGeneration = false // Reinicia o estado de geração

            val delay: Long = 50 // atraso entre as letras (em milissegundos)

            handler.post(object : Runnable {
                override fun run() {
                    if (currentIndex < currentResponse!!.length && !stopGeneration) {
                        binding.responseTextView.append(currentResponse!![currentIndex].toString())
                        currentIndex++
                        handler.postDelayed(this, delay)
                    } else {
                        binding.stopButton.visibility = View.GONE
                        binding.sendButton.visibility = View.VISIBLE
                    }
                }
            })
        } else {
            // Se não houver resposta, exiba uma mensagem padrão
            binding.responseTextView.text = "Nenhuma resposta disponível."
            binding.stopButton.visibility = View.GONE
            binding.sendButton.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_VOICE_RECOGNITION && resultCode == RESULT_OK) {
            data?.let {
                val results = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                results?.let { list ->
                    if (list.isNotEmpty()) {
                        val spokenText = list[0]
                        binding.questionEditText.setText(spokenText) // Define o texto reconhecido no EditText
                        fetchResponse(spokenText) // Envia a pergunta automaticamente
                    }
                }
            }
        }
    }


    private fun toggleView() {
        if (isWebViewVisible) {
            // Alterna para a visualização do WebView
            if (!isWebViewLoaded) {
                // Carrega a URL do site
                val url = "https:xxxxxxxxxxxxxxxxx.com"
                binding.webView.loadUrl(url)
                isWebViewLoaded = true
            }
            binding.webView.visibility = View.VISIBLE
            binding.responseLinearLayout.visibility = View.GONE // Esconde a resposta
            binding.toggleViewButton.text = "Tutor AI" // Muda o texto do botão
        } else {
            // Mostra a resposta estática, se já houver uma resposta
            if (currentResponse != null) {
                showResponse(currentResponse) // Chama o método para exibir a resposta
                startResponseAnimation() // Inicia a animação se houver uma resposta
            }
            binding.webView.visibility = View.GONE
            binding.toggleViewButton.text = "Ver Site" // Muda o texto do botão
        }
        isWebViewVisible = !isWebViewVisible // Alterna a visibilidade
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        // Libera os recursos do TextToSpeech ao destruir a atividade
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
