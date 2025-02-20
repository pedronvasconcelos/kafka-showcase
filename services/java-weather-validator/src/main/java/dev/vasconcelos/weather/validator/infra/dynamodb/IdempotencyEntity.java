package dev.vasconcelos.weather.validator.infra.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import dev.vasconcelos.weather.validator.application.idempotency.Idempotency;

import java.util.HashSet;
import java.util.Set;

@DynamoDBTable(tableName = "idempotency")
public class IdempotencyEntity {
    private String idempotencyKey;
    private String status;
     private String payloadHash;
    private int expireMinutes;
    private Set<String> stepsCompleted;

    // Default constructor required by DynamoDB
    public IdempotencyEntity() {}

    public IdempotencyEntity(Idempotency domain) {
        idempotencyKey = domain.getIdempotencyKey();
        status = domain.getStatus().toString();
         payloadHash = domain.getPayloadHash();
        expireMinutes = domain.getExpireMinutes();
        stepsCompleted = new HashSet<>(domain.getStepsCompleted());
    }

    @DynamoDBHashKey(attributeName = "idempotency_key")
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    @DynamoDBAttribute(attributeName = "payload_hash")
    public String getPayloadHash() { return payloadHash; }
    public void setPayloadHash(String payloadHash) { this.payloadHash = payloadHash; }

    @DynamoDBAttribute(attributeName = "expire_minutes")
    public int getExpireMinutes() { return expireMinutes; }
    public void setExpireMinutes(int expireMinutes) { this.expireMinutes = expireMinutes; }

    @DynamoDBAttribute(attributeName = "steps_completed")
    public Set<String> getStepsCompleted() { return stepsCompleted; }
    public void setStepsCompleted(Set<String> stepsCompleted) { this.stepsCompleted = stepsCompleted; }
}
