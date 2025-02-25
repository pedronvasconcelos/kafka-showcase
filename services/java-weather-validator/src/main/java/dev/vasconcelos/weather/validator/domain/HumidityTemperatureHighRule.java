package dev.vasconcelos.weather.validator.domain;

class HumidityTemperatureHighRule implements Rule {
    @Override
    public ValidationResult validate(WeatherData data) {
        if (data.getHumidity() > 95 && data.getTemperature() > 30) {
            return new ValidationResult(false, getCode(),
                    "Invalid combination detected: Humidity " + data.getHumidity() +
                            "% with temperature " + data.getTemperature() + "°C");
        }
        return new ValidationResult(true, getCode(), "Humidity-temperature combination within limits");
    }

    @Override
    public String getCode() {
        return "D4";
    }

    @Override
    public String getDescription() {
        return "Identify humidity >95% with temperature >30°C";
    }
}