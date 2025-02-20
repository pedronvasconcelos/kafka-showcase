package dev.vasconcelos.weather.validator.domain;


import dev.vasconcelos.weather.validator.domain.WeatherData;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository {
    WeatherData save(WeatherData weatherData);
    Optional<WeatherData> findById(Long id);
    List<WeatherData> findAll();
    List<WeatherData> findByCity(String city);
    void delete(Long id);
    void update(WeatherData weatherData, Long id);
}
