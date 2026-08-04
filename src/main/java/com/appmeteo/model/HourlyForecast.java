package com.appmeteo.model;

public class HourlyForecast {
    private String time;
    private int weatherCode;
    private double temperature;
    private int precipitationProbability;
    private double precipitationMm;

    public HourlyForecast() {}

    public HourlyForecast(String time, int weatherCode, double temperature, int precipitationProbability, double precipitationMm) {
        this.time = time;
        this.weatherCode = weatherCode;
        this.temperature = temperature;
        this.precipitationProbability = precipitationProbability;
        this.precipitationMm = precipitationMm;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getWeatherCode() { return weatherCode; }
    public void setWeatherCode(int weatherCode) { this.weatherCode = weatherCode; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getPrecipitationProbability() { return precipitationProbability; }
    public void setPrecipitationProbability(int precipitationProbability) { this.precipitationProbability = precipitationProbability; }

    public double getPrecipitationMm() { return precipitationMm; }
    public void setPrecipitationMm(double precipitationMm) { this.precipitationMm = precipitationMm; }
}