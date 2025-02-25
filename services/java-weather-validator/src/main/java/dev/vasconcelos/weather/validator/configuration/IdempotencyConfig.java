package dev.vasconcelos.weather.validator.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dev.vasconcelos.weather.validator.application.idempotency.IdempotencyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotencyConfig {

    @Bean
    public IdempotencyService idempotencyService(DynamoDBMapper dynamoDBMapper) {
        return new IdempotencyService(dynamoDBMapper);
    }
}