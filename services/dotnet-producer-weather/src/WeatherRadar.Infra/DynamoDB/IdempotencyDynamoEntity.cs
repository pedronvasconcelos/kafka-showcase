using Amazon.DynamoDBv2.DataModel;
using WeatherRadar.Application.Idempotency;

namespace WeatherRadar.Infra.DynamoDB;

public class IdempotencyDynamoEntity
{
    [DynamoDBHashKey("idempotency_key")]
    public string IdempotencyKey { get; set; }

    [DynamoDBProperty("status")] 
    public string Status { get; set; }

  
    [DynamoDBProperty("payload_hash")]
    public string PayloadHash { get; set; }
    
    [DynamoDBProperty("steps_completed")]
    public List<string> StepsCompleted { get; set; }


    public static IdempotencyDynamoEntity FromDomain(Idempotency domain)
    {
        return new IdempotencyDynamoEntity()
        {
            IdempotencyKey = domain.IdempotencyKey,
            Status = domain.Status.ToString(),
             PayloadHash = domain.PayloadHash,
             StepsCompleted = domain.StepsCompleted
        };
    }
}