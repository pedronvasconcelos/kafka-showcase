package dev.vasconcelos.weather.validator.infra.sql.repositories.jpa;

import dev.vasconcelos.weather.validator.infra.sql.WeatherAnomalyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWeatherAnomalyRepository extends JpaRepository<WeatherAnomalyEntity, Long> {
}
