package dev.vasconcelos.weather.validator.application.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vasconcelos.weather.validator.application.idempotency.Idempotency;
import dev.vasconcelos.weather.validator.application.idempotency.IdempotencyService;
import dev.vasconcelos.weather.validator.application.idempotency.IdempotencyStepConstants;
import dev.vasconcelos.weather.validator.domain.ValidatorService;
import dev.vasconcelos.weather.validator.domain.WeatherAnomaly;
import dev.vasconcelos.weather.validator.domain.WeatherAnomalyRepository;
import dev.vasconcelos.weather.validator.domain.WeatherData;
import dev.vasconcelos.weather.validator.domain.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class WeatherValidationConsumer {
    private static final String TOPIC = "${kafka.topics.weather-data}";
    private final Logger logger = LoggerFactory.getLogger(WeatherValidationConsumer.class);
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;
    private final ValidatorService validatorService;
    private final WeatherRepository weatherRepository;
    private final WeatherAnomalyRepository anomalyRepository;

    public WeatherValidationConsumer(IdempotencyService idempotencyService,
                                     ObjectMapper objectMapper,
                                     WeatherRepository weatherRepository,
                                     WeatherAnomalyRepository anomalyRepository) {
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
        this.validatorService = new ValidatorService();
        this.weatherRepository = weatherRepository;
        this.anomalyRepository = anomalyRepository;
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
            @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(name = "idempotency-key", required = false) byte[] idempotencyKeyBytes,
            Acknowledgment acknowledgment) {
        String idempotencyKey = extractIdempotencyKey(idempotencyKeyBytes);

        logger.info("Received message: idempotency-key={}, offset={}", idempotencyKey, offset);

        try {
            if (isInvalidIdempotencyKey(idempotencyKey)) {
                logger.error("Missing or empty idempotency-key header");
                acknowledgment.acknowledge();
                return;
            }

            processMessageWithIdempotency(payload, idempotencyKey);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            logger.error("Error processing message: idempotency-key={}, offset={}, error={}",
                    idempotencyKey, offset, e.getMessage(), e);
            throw new RuntimeException("Failed to process weather data message", e);
        }
    }

    private String extractIdempotencyKey(byte[] idempotencyKeyBytes) {
        return idempotencyKeyBytes != null ? new String(idempotencyKeyBytes, StandardCharsets.UTF_8) : null;
    }

    private boolean isInvalidIdempotencyKey(String idempotencyKey) {
        return idempotencyKey == null || idempotencyKey.isEmpty();
    }

    private void processMessageWithIdempotency(String payload, String idempotencyKey) {
        Optional<Idempotency> idempotencyOpt = idempotencyService.getIdempotency(idempotencyKey);

        if (shouldSkipProcessing(idempotencyOpt)) {
            logger.info("Skipping already processed message with idempotency key: {}", idempotencyKey);
            return;
        }

        WeatherData weatherData = parseWeatherData(payload);
        if (weatherData == null) {
            logger.error("Failed to parse weather data: {}", payload);
            throw new IllegalArgumentException("Invalid weather data format");
        }

        processWeatherData(weatherData);
        updateIdempotencyStatus(idempotencyOpt.get(), idempotencyKey);
    }

    private boolean shouldSkipProcessing(Optional<Idempotency> idempotencyOpt) {
        return idempotencyOpt.isEmpty() ||
                idempotencyOpt.get().isValidationCompleted() ||
                idempotencyOpt.get().getStepsCompleted().contains(IdempotencyStepConstants.VALIDATION_STEP);
    }

    private void updateIdempotencyStatus(Idempotency idempotency, String idempotencyKey) {
        idempotency.completeStep();
        idempotencyService.updateIdempotency(idempotency);
        logger.info("Successfully processed message with idempotency key: {}", idempotencyKey);
    }

    private void processWeatherData(WeatherData weatherData) {
        logger.debug("Processing weather data: {}", weatherData);
        verifyAnomaly(weatherData);
        weatherRepository.save(weatherData);
        logger.debug("Weather data saved successfully");
    }

    private void verifyAnomaly(WeatherData weatherData) {
        try {
            Optional<WeatherAnomaly> anomalyOpt = validatorService.validateWeather(weatherData);
            if (anomalyOpt.isPresent()) {
                WeatherAnomaly anomaly = anomalyOpt.get();
                anomalyRepository.save(anomaly);
                logger.info("Weather anomaly detected and saved: {}", anomaly);
            }
        } catch (Exception e) {
            logger.error("Error verifying weather anomalies: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to verify weather anomalies", e);
        }
    }

    private WeatherData parseWeatherData(String weatherData) {
        try {
            return objectMapper.readValue(weatherData, WeatherData.class);
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing weather data JSON: {}", ex.getMessage(), ex);
            return null;
        }
    }
}