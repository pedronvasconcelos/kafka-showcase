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
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Predicate;

@Service
public class WeatherValidationConsumer {
    private static final String TOPIC = "weather-data";
    private static final String IDEMPOTENCY_HEADER = "idempotency-key";
    private final Logger logger = LoggerFactory.getLogger(WeatherValidationConsumer.class);
    private final IdempotencyService idempotencyService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherValidationConsumer(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(topics = TOPIC, groupId = "weather-validation-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            logReceivedData(record);

            extractIdempotencyKey(record)
                    .flatMap(this::processIdempotency)
                    .ifPresentOrElse(
                            idempotency -> {
                                processWeatherData(record.value());
                                idempotency.completeStep();
                                updateIdempotency(idempotency);
                            },
                            () -> logger.info("Skipping already processed message")

                    );

        } catch (Exception e) {
            logger.error("Error processing weather data", e);
        }
    }

    private void logReceivedData(ConsumerRecord<String, String> record) {
        logger.info("Received weather data: key = {}, value = {}",
                record.key(), record.value());
    }

    private Optional<String> extractIdempotencyKey(ConsumerRecord<String, String> record) {
        return Optional.ofNullable(record.headers().lastHeader(IDEMPOTENCY_HEADER))
                .map(header -> new String(header.value()))
                .or(() -> {
                    logger.error("Missing idempotency-key header");
                    return Optional.empty();
                });
    }

    private Optional<Idempotency> processIdempotency(String idempotencyKey) {
        return idempotencyService.getIdempotency(idempotencyKey)
                .filter(Predicate.not(Idempotency::isValidationCompleted))
                .filter(idempotency -> !idempotency.getStepsCompleted()
                        .contains(IdempotencyStepConstants.VALIDATION_STEP));
    }

    private void processWeatherData(String weatherData) {
        var weather = parseWeatherData(weatherData);
    }

    private void updateIdempotency(Idempotency idempotency){
        idempotencyService.updateIdempotency(idempotency);
    }

    private WeatherData parseWeatherData(String weatherData)  {
        try{
             return objectMapper.readValue(weatherData, WeatherData.class);
        }
        catch(JsonProcessingException ex){
            return null;
        }
    }
}
