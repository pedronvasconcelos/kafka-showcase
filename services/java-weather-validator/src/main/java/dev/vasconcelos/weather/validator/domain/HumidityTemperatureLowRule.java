package dev.vasconcelos.weather.validator.domain;

class HumidityTemperatureLowRule implements Rule {
    @Override
    public ValidationResult validate(WeatherData data) {
        if (data.getHumidity() < 15 && data.getTemperature() > 35) {
            return new ValidationResult(false, getCode(),
                    "Extreme dry heat detected: Humidity " + data.getHumidity() +
                            "% with temperature " + data.getTemperature() + "°C");
        }

        return new ValidationResult(true, getCode(), "Humidity-temperature combination within limits");
    }

    @Override
    public String getCode() {
        return "D5";
    }

    @Override
    public String getDescription() {
        return "Detect humidity <15% with temperature >35°C";
    }
}

