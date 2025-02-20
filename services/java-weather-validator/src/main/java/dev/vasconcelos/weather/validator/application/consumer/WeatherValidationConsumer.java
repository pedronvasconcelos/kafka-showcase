package dev.vasconcelos.weather.validator.application.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WeatherValidationConsumer {
    private static final String TOPIC = "weather-data";
    private final Logger logger = LoggerFactory.getLogger(WeatherValidationConsumer.class);

    @KafkaListener(topics = TOPIC, groupId = "weather-validation-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            logger.info("Received weather data: key = {}, value = {}",
                    record.key(), record.value());

            validateWeatherData(record.value());

        } catch (Exception e) {
            logger.error("Error processing weather data", e);
        }
    }

    private void validateWeatherData(String weatherData) {
        System.out.println(weatherData);
    }
}