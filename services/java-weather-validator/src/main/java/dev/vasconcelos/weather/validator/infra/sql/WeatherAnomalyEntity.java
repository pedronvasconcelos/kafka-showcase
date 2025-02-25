package dev.vasconcelos.weather.validator.infra.sql;


import dev.vasconcelos.weather.validator.domain.AnomalySeverity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "weather_anomaly")
public class WeatherAnomalyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private AnomalySeverity severity;

    @ElementCollection
    @CollectionTable(name = "weather_anomaly_codes",
            joinColumns = @JoinColumn(name = "anomaly_id"))
    @Column(name = "anomaly_code")
    private List<String> anomalyCodes = new ArrayList<>();

    public WeatherAnomalyEntity() {
    }

    public WeatherAnomalyEntity(String location, Double temperature, LocalDateTime timestamp,
                                AnomalySeverity severity, List<String> anomalyCodes) {
        this.location = location;
        this.temperature = temperature;
        this.timestamp = timestamp;
        this.severity = severity;
        this.anomalyCodes = anomalyCodes;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public AnomalySeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AnomalySeverity severity) {
        this.severity = severity;
    }

    public List<String> getAnomalyCodes() {
        return anomalyCodes;
    }

    public void setAnomalyCodes(List<String> anomalyCodes) {
        this.anomalyCodes = anomalyCodes;
    }
}