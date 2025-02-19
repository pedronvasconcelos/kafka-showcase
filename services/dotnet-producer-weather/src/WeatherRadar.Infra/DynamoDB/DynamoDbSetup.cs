using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace WeatherRadar.Infra.DynamoDB;

public static class DynamoDbSetup
{
    private const string TableName = "idempotency";
    private const string IdempotencyKeyAttribute = "idempotency_key";
    
    public static async Task EnsureTableExists(IAmazonDynamoDB dynamoDbClient)
    {
        ArgumentNullException.ThrowIfNull(dynamoDbClient);

        try
        {
            var request = new CreateTableRequest
            {
                TableName = TableName,
                AttributeDefinitions = new List<AttributeDefinition>
                {
                    new AttributeDefinition
                    {
                        AttributeName = IdempotencyKeyAttribute,
                        AttributeType = ScalarAttributeType.S
                    }
                },
                KeySchema = new List<KeySchemaElement>
                {
                    new KeySchemaElement
                    {
                        AttributeName = IdempotencyKeyAttribute,
                        KeyType = KeyType.HASH
                    }
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 5,
                    WriteCapacityUnits = 5
                }
            };

            await dynamoDbClient.CreateTableAsync(request);
            await WaitUntilTableReady(dynamoDbClient);
        }
        catch (ResourceInUseException)
        {
            // Tabela já existe
        }
    }

    private static async Task WaitUntilTableReady(IAmazonDynamoDB dynamoDbClient)
    {
        string status = null;
        var retryCount = 0;
        const int maxRetries = 10;

        do
        {
            try
            {
                var response = await dynamoDbClient.DescribeTableAsync(new DescribeTableRequest { TableName = TableName });
                status = response.Table.TableStatus;
                
                if (status != "ACTIVE")
                {
                    retryCount++;
                    await Task.Delay(TimeSpan.FromSeconds(2));
                }
            }
            catch (ResourceNotFoundException)
            {
                retryCount++;
                await Task.Delay(TimeSpan.FromSeconds(2));
            }
        } while (status != "ACTIVE" && retryCount < maxRetries);

        if (status != "ACTIVE")
        {
            throw new TimeoutException($"Tabela {TableName} não ficou ativa após {maxRetries} tentativas");
        }
    }
}