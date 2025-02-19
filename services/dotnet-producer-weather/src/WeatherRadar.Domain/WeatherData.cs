namespace WeatherRadar.Domain;

public record WeatherData
{
    public string City { get; init; }
    public double Latitude { get; init; }
    public double Longitude { get; init; }
    public double Temperature { get; init; }
    public int Humidity { get; init; }
    public string Conditions { get; init; }
    public DateTime Timestamp { get; init; }
}