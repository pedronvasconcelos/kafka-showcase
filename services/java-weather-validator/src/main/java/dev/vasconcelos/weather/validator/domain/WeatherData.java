package dev.vasconcelos.weather.validator.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

public class WeatherData {
    public  String city;
    public  double latitude;
    public  double longitude;
    public  double temperature;
    public  int humidity;
    public  String conditions;

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    public  LocalDateTime timestamp;

    public WeatherData() {
     }

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


    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getConditions() {
        return conditions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}