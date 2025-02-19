using Bogus;
using WeatherRadar.Domain;

namespace WeatherRadar.Infra.Mock;

public class WeatherMockService : IWeatherService
{
    private readonly Faker _faker;

    public WeatherMockService()
    {
        _faker = new Faker();
    }

    public async Task<WeatherData> GetWeatherData(int latitude, int longitude)
    {
        return new WeatherData()
        {
            City = _faker.Address.City(),
            Latitude = latitude,
            Longitude = longitude,
            Temperature = _faker.Random.Int(0, 100),
            Humidity = _faker.Random.Int(0, 100),
            Conditions = GetFakeCondition(_faker.Random.Int(0, 4)),
            Timestamp = DateTime.Now.ToUniversalTime(),
        };
    }



    private string GetFakeCondition(int index)
    {
        switch (index)
        {
            case 0:
                return "Sunny";
            case 1: 
                return "Cloudy";
            case 3:
                return "Rainy";
            case 2: 
                return "Snowy";
            default: 
                return "Sunny";
        }
    }
}