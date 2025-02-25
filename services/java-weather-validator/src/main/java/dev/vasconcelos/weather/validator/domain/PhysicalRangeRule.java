package dev.vasconcelos.weather.validator.domain;

public class PhysicalRangeRule implements Rule {
    @Override
    public ValidationResult validate(WeatherData data) {
        double temperature = data.getTemperature();
        double humidity = data.getHumidity();


        // Check temperature range for tropical regions
        if (temperature < -30 || temperature > 50) {
            return new ValidationResult(false, getCode(),
                    "Temperature " + temperature + "°C outside physical range [-30°C, +50°C] ");
        }
        return new ValidationResult(true, getCode(), "Values within physical ranges");
    }

    @Override
    public String getCode() {
        return "Q1";
    }

    @Override
    public String getDescription() {
        return "Verify values outside possible physical ranges";
    }
}
