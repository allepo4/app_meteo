package com.appmeteo.controller;

import com.appmeteo.model.City;
import com.appmeteo.model.CurrentWeather;
import com.appmeteo.model.DailyForecast;
import com.appmeteo.model.HourlyForecast;
import com.appmeteo.service.RadarMapHtmlBuilder;
import com.appmeteo.service.WeatherService;
import com.appmeteo.service.WeatherUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML private TextField cityField;
    @FXML private Label statusLabel;
    @FXML private Label cityNameLabel;
    @FXML private Label currentTempLabel;
    @FXML private Label conditionLabel;
    @FXML private Label windLabel;
    @FXML private StackPane weatherIconContainer;
    @FXML private ListView<DailyForecast> dailyForecastList;
    @FXML private TabPane hourlyTabPane;
    @FXML private WebView radarWebView;
    @FXML private VBox radarCard;
    @FXML private Button expandRadarButton;

    private boolean radarExpanded = false;
    private final WeatherService weatherService = new WeatherService();

    @FXML
    public void initialize() {
        dailyForecastList.setCellFactory(list -> new DailyForecastCell());
    }

    @FXML
    private void onSearchClicked() {
        String cityName = cityField.getText();
        if (cityName == null || cityName.isBlank()) {
            statusLabel.setText("Inserisci un nome città");
            return;
        }

        statusLabel.setText("Ricerca in corso...");

        new Thread(() -> {
            try {
                City city = weatherService.searchCity(cityName);
                CurrentWeather current = weatherService.getCurrentWeather(city);
                List<DailyForecast> daily = weatherService.getDailyForecast(city);
                List<HourlyForecast> hourly = weatherService.getHourlyPrecipitation(city);

                Platform.runLater(() -> {
                    cityNameLabel.setText(city.getName());
                    currentTempLabel.setText(Math.round(current.getTemperature()) + "°");
                    conditionLabel.setText(WeatherUtils.getWeatherDescription(current.getWeatherCode()));
                    windLabel.setText("Vento " + Math.round(current.getWindSpeed()) + " km/h");

                    weatherIconContainer.getChildren().clear();
                    WebView icon = WeatherUtils.getAnimatedIcon(current.getWeatherCode(), current.isDay(), 110);
                    weatherIconContainer.getChildren().add(icon);

                    dailyForecastList.getItems().setAll(daily);

                    buildHourlyTabs(hourly);

                    radarWebView.getEngine().loadContent(RadarMapHtmlBuilder.build(city));

                    statusLabel.setText("");
                });

            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Errore: " + e.getMessage()));
            }
        }).start();
    }

    /** Raggruppa le ore per giorno e crea un tab per ciascuno dei prossimi 7 giorni. */
    private void buildHourlyTabs(List<HourlyForecast> hourly) {
        Map<String, List<HourlyForecast>> byDay = new LinkedHashMap<>();
        for (HourlyForecast h : hourly) {
            String dateStr = h.getTime().split("T")[0];
            byDay.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(h);
        }

        LocalDate today = LocalDate.now();
        hourlyTabPane.getTabs().clear();

        int count = 0;
        for (Map.Entry<String, List<HourlyForecast>> entry : byDay.entrySet()) {
            if (count >= 7) break;

            ListView<HourlyForecast> dayList = new ListView<>();
            dayList.getStyleClass().add("forecast-list");
            dayList.setCellFactory(list -> new HourlyForecastCell());
            dayList.getItems().setAll(entry.getValue());

            Tab tab = new Tab(formatTabLabel(entry.getKey(), today), dayList);
            hourlyTabPane.getTabs().add(tab);
            count++;
        }
    }

    private String formatTabLabel(String dateStr, LocalDate today) {
        try {
            LocalDate ld = LocalDate.parse(dateStr);
            if (ld.equals(today)) return "Oggi";
            if (ld.equals(today.plusDays(1))) return "Domani";
            String formatted = ld.format(DateTimeFormatter.ofPattern("EEE d", Locale.ITALIAN));
            return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        } catch (Exception e) {
            return dateStr;
        }
    }
    @FXML
    private void onToggleRadarSize() {
        radarExpanded = !radarExpanded;
        radarCard.setPrefHeight(radarExpanded ? 650 : 320);
        expandRadarButton.setText(radarExpanded ? "⤡" : "⤢");
    }
}