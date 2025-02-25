package dev.vasconcelos.weather.validator.domain;

public interface Rule {
      ValidationResult validate(WeatherData data);
      String getCode();
      String getDescription();
}
