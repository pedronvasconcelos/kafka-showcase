package dev.vasconcelos.weather.validator.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vasconcelos.weather.validator.domain.ValidatorService;
import dev.vasconcelos.weather.validator.domain.WeatherAnomalyRepository;
import dev.vasconcelos.weather.validator.domain.WeatherRepository;
import org.apache.kafka.common.serialization.StringDeserializer;
import dev.vasconcelos.weather.validator.application.consumer.WeatherValidationConsumer;
import dev.vasconcelos.weather.validator.application.idempotency.IdempotencyService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableRetry
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${kafka.consumer.max-poll-records:500}")
    private int maxPollRecords;

    @Value("${kafka.consumer.concurrency:3}")
    private int concurrency;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);

         factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

         DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new FixedBackOff(1000L, 3));
        factory.setCommonErrorHandler(errorHandler);
        factory.setMissingTopicsFatal(false);
        return factory;
    }

    @Bean
    public WeatherValidationConsumer weatherValidationConsumer(
            IdempotencyService idempotencyService,
            ObjectMapper objectMapper,
            WeatherRepository weatherRepository,
            WeatherAnomalyRepository anomalyRepository) {
        return new WeatherValidationConsumer(idempotencyService, objectMapper, weatherRepository, anomalyRepository);
    }
}