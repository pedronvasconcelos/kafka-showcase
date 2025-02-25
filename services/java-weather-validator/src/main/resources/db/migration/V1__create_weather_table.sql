CREATE TABLE weather_data (
                              id SERIAL PRIMARY KEY,
                              city VARCHAR(255) NOT NULL,
                              latitude DOUBLE PRECISION NOT NULL,
                              longitude DOUBLE PRECISION NOT NULL,
                              temperature DOUBLE PRECISION NOT NULL,
                              humidity INTEGER NOT NULL,
                              conditions VARCHAR(255) NOT NULL,
                              timestamp TIMESTAMP NOT NULL
);

CREATE INDEX idx_weather_data_city ON weather_data(city);
CREATE INDEX idx_weather_data_timestamp ON weather_data(timestamp);
CREATE INDEX idx_weather_data_coordinates ON weather_data(latitude, longitude);