package dev.vasconcelos.weather.validator.infra.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import dev.vasconcelos.weather.validator.application.idempotency.Idempotency;

import java.util.Date;
import java.util.List;

@DynamoDBTable(tableName = "idempotency")
public class IdempotencyEntity {
    private String idempotencyKey;
    private String status;
    private Date expiryTimestamp;
    private String payloadHash;
    private int expireMinutes;
    private List<String> stepsCompleted;

    @DynamoDBHashKey(attributeName = "idempotency_key")
    public String getIdempotencyKey() { return idempotencyKey; }

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() { return status; }

    @DynamoDBAttribute(attributeName = "expiry_timestamp")
    public Date getExpiryTimestamp() { return expiryTimestamp; }

    @DynamoDBAttribute(attributeName = "payload_hash")
    public String getPayloadHash() { return payloadHash; }

    @DynamoDBAttribute(attributeName = "expire_minutes")
    public int getExpireMinutes() { return expireMinutes; }

    @DynamoDBAttribute(attributeName = "steps_completed")
    public List<String> getStepsCompleted() { return stepsCompleted; }

    public IdempotencyEntity(Idempotency domain){
        idempotencyKey = domain.getIdempotencyKey();
        status = domain.getStatus().toString();
        expiryTimestamp = domain.getExpiryTimestamp();
        payloadHash = domain.getPayloadHash();
        expireMinutes = domain.getExpireMinutes();
        stepsCompleted = domain.getStepsCompleted();
    }

}