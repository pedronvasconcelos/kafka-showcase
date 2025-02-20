package dev.vasconcelos.weather.validator.application.idempotency;

import dev.vasconcelos.weather.validator.infra.dynamodb.IdempotencyEntity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Idempotency {
    private String idempotencyKey;
    private IdempotencyStatus status;
    private Date expiryTimestamp;
    private String payloadHash;
    private int expireMinutes = 60;
    private List<String> stepsCompleted;

    public Idempotency(IdempotencyEntity entity){
        idempotencyKey = entity.getIdempotencyKey();
        status = IdempotencyStatus.valueOf(entity.getStatus());
        expiryTimestamp = entity.getExpiryTimestamp();
        payloadHash = entity.getPayloadHash();
        expireMinutes = entity.getExpireMinutes();
        stepsCompleted = entity.getStepsCompleted();
    }

    public IdempotencyStatus getStatus() {
        return status;
    }

    public Date getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    public int getExpireMinutes() {
        return expireMinutes;
    }

    public List<String> getStepsCompleted() {
        return stepsCompleted;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void completeStep() {
        stepsCompleted.add(IdempotencyStepConstants.ValidationStep);
    }
}
