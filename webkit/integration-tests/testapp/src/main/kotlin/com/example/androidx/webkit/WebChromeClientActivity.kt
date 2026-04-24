/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidx.webkit

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebChromeClientCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

/** An [Activity] to exercise [WebChromeClientCompat] related functionality. */
class WebChromeClientActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var statusBar: View

    private inner class DemoWebChromeClient : WebChromeClientCompat() {
        override fun onReceivedThemeColor(view: WebView, color: Color) {
            super.onReceivedThemeColor(view, color)

            statusBar.setBackgroundColor(color.toArgb())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().requestFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_web_chrome_client)
        setTitle(R.string.web_chrome_client_activity_title)
        appendWebViewVersionToTitle()

        if (!WebViewFeature.isFeatureSupported(WebViewFeature.THEME_COLOR_CALLBACK)) {
            showMessage(R.string.webkit_api_not_available)
            return
        }

        val assetLoader =
            WebViewAssetLoader.Builder()
                .setDomain(EXAMPLE_DOMAIN)
                .addPathHandler("$EXAMPLE_PATH/", WebViewAssetLoader.AssetsPathHandler(this))
                .build()

        statusBar = findViewById(R.id.status_bar_background)
        webView =
            findViewById<WebView>(R.id.webview).apply {
                webViewClient = AssetLoaderWebViewClient(assetLoader)
                webChromeClient = DemoWebChromeClient()
                settings.javaScriptEnabled = true
            }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) {
            v: View,
            insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

            val webViewLayout = webView.getLayoutParams()
            if (webViewLayout is FrameLayout.LayoutParams) {
                webViewLayout.setMargins(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                webView.setLayoutParams(webViewLayout)
            }

            val statusBarLayout = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, systemBars.top, Gravity.TOP)
            statusBar.setLayoutParams(statusBarLayout)

            insets
        }

        webView.loadUrl("https://$EXAMPLE_DOMAIN$EXAMPLE_PATH/www/chrome_client_demo.html")
    }

    companion object {
        private const val EXAMPLE_DOMAIN = "example.com"
        private const val EXAMPLE_PATH = "/androidx_webkit/example/assets"
    }
}
