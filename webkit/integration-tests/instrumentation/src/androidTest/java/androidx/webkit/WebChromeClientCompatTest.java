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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.webkit.test.common.WebViewOnUiThread;
import androidx.webkit.test.common.WebkitUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.mockwebserver.MockWebServer;

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

        String data = "<html><meta name=\"theme-color\" content=\"red\"></html>";
        mWebViewOnUiThread.loadDataAndWaitForCompletion(data, "text/html", null);

        final Color themeColor = webChromeClient.waitForThemeColor();
        Assert.assertEquals(Color.RED, themeColor.toArgb());
    }

    private class MockWebChromeClient extends WebViewOnUiThread.WaitForProgressClient {
        private final BlockingQueue<Color> mThemeColors = new LinkedBlockingQueue<>();
        private final BlockingQueue<int> mViewportFits = new LinkedBlockingQueue<>();

        MockWebChromeClient() {
            super(mWebViewOnUiThread);
        }

        public Color waitForThemeColor() throws Exception {
            return WebkitUtils.waitForNextQueueElement(mThemeColors);
        }

        public int waitForViewportFit() throws Exception {
            return WebkitUtils.waitForNextQueueElement(mViewportFits);
        }

        @Override
        public void onReceivedThemeColor(WebView view, Color color) {
            super.onReceivedThemeColor(view, color);
            mThemeColors.add(color);
        }

        @Override
        public void onViewportFitChanged(WebView view, int viewportFit) {
            super.onViewportFitChanged(view, viewportFit);
            mViewportFits.add(viewportFit);
        }
    }
}
