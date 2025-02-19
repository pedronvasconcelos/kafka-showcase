namespace WeatherRadar.Application.Interfaces;

public interface IMessageBrokerService
{
    Task SendMessageAsync(string message);
}