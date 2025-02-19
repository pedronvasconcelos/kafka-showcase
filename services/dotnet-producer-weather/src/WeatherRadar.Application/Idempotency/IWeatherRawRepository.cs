using WeatherRadar.Domain;

namespace WeatherRadar.Application.Idempotency;

public interface IWeatherRawRepository
{
    public Task<bool> SaveRawData(WeatherData data);
}