package dev.vasconcelos.weather.validator.application.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vasconcelos.weather.validator.application.idempotency.Idempotency;
import dev.vasconcelos.weather.validator.application.idempotency.IdempotencyService;
import dev.vasconcelos.weather.validator.application.idempotency.IdempotencyStepConstants;
import dev.vasconcelos.weather.validator.domain.WeatherData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Predicate;

public class WeatherValidationConsumer {
    private static final String TOPIC = "${kafka.topics.weather-data}";
    private static final String IDEMPOTENCY_HEADER = "idempotency-key";
    private final Logger logger = LoggerFactory.getLogger(WeatherValidationConsumer.class);
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    public WeatherValidationConsumer(IdempotencyService idempotencyService, ObjectMapper objectMapper) {
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = TOPIC,
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Retryable(
            value = {Exception.class},
            exclude = {IllegalArgumentException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void consume(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(name = IDEMPOTENCY_HEADER, required = false) String idempotencyKey,
            Acknowledgment acknowledgment) {

        try {
            logger.info("Received message: topic={}, partition={}, offset={}, key={}",
                    topic, partition, offset, key);

            if (idempotencyKey == null || idempotencyKey.isEmpty()) {
                logger.error("Missing idempotency-key header");
                acknowledgment.acknowledge();
                return;
            }

            processMessageWithIdempotency(payload, idempotencyKey);

            // Commit offset after successful processing
            acknowledgment.acknowledge();

        } catch (Exception e) {
            logger.error("Error processing weather data from topic={}, partition={}, offset={}",
                    topic, partition, offset, e);
            // Don't acknowledge - will be retried based on @Retryable annotation
            throw e;
        }
    }

    private void processMessageWithIdempotency(String payload, String idempotencyKey) {
        idempotencyService.getIdempotency(idempotencyKey)
                .filter(Predicate.not(Idempotency::isValidationCompleted))
                .filter(idempotency -> !idempotency.getStepsCompleted()
                        .contains(IdempotencyStepConstants.VALIDATION_STEP))
                .ifPresentOrElse(
                        idempotency -> {
                            WeatherData weatherData = parseWeatherData(payload);
                            if (weatherData != null) {
                                processWeatherData(weatherData);
                                idempotency.completeStep();
                                idempotencyService.updateIdempotency(idempotency);
                                logger.info("Successfully processed message with idempotency key: {}", idempotencyKey);
                            } else {
                                logger.error("Failed to parse weather data: {}", payload);
                                throw new IllegalArgumentException("Invalid weather data format");
                            }
                        },
                        () -> logger.info("Skipping already processed message with idempotency key: {}", idempotencyKey)
                );
    }

    private void processWeatherData(WeatherData weatherData) {
        logger.info("Processing weather data: {}", weatherData);
    }

    private WeatherData parseWeatherData(String weatherData) {
        try {
            return objectMapper.readValue(weatherData, WeatherData.class);
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing weather data JSON", ex);
            return null;
        }
    }
}