package com.appmeteo.service;

import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Mostra un'icona SVG dentro un WebView (JavaFX non sa disegnare SVG nativamente).
 * Con animate=false, l'animazione dell'SVG viene "congelata" via CSS — utile
 * per le icone piccole nelle liste, dove non serve muoversi e si risparmiano risorse.
 */
public class SvgIcon {

    public static WebView create(String fileName, double size, boolean animate) {
        WebView webView = new WebView();
        webView.setPrefSize(size, size);
        webView.setMaxSize(size, size);
        webView.setMinSize(size, size);
        webView.setPageFill(Color.TRANSPARENT);
        webView.setStyle("-fx-background-color: transparent;");
        webView.setMouseTransparent(true);

        String svgContent = readResource("/com/appmeteo/icons/" + fileName);
        if (svgContent == null) {
            System.err.println("ERRORE: SVG non trovato: " + fileName);
            return webView;
        }

        String freezeCss = animate ? "" : """
                *, *::before, *::after {
                    animation-play-state: paused !important;
                    animation-duration: 0s !important;
                    transition: none !important;
                }
                """;

        String html = """
                <html>
                <head>
                <style>
                    html, body { margin:0; padding:0; background: transparent; overflow: hidden; }
                    svg { width: 100%%; height: 100%%; }
                    %s
                </style>
                </head>
                <body>%s
                <script>
                    var svgEl = document.querySelector('svg');
                    if (!%b && svgEl && svgEl.pauseAnimations) { svgEl.pauseAnimations(); }
                </script>
                </body>
                </html>
                """.formatted(freezeCss, svgContent, animate);

        webView.getEngine().loadContent(html);
        return webView;
    }

    private static String readResource(String path) {
        try (InputStream is = SvgIcon.class.getResourceAsStream(path)) {
            if (is == null) return null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}