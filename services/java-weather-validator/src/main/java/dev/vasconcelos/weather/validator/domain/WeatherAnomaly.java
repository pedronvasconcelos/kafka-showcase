package dev.vasconcelos.weather.validator.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WeatherAnomaly {
    private Long id;
    private final String location;
    private final Double temperature;
    private final LocalDateTime timestamp;
    private final AnomalySeverity severity;
    private final List<String> anomalyCodes;

    public WeatherAnomaly(String location, Double temperature, LocalDateTime timestamp, AnomalySeverity severity, List<String> anomalyCodes) {
        this.location = location;
        this.temperature = temperature;
        this.timestamp = timestamp;
        this.severity = severity;
        this.anomalyCodes = anomalyCodes;
    }

    public static class Builder {
        private String location;
        private Double temperature;
        private LocalDateTime timestamp;
        private AnomalySeverity severity;
        private List<String> anomalyCodes = new ArrayList<>();

        public Builder() {
         }


        public Builder location(String location) {
            this.location = location;
            return this;
        }


        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }


        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }


        public Builder severity(AnomalySeverity severity) {
            this.severity = severity;
            return this;
        }

        public Builder anomalyCodes(List<String> anomalyCodes) {
            this.anomalyCodes = new ArrayList<>(anomalyCodes);
            return this;
        }

        public Builder addAnomalyCode(String code) {
            this.anomalyCodes.add(code);
            return this;
        }
        public WeatherAnomaly build() {
            if (location == null) {
                throw new IllegalStateException("Location cannot be null");
            }
            if (temperature == null) {
                throw new IllegalStateException("Temperature cannot be null");
            }
            if (timestamp == null) {
                throw new IllegalStateException("Timestamp cannot be null");
            }
            if (severity == null) {
                throw new IllegalStateException("Severity cannot be null");
            }

            return new WeatherAnomaly(location, temperature, timestamp, severity, anomalyCodes);
        }
    }


    public static Builder builder() {
        return new Builder();
    }


    public static WeatherAnomaly fromValidationResults(WeatherData data, List<ValidationResult> results) {
        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("Validation results cannot be null or empty");
        }

        // Extract failed validation codes
        List<String> failedCodes = results.stream()
                .filter(result -> !result.isValid())
                .map(ValidationResult::getRuleCode)
                .toList();

        AnomalySeverity severity = AnomalySeverity.fromValidations(results);

         return builder()
                .location(data.getCity())
                .temperature(data.getTemperature())
                .timestamp(data.getTimestamp())
                .severity(severity)
                .anomalyCodes(failedCodes)
                .build();
    }

    public String getLocation() { return location; }
    public Double getTemperature() { return temperature; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public AnomalySeverity getSeverity() { return severity; }
    public List<String> getAnomalyCodes() { return anomalyCodes; }

    @Override
    public String toString() {
        return "WeatherAnomaly{" +
                "location='" + location + '\'' +
                ", temperature=" + temperature +
                ", timestamp=" + timestamp +
                ", severity=" + severity +
                ", anomalyCodes=" + anomalyCodes +
                '}';
    }
}


