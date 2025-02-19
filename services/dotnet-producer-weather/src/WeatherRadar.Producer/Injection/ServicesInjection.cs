using Confluent.Kafka;
using Microsoft.Extensions.DependencyInjection;
using WeatherRadar.Application.Interfaces;
using WeatherRadar.Domain;
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
        services.AddHostedService<WeatherProducerService>();
        return services;
    }
    
}