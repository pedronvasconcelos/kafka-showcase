package dev.vasconcelos.weather.validator.application.idempotency;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import dev.vasconcelos.weather.validator.infra.dynamodb.IdempotencyEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdempotencyService {
    private final DynamoDBMapper dynamoDBMapper;

    public IdempotencyService(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Optional<Idempotency> getIdempotency(String idempotencyKey) {
        try {
            IdempotencyEntity entity = dynamoDBMapper.load(IdempotencyEntity.class, idempotencyKey);
            return Optional.ofNullable(entity)
                    .map(Idempotency::new);
        } catch (AmazonDynamoDBException e) {
            throw new RuntimeException("Failed to get idempotency record", e);
        }
    }

    public boolean updateIdempotency(Idempotency idempotency) {
        try {
            IdempotencyEntity entity = new IdempotencyEntity(idempotency);

            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression()
                    .withExpectedEntry("idempotency_key",
                            new ExpectedAttributeValue()
                                    .withExists(true)
                                    .withValue(new AttributeValue().withS(idempotency.getIdempotencyKey())));

            dynamoDBMapper.save(entity, saveExpression);
            return true;
        } catch (ConditionalCheckFailedException e) {
            return false;
        } catch (AmazonDynamoDBException e) {
            throw new RuntimeException("Failed to update idempotency record", e);
        }
    }

}
