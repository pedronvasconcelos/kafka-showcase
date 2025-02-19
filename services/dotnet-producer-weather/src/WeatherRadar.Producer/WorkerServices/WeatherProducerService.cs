using System.Text.Json;
using Microsoft.Extensions.Hosting;
using WeatherRadar.Application.Interfaces;
using WeatherRadar.Domain;

namespace WeatherRadar.Producer.WorkerServices;

public class WeatherProducerService : BackgroundService
{
    private readonly IWeatherService _service;
    private readonly IMessageBrokerService _messageBrokerService;


    public WeatherProducerService(IWeatherService service, IMessageBrokerService messageBrokerService)
    {
        _service = service;
        _messageBrokerService = messageBrokerService;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        
        while (!stoppingToken.IsCancellationRequested)
        {
            await Process();
            await Task.Delay(1000, stoppingToken);
        }
    }


    async Task Process()
    {
        var weather = await _service.GetWeatherData(1, 1);
        var weatherString = JsonSerializer.Serialize(weather);
        await _messageBrokerService.SendMessageAsync( weatherString);
        
    }
}