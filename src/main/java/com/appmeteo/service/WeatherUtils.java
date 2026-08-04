package com.appmeteo.service;

import javafx.scene.web.WebView;

public class WeatherUtils {

    public static String getWeatherDescription(int code) {
        return switch (code) {
            case 0 -> "Cielo Sereno";
            case 1 -> "Prevalentemente Sereno";
            case 2 -> "Parzialmente Nuvoloso";
            case 3 -> "Nuvoloso";
            case 45, 48 -> "Nebbia";
            case 51, 53, 55 -> "Pioggerella";
            case 56, 57 -> "Pioggerella Gelata";
            case 61, 63, 65 -> "Pioggia";
            case 66, 67 -> "Pioggia Gelata";
            case 71, 73, 75, 77 -> "Neve";
            case 80, 81, 82 -> "Rovesci di Pioggia";
            case 85, 86 -> "Rovesci di Neve";
            case 95, 96, 99 -> "Temporale";
            default -> "Condizione Sconosciuta";
        };
    }

    public static String getSvgFileName(int code, boolean isDay) {
        return switch (code) {
            case 0 -> isDay ? "day.svg" : "night.svg";
            case 1, 2 -> isDay ? "cloudy-day-2.svg" : "cloudy-night-3.svg";
            case 3, 45, 48 -> "cloudy.svg";
            case 51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "rainy-7.svg";
            case 71, 73, 75, 77, 85, 86 -> "snowy-6.svg";
            case 95, 96, 99 -> "thunder.svg";
            default -> "cloudy.svg";
        };
    }

    /** Icona animata — usata per l'hero card grande in alto. */
    public static WebView getAnimatedIcon(int code, boolean isDay, double size) {
        return SvgIcon.create(getSvgFileName(code, isDay), size, true);
    }

    /** Icona statica (ferma) — usata nelle righe delle liste giorni/ore. */
    public static WebView getStaticIcon(int code, boolean isDay, double size) {
        return SvgIcon.create(getSvgFileName(code, isDay), size, false);
    }
}