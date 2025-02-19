using System.Text.Json;
using Microsoft.Extensions.Hosting;
using WeatherRadar.Application.Idempotency;
using WeatherRadar.Application.MessageBroker;
using WeatherRadar.Domain;

namespace WeatherRadar.Producer.WorkerServices;

public class WeatherProducerService : BackgroundService
{
    private readonly IWeatherService _service;
    private readonly IMessageBrokerService _messageBrokerService;
    private readonly IIdempotencyService _idempotencyService;
    private readonly IWeatherRawRepository _rawRepository;


    public WeatherProducerService(IWeatherService service, 
        IMessageBrokerService messageBrokerService,
        IIdempotencyService idempotencyService,
        IWeatherRawRepository rawRepository)
    {
        _service = service;
        _messageBrokerService = messageBrokerService;
        _idempotencyService = idempotencyService;
        _rawRepository = rawRepository;
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

        var idempotencyKey = Guid.NewGuid().ToString();
        var weatherString = JsonSerializer.Serialize(weather);

        var idempotencyRecord = new Idempotency(idempotencyKey, weatherString);
        await _rawRepository.SaveRawData(weather);
        await _idempotencyService.AddIdempotencyAsync(idempotencyRecord);   
        await _messageBrokerService.SendMessageAsync(weatherString, idempotencyKey);
        
    }
}