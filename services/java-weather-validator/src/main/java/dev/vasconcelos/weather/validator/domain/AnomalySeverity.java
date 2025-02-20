package dev.vasconcelos.weather.validator.domain;

public enum AnomalySeverity {
    LOW,
    MEDIUM,
    HIGH;

    public static AnomalySeverity fromDeviation(double deviation) {
        if (deviation > 10) return HIGH;
        if (deviation > 5) return MEDIUM;
        return LOW;
    }
}