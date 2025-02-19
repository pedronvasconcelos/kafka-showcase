namespace WeatherRadar.Application.Idempotency;

public interface IIdempotencyService
{
    public Task<bool> AddIdempotencyAsync(Idempotency idempotency, CancellationToken cancellationToken = default);
}