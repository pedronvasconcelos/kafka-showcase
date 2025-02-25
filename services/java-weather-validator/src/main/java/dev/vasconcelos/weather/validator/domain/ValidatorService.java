package dev.vasconcelos.weather.validator.domain;

import java.util.Optional;

public class ValidatorService {
    public Optional<WeatherAnomaly> validateWeather(WeatherData data){
        var ruleEngine = RuleEngine.create();
        var validationResults = ruleEngine.validate(data);
        var anomalySeverity = AnomalySeverity.fromValidations(validationResults);
        if(anomalySeverity == AnomalySeverity.NO_ANOMALY){
            return Optional.empty();
        }
        var weatherAnomaly = WeatherAnomaly.fromValidationResults(data, validationResults);
        return Optional.of(weatherAnomaly);
    }



}
