package dev.vasconcelos.weather.validator.infra.sql;

import dev.vasconcelos.weather.validator.domain.WeatherAnomaly;

public class WeatherAnomalyMapper {

    public static WeatherAnomalyEntity toEntity(WeatherAnomaly weatherAnomaly) {
        if (weatherAnomaly == null) {
            return null;
        }
        return new WeatherAnomalyEntity(
                weatherAnomaly.getLocation(),
                weatherAnomaly.getTemperature(),
                weatherAnomaly.getTimestamp(),
                weatherAnomaly.getSeverity(),
                weatherAnomaly.getAnomalyCodes()
        );
    }
}