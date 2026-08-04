package com.appmeteo.controller;

import com.appmeteo.model.DailyForecast;
import com.appmeteo.service.WeatherUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DailyForecastCell extends ListCell<DailyForecast> {

    private final LocalDate today = LocalDate.now();

    @Override
    protected void updateItem(DailyForecast d, boolean empty) {
        super.updateItem(d, empty);
        setPadding(Insets.EMPTY);

        if (empty || d == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("forecast-row");

        Label dayLabel = new Label(formatDay(d.getDate()));
        dayLabel.getStyleClass().add("forecast-day");
        dayLabel.setMinWidth(70);

        javafx.scene.web.WebView icon = WeatherUtils.getStaticIcon(d.getWeatherCode(), true, 34);

        Label conditionLabel = new Label(d.getWeatherDescription());
        conditionLabel.getStyleClass().add("forecast-condition");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label minLabel = new Label(Math.round(d.getTempMin()) + "°");
        minLabel.getStyleClass().add("forecast-temp-min");

        Label maxLabel = new Label(Math.round(d.getTempMax()) + "°");
        maxLabel.getStyleClass().add("forecast-temp-max");

        row.getChildren().addAll(dayLabel, icon, conditionLabel, spacer, minLabel, maxLabel);
        setGraphic(row);
        setText(null);
    }

    private String formatDay(String dateStr) {
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
}