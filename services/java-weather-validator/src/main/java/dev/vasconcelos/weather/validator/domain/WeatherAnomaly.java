package dev.vasconcelos.weather.validator.domain;

import java.time.LocalDateTime;

public class WeatherAnomaly {
    private Long id;
    private String location;
    private Double temperature;
    private Double expectedValue;
    private Double deviation;
    private LocalDateTime timestamp;
    private String severity;



 }