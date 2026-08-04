package com.appmeteo.controller;

import com.appmeteo.model.HourlyForecast;
import com.appmeteo.service.WeatherUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.Locale;

public class HourlyForecastCell extends ListCell<HourlyForecast> {

    @Override
    protected void updateItem(HourlyForecast h, boolean empty) {
        super.updateItem(h, empty);
        setPadding(Insets.EMPTY);

        if (empty || h == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("hourly-row");

        String[] parts = h.getTime().split("T");
        String timeStr = parts.length > 1 ? parts[1] : "";

        Label timeLabel = new Label(timeStr);
        timeLabel.getStyleClass().add("hourly-time");
        timeLabel.setMinWidth(55);

        javafx.scene.web.WebView icon = WeatherUtils.getStaticIcon(h.getWeatherCode(), true, 42);

        VBox conditionBox = new VBox(2);
        conditionBox.setAlignment(Pos.CENTER_LEFT);

        Label conditionLabel = new Label(WeatherUtils.getWeatherDescription(h.getWeatherCode()));
        conditionLabel.getStyleClass().add("hourly-condition");

        String rainText = (h.getPrecipitationProbability() > 0 || h.getPrecipitationMm() > 0)
                ? String.format(Locale.ITALIAN, "☔ %d%% · %.1f mm", h.getPrecipitationProbability(), h.getPrecipitationMm())
                : "Nessuna pioggia prevista";
        Label rainLabel = new Label(rainText);
        rainLabel.getStyleClass().add("hourly-rain");

        conditionBox.getChildren().addAll(conditionLabel, rainLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label tempLabel = new Label(Math.round(h.getTemperature()) + "°");
        tempLabel.getStyleClass().add("hourly-temp");

        row.getChildren().addAll(timeLabel, icon, conditionBox, spacer, tempLabel);
        setGraphic(row);
        setText(null);
    }
}