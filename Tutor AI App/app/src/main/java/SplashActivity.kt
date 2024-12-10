package com.example.sic_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var splashVideoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashVideoView = findViewById(R.id.splashVideoView)
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_video}")
        splashVideoView.setVideoURI(videoUri)

        splashVideoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false
            splashVideoView.start()
        }

        splashVideoView.setOnCompletionListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        splashVideoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Erro ao enviar o request", Toast.LENGTH_SHORT).show()
            finish()
            true
        }

        // Alternativa: Usar um temporizador para iniciar a próxima atividade
        // Handler().postDelayed({
        //     startActivity(Intent(this, MainActivity::class.java))
        //     finish()
        // }, 3000) // 3 segundos
    }

    override fun onPause() {
        super.onPause()
        splashVideoView.pause()
    }

    override fun onResume() {
        super.onResume()
        splashVideoView.start()
    }
}
