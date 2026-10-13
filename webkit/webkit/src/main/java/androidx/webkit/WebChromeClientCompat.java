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
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.annotation.UiThread;

import org.chromium.support_lib_boundary.WebChromeClientBoundaryInterface;
import org.chromium.support_lib_boundary.util.Features;
import org.jspecify.annotations.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Compatibility version of {@link android.webkit.WebChromeClient}.
 */
@SuppressWarnings("HiddenSuperclass")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class WebChromeClientCompat extends WebChromeClient implements
        WebChromeClientBoundaryInterface {
    private static final String[] sSupportedFeatures = new String[] {
        Features.THEME_COLOR_CALLBACK + Features.DEV_SUFFIX,
        Features.VIEWPORT_FIT_CALLBACK + Features.DEV_SUFFIX
    };

    /**
     * Returns the list of features this client supports. This feature list should always be a
     * subset of the Features declared in WebViewFeature.
     *
     */
    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public final String @NonNull [] getSupportedFeatures() {
        return sSupportedFeatures;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @IntDef(value = { VIEWPORT_FIT_AUTO, VIEWPORT_FIT_CONTAIN, VIEWPORT_FIT_COVER })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewportFitType {}

    /**
     * WebView document content should be displayed to ensure it is viewable on the screen.
     */
    public static final int VIEWPORT_FIT_AUTO =
            WebChromeClientBoundaryInterface.ViewportFitType.AUTO;

    /**
     * WebView document content should be displayed contained within the safe areas of the screen,
     * avoiding display cutouts and other obscured insets.
     */
    public static final int VIEWPORT_FIT_CONTAIN =
            WebChromeClientBoundaryInterface.ViewportFitType.CONTAIN;

    /**
     * WebView document content should be displayed covering the screen, including behind display
     * cutouts and other obscured areas.
     */
    public static final int VIEWPORT_FIT_COVER =
            WebChromeClientBoundaryInterface.ViewportFitType.COVER;

    /**
     * Notify the host application of a new theme color for the current page.
     *
     * <p>The theme color of a web page is specified using a {@code meta} tag
     * with {@code name="theme-color"} and the {@code content} attribute set to
     * a CSS color value. See <a
     * href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/meta/name/theme-color">
     * MDN theme-color docs</a> for more information.
     *
     * <p>This callback is only called for the main frame document.
     *
     * <p>This method is called only if {@link WebViewFeature#THEME_COLOR_CALLBACK} is supported.
     * You can check whether that flag is supported using {@link
     * WebViewFeature#isFeatureSupported(String)}.
     *
     * @param view The WebView that initiated the callback.
     * @param color A Color containing the new theme color of the document, or
     * {@link android.graphics.Color#TRANSPARENT} if there is no declared theme
     * color.
     */
    @Override
    @UiThread
    public void onReceivedThemeColor(@NonNull WebView view, @NonNull Color color) {}

    /**
     * Notify the host application of a change to the document's declared viewport fit layout.
     *
     * <p>The viewport fit of a web page is declared using a {@code meta} tag
     * with a {@code viewport-fit} directive in the {@code content} attribute.
     * The permissible values are {@code "auto"}, {@code "contain"}, and {@code
     * "cover"}. See <a href="https://webkit.org/blog/7929/designing-websites-for-iphone-x/">
     * the WebKit Blog</a> for more information about how these values cause web
     * content to interact with display cutout areas.
     *
     * <p>This callback is only called for the main frame document.
     *
     * <p>This method is called only if {@link WebViewFeature#VIEWPORT_FIT_CALLBACK} is supported.
     * You can check whether that flag is supported using {@link
     * WebViewFeature#isFeatureSupported(String)}.
     *
     * @param view The WebView that initiated the callback.
     * @param value The requested viewport fit layout for the document.
     */
    @Override
    @UiThread
    public void onViewportFitChanged(@NonNull WebView view, @ViewportFitType int value) {}
}
