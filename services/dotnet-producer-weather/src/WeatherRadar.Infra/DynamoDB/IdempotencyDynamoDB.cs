using System.Globalization;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using WeatherRadar.Application.Idempotency;

namespace WeatherRadar.Infra.DynamoDB;

public class IdempotencyDynamoDb : IIdempotencyService
{
    private readonly IAmazonDynamoDB _dynamoDbClient;
    private const string TableName = "idempotency";
    private const string IdempotencyKeyAttribute = "idempotency_key";

    public IdempotencyDynamoDb(IAmazonDynamoDB dynamoDbClient)
    {
        _dynamoDbClient = dynamoDbClient ?? throw new ArgumentNullException(nameof(dynamoDbClient));
    }

    public async Task<bool> AddIdempotencyAsync(Idempotency idempotency, CancellationToken cancellationToken = default)
    {
        ArgumentNullException.ThrowIfNull(idempotency);
        
        var entity = IdempotencyDynamoEntity.FromDomain(idempotency);
        
        var request = new PutItemRequest
        {
            TableName = TableName,
            Item = new Dictionary<string, AttributeValue>
            {
                [IdempotencyKeyAttribute] = new AttributeValue { S = entity.IdempotencyKey.ToString() },
                ["status"] = new AttributeValue { S = entity.Status.ToString() },
                ["payload_hash"] = new AttributeValue { S = entity.PayloadHash },
                ["steps_completed"] = new AttributeValue { SS = entity.StepsCompleted }
            },
            ConditionExpression = $"attribute_not_exists({IdempotencyKeyAttribute})"
        };

        try
        {
            var response = await _dynamoDbClient.PutItemAsync(request, cancellationToken);
            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }
        catch (ConditionalCheckFailedException)
        {
            return false;
        }
        catch (AmazonDynamoDBException ex)
        {
            throw new InvalidOperationException("Fail", ex);
        }
    }
}