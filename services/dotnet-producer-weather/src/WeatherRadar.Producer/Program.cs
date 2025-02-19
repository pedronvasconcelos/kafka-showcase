using Amazon.DynamoDBv2;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using WeatherRadar.Infra.DynamoDB;
using WeatherRadar.Producer.Injection;

namespace WeatherRadar.Producer;


internal class Program
{
    static async Task Main(string[] args)
    {
        var builder = Host.CreateApplicationBuilder(args);

        builder.Configuration.AddJsonFile("appsettings.json", optional: false, reloadOnChange: true);
        builder.Configuration.AddEnvironmentVariables();

        builder.AddConfig();
        builder.Services.AddServices()
            .AddDynamoDb(builder.Configuration)
            .AddPostgreSQL(builder.Configuration);

        var host = builder.Build();
        using (var scope = host.Services.CreateScope())
        {
            var dynamoDb = scope.ServiceProvider.GetRequiredService<IAmazonDynamoDB>();
            await DynamoDbSetup.EnsureTableExists(dynamoDb);
        }
        await host.RunAsync();
    }
}