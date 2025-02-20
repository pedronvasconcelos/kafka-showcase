package dev.vasconcelos.weather.validator.infra.sql;


import dev.vasconcelos.weather.validator.domain.WeatherData;

public class WeatherDataMapper {

    public static WeatherDataEntity toEntity(WeatherData weatherData) {
        if (weatherData == null) {
            return null;
        }
        return new WeatherDataEntity(
                weatherData.city,
                weatherData.latitude,
                weatherData.longitude,
                weatherData.temperature,
                weatherData.humidity,
                weatherData.conditions,
                weatherData.timestamp
        );
    }

    public static WeatherData toDomain(WeatherDataEntity entity) {
        if (entity == null) {
            return null;
        }
        return new WeatherData(
                entity.getCity(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getTemperature(),
                entity.getHumidity(),
                entity.getConditions(),
                entity.getTimestamp()
        );
    }
}