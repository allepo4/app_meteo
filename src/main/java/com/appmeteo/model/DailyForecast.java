package com.appmeteo.model;

import com.appmeteo.service.WeatherUtils;

public class DailyForecast {
    private final String date;
    private final int weatherCode;
    private final double tempMax;
    private final double tempMin;

    public DailyForecast(String date, int weatherCode, double tempMax, double tempMin) {
        this.date = date;
        this.weatherCode = weatherCode;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
    }

    public String getDate() { return date; }
    public int getWeatherCode() { return weatherCode; }
    public double getTempMax() { return tempMax; }
    public double getTempMin() { return tempMin; }

    public String getWeatherDescription() {
        return WeatherUtils.getWeatherDescription(weatherCode);
    }
}