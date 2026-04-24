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

package androidx.webkit;

import android.graphics.Color;
import android.webkit.WebView;

import androidx.concurrent.futures.ResolvableFuture;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.webkit.test.common.PollingCheck;
import androidx.webkit.test.common.WebViewOnUiThread;
import androidx.webkit.test.common.WebkitUtils;

import org.jspecify.annotations.NonNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class WebChromeClientCompatTest {
    private WebViewOnUiThread mWebViewOnUiThread;
    private MockWebServer mWebServer;

    @Before
    public void setUp() {
        mWebViewOnUiThread = new WebViewOnUiThread();
    }

    @After
    public void tearDown() throws IOException {
        if (mWebViewOnUiThread != null) {
            mWebViewOnUiThread.cleanUp();
        }

        if (mWebServer != null) {
            mWebServer.shutdown();
        }
    }

    @Test
    public void testOnReceivedThemeColor() throws Exception {
        WebkitUtils.checkFeature(WebViewFeature.THEME_COLOR_CALLBACK);

        final MockWebChromeClient webChromeClient = new MockWebChromeClient();
        mWebViewOnUiThread.setWebChromeClient(webChromeClient);

        Assert.assertFalse(webChromeClient.hadOnReceivedThemeColor());

        String data = "<html><meta name=\"theme-color\" content=\"red\"></html>";
        mWebViewOnUiThread.loadDataAndWaitForCompletion(data, "text/html", null);

        new PollingCheck(WebkitUtils.TEST_TIMEOUT_MS) {
            @Override
            protected boolean check() {
                return webChromeClient.hadOnReceivedThemeColor();
            }
        }.run();

        Assert.assertEquals(Color.RED, webChromeClient.getThemeColor().toArgb());
    }

    private class MockWebChromeClient extends WebViewOnUiThread.WaitForProgressClient {
        private boolean mHadOnReceivedThemeColor;
        private Color mThemeColor;
        private boolean mHadOnViewportFitChanged;
        private int mViewportFit;

        public MockWebChromeClient() {
            super(mWebViewOnUiThread);
        }

        public boolean hadOnReceivedThemeColor() {
            return mHadOnReceivedThemeColor;
        }

        public Color getThemeColor() {
            return mThemeColor;
        }

        public boolean hadOnViewportFitChanged() {
            return mHadOnViewportFitChanged;
        }

        public int getViewportFit() {
            return mViewportFit;
        }

        @Override
        public void onReceivedThemeColor(WebView view, Color color) {
            super.onReceivedThemeColor(view, color);
            mThemeColor = color;
            mHadOnReceivedThemeColor = true;
        }

        @Override
        public void onViewportFitChanged(WebView view, int viewportFit) {
            super.onViewportFitChanged(view, viewportFit);
            mViewportFit = viewportFit;
            mHadOnViewportFitChanged = true;
        }
    }
}
