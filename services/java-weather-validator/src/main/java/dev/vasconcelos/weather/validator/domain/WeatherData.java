package dev.vasconcelos.weather.validator.domain;

import java.time.LocalDateTime;

public class WeatherData {
    public final String city;
    public final double latitude;
    public final double longitude;
    public final double temperature;
    public final int humidity;
    public final String conditions;
    public final LocalDateTime timestamp;

    public WeatherData(String city, double latitude, double longitude, double temperature,
                       int humidity, String conditions, LocalDateTime timestamp) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.humidity = humidity;
        this.conditions = conditions;
        this.timestamp = timestamp;
    }
}