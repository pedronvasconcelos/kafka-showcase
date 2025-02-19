using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using WeatherRadar.Infra.Kafka;

namespace WeatherRadar.Producer.Injection;

public static class ConfigurationExtensions
{
    public static HostApplicationBuilder AddConfig(this HostApplicationBuilder builder)
    {
        builder.Services.Configure<KafkaConfig>(builder.Configuration.GetSection("Kafka"));
        return builder;
    }
}