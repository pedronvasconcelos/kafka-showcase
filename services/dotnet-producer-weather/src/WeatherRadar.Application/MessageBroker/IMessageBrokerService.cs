using WeatherRadar.Domain;

namespace WeatherRadar.Application.MessageBroker;

public interface IMessageBrokerService
{
    Task SendMessageAsync(string message);
}


public record WeatherMessage : WeatherData
{
    public string IdempotencyKey { get; private set; }

}