package dev.vasconcelos.weather.validator.infra.sql.repositories;

import dev.vasconcelos.weather.validator.domain.WeatherAnomaly;
import dev.vasconcelos.weather.validator.domain.WeatherAnomalyRepository;
import dev.vasconcelos.weather.validator.infra.sql.WeatherAnomalyEntity;
import dev.vasconcelos.weather.validator.infra.sql.WeatherAnomalyMapper;
import dev.vasconcelos.weather.validator.infra.sql.repositories.jpa.JpaWeatherAnomalyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherAnomalyRepositoryImpl implements WeatherAnomalyRepository {

    private final JpaWeatherAnomalyRepository jpaRepository;

    @Autowired
    public WeatherAnomalyRepositoryImpl(JpaWeatherAnomalyRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(WeatherAnomaly anomaly) {
        WeatherAnomalyEntity entity = WeatherAnomalyMapper.toEntity(anomaly);
        jpaRepository.save(entity);
    }
}