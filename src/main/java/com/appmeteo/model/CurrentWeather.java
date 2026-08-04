package com.appmeteo.model;

public class CurrentWeather {
    private final double temperature;
    private final double windSpeed;
    private final int weatherCode;
    private final int isDay;

    public CurrentWeather(double temperature, double windSpeed, int weatherCode, int isDay) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.weatherCode = weatherCode;
        this.isDay = isDay;
    }

    public double getTemperature() { return temperature; }
    public double getWindSpeed() { return windSpeed; }
    public int getWeatherCode() { return weatherCode; }
    public boolean isDay() { return isDay == 1; }
}