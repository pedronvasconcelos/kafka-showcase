 CREATE TABLE weather_anomaly (
                                 id BIGSERIAL PRIMARY KEY,
                                 location VARCHAR(255) NOT NULL,
                                 temperature DOUBLE PRECISION NOT NULL,
                                 timestamp TIMESTAMP NOT NULL,
                                 severity VARCHAR(50) NOT NULL
);

 CREATE TABLE weather_anomaly_codes (
                                       anomaly_id BIGINT NOT NULL,
                                       anomaly_code VARCHAR(255),
                                       FOREIGN KEY (anomaly_id) REFERENCES weather_anomaly(id)
);