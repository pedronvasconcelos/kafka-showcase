using System.ComponentModel.Design;
using Microsoft.Extensions.Logging;
using MongoDB.Driver;
using WeatherRadar.Application.Idempotency;
using WeatherRadar.Domain;

namespace WeatherRadar.Infra.Sql;

public class WeatherRawRepository : IWeatherRawRepository
{
    private readonly IMongoCollection<WeatherData> _collection;
 
    public WeatherRawRepository(string connectionString)
    {
         
        var client = new MongoClient(connectionString);
        var database = client.GetDatabase("weatherradar");
        _collection = database.GetCollection<WeatherData>("weather_data");

     }

    public async Task<bool> SaveRawData(WeatherData data)
    {
        try
        {
            await _collection.InsertOneAsync(data);
            return true;
        }
        catch (Exception ex)
        {
             return false;
        }
    }
}