package com.appmeteo.service;

import com.appmeteo.model.City;
import com.appmeteo.model.CurrentWeather;
import com.appmeteo.model.DailyForecast;
import com.appmeteo.model.HourlyForecast;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public City searchCity(String cityName) throws Exception {
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity
                + "&count=1&language=it&format=json";

        String body = fetchJson(url);
        if (!body.contains("\"results\"")) {
            throw new RuntimeException("Città non trovata: " + cityName);
        }

        SimpleJsonParser root = new SimpleJsonParser(body);
        SimpleJsonParser cityNode = root.getFirstObjectInArray("results");

        return new City(
                cityNode.getString("name"),
                cityNode.getDouble("latitude"),
                cityNode.getDouble("longitude")
        );
    }

    public CurrentWeather getCurrentWeather(City city) throws Exception {
        String url = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f"
                        + "&current=temperature_2m,wind_speed_10m,weather_code,is_day",
                city.getLatitude(), city.getLongitude());

        SimpleJsonParser current = new SimpleJsonParser(fetchJson(url)).getObject("current");

        return new CurrentWeather(
                current.getDouble("temperature_2m"),
                current.getDouble("wind_speed_10m"),
                current.getInt("weather_code"),
                current.getInt("is_day")
        );
    }

    public List<DailyForecast> getDailyForecast(City city) throws Exception {
        String url = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f"
                        + "&daily=weather_code,temperature_2m_max,temperature_2m_min"
                        + "&forecast_days=16&timezone=auto",
                city.getLatitude(), city.getLongitude());

        SimpleJsonParser daily = new SimpleJsonParser(fetchJson(url)).getObject("daily");

        List<String> dates = daily.getStringArray("time");
        List<Integer> codes = daily.getIntArray("weather_code");
        List<Double> max = daily.getDoubleArray("temperature_2m_max");
        List<Double> min = daily.getDoubleArray("temperature_2m_min");

        List<DailyForecast> list = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            list.add(new DailyForecast(dates.get(i), codes.get(i), max.get(i), min.get(i)));
        }
        return list;
    }

    public List<HourlyForecast> getHourlyPrecipitation(City city) throws Exception {
        String url = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f"
                        + "&hourly=temperature_2m,precipitation_probability,precipitation,weather_code"
                        + "&forecast_days=7&timezone=auto",
                city.getLatitude(), city.getLongitude());

        SimpleJsonParser hourly = new SimpleJsonParser(fetchJson(url)).getObject("hourly");

        List<String> times = hourly.getStringArray("time");
        List<Double> temps = hourly.getDoubleArray("temperature_2m");
        List<Integer> codes = hourly.getIntArray("weather_code");
        List<Integer> probs = hourly.getIntArray("precipitation_probability");
        List<Double> precs = hourly.getDoubleArray("precipitation");

        List<HourlyForecast> list = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            list.add(new HourlyForecast(
                    times.get(i),
                    codes.get(i),
                    temps.get(i),
                    i < probs.size() ? probs.get(i) : 0,
                    i < precs.size() ? precs.get(i) : 0.0
            ));
        }
        return list;
    }

    private String fetchJson(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}