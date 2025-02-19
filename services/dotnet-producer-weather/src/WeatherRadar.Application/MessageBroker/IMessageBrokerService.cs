using WeatherRadar.Domain;

namespace WeatherRadar.Application.MessageBroker;

public interface IMessageBrokerService
{
    Task SendMessageAsync(string message, string idempotency);
}


 