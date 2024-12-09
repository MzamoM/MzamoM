package com.sos.msgroup.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sos.msgroup.R



class PaymentFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        var root: View = inflater.inflate(R.layout.fragment_payment, container, false)
        findViewByIds(root)
        return root
    }

    private fun findViewByIds(view: View) {
        webView = view.findViewById(R.id.webViewPayment)

        webView.settings.javaScriptEnabled = true


        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, weburl: String) {
                webView.visibility = View.VISIBLE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {

                if (newProgress == 100) {
                    webView.visibility = View.VISIBLE
                }
            }
        }

        webView.loadUrl("https://payf.st/dgryt")
    }

}