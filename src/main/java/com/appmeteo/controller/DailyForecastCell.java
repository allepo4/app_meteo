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
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DailyForecastCell extends ListCell<DailyForecast> {

    private static final double MIN_FONT_SIZE = 8; // sotto questa dimensione non si rimpicciolisce più

    private final LocalDate today = LocalDate.now();

    public DailyForecastCell() {
        // la cella non impone la propria larghezza alla lista
        setPrefWidth(0);
    }

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
        // la riga ha sempre la larghezza della cella, né più né meno
        row.prefWidthProperty().bind(widthProperty());
        row.maxWidthProperty().bind(widthProperty());

        Label dayLabel = new Label(formatDay(d.getDate()));
        dayLabel.getStyleClass().add("forecast-day");
        dayLabel.setMinWidth(70);

        javafx.scene.web.WebView icon = WeatherUtils.getStaticIcon(d.getWeatherCode(), true, 34);
        icon.setMinWidth(Region.USE_PREF_SIZE);

        Label conditionLabel = new Label(d.getWeatherDescription());
        conditionLabel.getStyleClass().add("forecast-condition");
        conditionLabel.setMinWidth(0);

        // Contenitore che prende lo spazio libero (fa le veci dello spacer)
        StackPane conditionBox = new StackPane(conditionLabel);
        conditionBox.setAlignment(Pos.CENTER_LEFT);
        conditionBox.setMinWidth(0);
        conditionBox.setPrefWidth(0);
        HBox.setHgrow(conditionBox, Priority.ALWAYS);

        // Ogni volta che lo spazio disponibile cambia, adatto la dimensione del testo
        conditionBox.widthProperty().addListener((obs, oldW, newW) ->
                fitText(conditionLabel, newW.doubleValue()));

        Label minLabel = new Label(Math.round(d.getTempMin()) + "°");
        minLabel.getStyleClass().add("forecast-temp-min");
        minLabel.setMinWidth(Region.USE_PREF_SIZE);

        Label maxLabel = new Label(Math.round(d.getTempMax()) + "°");
        maxLabel.getStyleClass().add("forecast-temp-max");
        maxLabel.setMinWidth(Region.USE_PREF_SIZE);

        row.getChildren().addAll(dayLabel, icon, conditionBox, minLabel, maxLabel);
        setGraphic(row);
        setText(null);
    }

    /** Riduce la dimensione del font finché il testo entra nello spazio disponibile. */
    private void fitText(Label label, double availableWidth) {
        if (availableWidth <= 0) return;

        // Salvo il font originale (quello del CSS) la prima volta
        Font base = (Font) label.getProperties()
                .computeIfAbsent("baseFont", k -> label.getFont());

        double padding = label.getInsets().getLeft() + label.getInsets().getRight();
        double available = availableWidth - padding - 2; // piccolo margine di sicurezza

        Text measure = new Text(label.getText());
        measure.setFont(base);
        double textWidth = measure.getLayoutBounds().getWidth();

        if (textWidth <= available) {
            label.setStyle(""); // ci sta: dimensione originale
        } else {
            double size = Math.max(MIN_FONT_SIZE, base.getSize() * available / textWidth);
            label.setStyle("-fx-font-size: " + size + "px;");
        }
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