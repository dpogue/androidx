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
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebChromeClientCompat
import androidx.webkit.WebViewAssetLoader
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

        setContentView(R.layout.activity_web_chrome_client)
        setTitle(R.string.web_chrome_client_activity_title)
        setUpDemoAppActivity()

        if (!WebViewFeature.isFeatureSupported(WebViewFeature.THEME_COLOR_CALLBACK)) {
            showMessage(R.string.webkit_api_not_available)
            return
        }

        val actionBar = getSupportActionBar()
        actionBar?.hide()

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
            _: View,
            insets: WindowInsetsCompat ->
            val types =
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            val systemBars = insets.getInsets(types)

            val webViewLayout = webView.getLayoutParams()
            if (webViewLayout is FrameLayout.LayoutParams) {
                webViewLayout.setMargins(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom,
                )
                webView.setLayoutParams(webViewLayout)
            }

            val statusBarLayout =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    systemBars.top,
                    Gravity.TOP,
                )
            statusBar.setLayoutParams(statusBarLayout)

            WindowInsetsCompat.Builder(insets).setInsets(types, Insets.NONE).build()
        }

        webView.loadUrl("https://$EXAMPLE_DOMAIN$EXAMPLE_PATH/www/chrome_client_demo.html")
    }

    companion object {
        private const val EXAMPLE_DOMAIN = "example.com"
        private const val EXAMPLE_PATH = "/androidx_webkit/example/assets"
    }
}
