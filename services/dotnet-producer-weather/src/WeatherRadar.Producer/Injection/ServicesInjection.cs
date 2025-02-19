using Amazon.DynamoDBv2;
using Amazon.Runtime;
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using WeatherRadar.Application.Idempotency;
using WeatherRadar.Application.MessageBroker;
using WeatherRadar.Domain;
using WeatherRadar.Infra.DynamoDB;
using WeatherRadar.Infra.Kafka;
using WeatherRadar.Infra.Mock;
using WeatherRadar.Producer.WorkerServices;

namespace WeatherRadar.Producer.Injection;

public static class ServicesInjection
{

    public static IServiceCollection AddServices(this IServiceCollection services)
    {
        services.AddScoped<IWeatherService, WeatherMockService>();
        services.AddScoped<IMessageBrokerService, KafkaBrokerService>();
        services.AddScoped<IIdempotencyService, IdempotencyDynamoDb>();
        services.AddHostedService<WeatherProducerService>();
        return services;
    }

    public static IServiceCollection AddDynamoDb(this IServiceCollection services, IConfiguration configuration)
    {
        var dynamoDbConfig = configuration.GetSection("DynamoDb");
        var clientConfig = new AmazonDynamoDBConfig
        {
            ServiceURL = dynamoDbConfig.GetValue<string>("ServiceUrl") ?? "http://localhost:8000",
            UseHttp = true
        };

        var credentials = new BasicAWSCredentials(
            dynamoDbConfig.GetValue<string>("AccessKey") ?? "dummy",
            dynamoDbConfig.GetValue<string>("SecretKey") ?? "dummy"
        );

        services.AddSingleton<IAmazonDynamoDB>(_ => 
            new AmazonDynamoDBClient(credentials, clientConfig));
    
        return services;
    }
    
}