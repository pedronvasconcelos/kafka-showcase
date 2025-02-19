using Amazon.DynamoDBv2.DataModel;
using WeatherRadar.Application.Idempotency;

namespace WeatherRadar.Infra.DynamoDB;

public class IdempotencyDynamoEntity
{
    [DynamoDBHashKey("idempotency_key")]
    public string IdempotencyKey { get; set; }

    [DynamoDBProperty("status")] 
    public string Status { get; set; }

    [DynamoDBProperty("expiry_timestamp")]
    public DateTime ExpiryTimestamp { get; set; }

    [DynamoDBProperty("payload_hash")]
    public string PayloadHash { get; set; }

    [DynamoDBProperty("expire_minutes")]
    public int ExpireMinutes { get; set; }

    [DynamoDBProperty("steps_completed")]
    public List<string> StepsCompleted { get; set; }


    public static IdempotencyDynamoEntity FromDomain(Idempotency domain)
    {
        return new IdempotencyDynamoEntity()
        {
            IdempotencyKey = domain.IdempotencyKey,
            Status = domain.Status.ToString(),
            ExpiryTimestamp = domain.ExpiryTimestamp,
            PayloadHash = domain.PayloadHash,
            ExpireMinutes = domain.ExpireMinutes,
            StepsCompleted = domain.StepsCompleted
        };
    }
}