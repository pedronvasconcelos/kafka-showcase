package dev.vasconcelos.weather.validator.infra.sql;

import dev.vasconcelos.weather.validator.domain.WeatherData;
import dev.vasconcelos.weather.validator.domain.WeatherRepository;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class WeatherRepositoryImpl implements WeatherRepository {

    private final JdbcTemplate jdbcTemplate;

    public WeatherRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public WeatherData save(WeatherData weatherData) {
        WeatherDataEntity entity = WeatherDataMapper.toEntity(weatherData);

        String sql = """
            INSERT INTO weather_data 
            (city, latitude, longitude, temperature, humidity, conditions, timestamp) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                entity.getCity(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getTemperature(),
                entity.getHumidity(),
                entity.getConditions(),
                Timestamp.valueOf(entity.getTimestamp())
        );

        return weatherData;
    }

    @Override
    public Optional<WeatherData> findById(Long id) {
        String sql = "SELECT * FROM weather_data WHERE id = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
            WeatherDataEntity entity = new WeatherDataEntity();
            entity.setId(rs.getLong("id"));
            entity.setCity(rs.getString("city"));
            entity.setLatitude(rs.getDouble("latitude"));
            entity.setLongitude(rs.getDouble("longitude"));
            entity.setTemperature(rs.getDouble("temperature"));
            entity.setHumidity(rs.getInt("humidity"));
            entity.setConditions(rs.getString("conditions"));
            entity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            return WeatherDataMapper.toDomain(entity);
        }).stream().findFirst();
    }

    @Override
    public List<WeatherData> findAll() {
        String sql = "SELECT * FROM weather_data";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            WeatherDataEntity entity = new WeatherDataEntity();
            entity.setId(rs.getLong("id"));
            entity.setCity(rs.getString("city"));
            entity.setLatitude(rs.getDouble("latitude"));
            entity.setLongitude(rs.getDouble("longitude"));
            entity.setTemperature(rs.getDouble("temperature"));
            entity.setHumidity(rs.getInt("humidity"));
            entity.setConditions(rs.getString("conditions"));
            entity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            return WeatherDataMapper.toDomain(entity);
        });
    }

    @Override
    public List<WeatherData> findByCity(String city) {
        String sql = "SELECT * FROM weather_data WHERE city = ?";

        return jdbcTemplate.query(sql, new Object[]{city}, (rs, rowNum) -> {
            WeatherDataEntity entity = new WeatherDataEntity();
            entity.setId(rs.getLong("id"));
            entity.setCity(rs.getString("city"));
            entity.setLatitude(rs.getDouble("latitude"));
            entity.setLongitude(rs.getDouble("longitude"));
            entity.setTemperature(rs.getDouble("temperature"));
            entity.setHumidity(rs.getInt("humidity"));
            entity.setConditions(rs.getString("conditions"));
            entity.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            return WeatherDataMapper.toDomain(entity);
        });
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM weather_data WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(WeatherData weatherData, Long id) {
        WeatherDataEntity entity = WeatherDataMapper.toEntity(weatherData);

        String sql = """
            UPDATE weather_data 
            SET city = ?, latitude = ?, longitude = ?, 
                temperature = ?, humidity = ?, conditions = ?, timestamp = ? 
            WHERE id = ?
        """;

        jdbcTemplate.update(sql,
                entity.getCity(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getTemperature(),
                entity.getHumidity(),
                entity.getConditions(),
                Timestamp.valueOf(entity.getTimestamp()),
                id
        );
    }
}
