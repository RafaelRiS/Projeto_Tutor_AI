package com.example.sic_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.sic_project.R

class MyWebFragment : Fragment() {

    private lateinit var webView: WebView
    private var isLoaded = false
    private val url = "https://xxxxxxxxxxxxxxx.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web, container, false)
        webView = view.findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        if (savedInstanceState != null) {
            // Restaura o estado do WebView
            webView.restoreState(savedInstanceState)
            isLoaded = true
        } else {
            webView.loadUrl(url)
            isLoaded = false
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Não destrua o WebView para manter o estado
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null && isLoaded) {
            // Garante que a posição de rolagem seja restaurada
            val scrollPosition = savedInstanceState.getInt("scroll_position", 0)
            webView.scrollTo(0, scrollPosition)
        }
    }

    // Implementação para capturar o estado de rolagem
    override fun onPause() {
        super.onPause()
        // Salva a posição de rolagem ao pausar
        val scrollY = webView.scrollY
        val args = Bundle()
        args.putInt("scroll_position", scrollY)
        this.arguments = args
    }

    override fun onResume() {
        super.onResume()
        // Restaura a posição de rolagem ao retomar
        val scrollPosition = arguments?.getInt("scroll_position", 0) ?: 0
        webView.scrollTo(0, scrollPosition)
    }
}







