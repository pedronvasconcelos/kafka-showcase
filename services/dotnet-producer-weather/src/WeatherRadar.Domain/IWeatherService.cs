namespace WeatherRadar.Domain;

public interface IWeatherService
{
    Task<WeatherData> GetWeatherData(int latitude, int longitude);

}

