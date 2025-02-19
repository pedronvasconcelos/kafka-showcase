using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
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
        builder.Services.AddServices();

        var host = builder.Build();
        await host.RunAsync();
    }
}