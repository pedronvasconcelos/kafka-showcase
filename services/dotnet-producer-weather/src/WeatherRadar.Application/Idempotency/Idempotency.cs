namespace WeatherRadar.Application.Idempotency;

public class Idempotency
{
    public string IdempotencyKey { get; set; }
    public IdempotencyStatus Status { get; set; }
    public string PayloadHash { get; set; }
    public List<string> StepsCompleted { get; set; }

    public Idempotency(string idempotencyKey, string jsonPayload)
    {
        IdempotencyKey = idempotencyKey;
        Status = IdempotencyStatus.InProgress;
        PayloadHash = CalculateHash(jsonPayload);
        StepsCompleted = new List<string>() { IdempotencyStepConstants.ProducerStep };
    }

    private string CalculateHash(string jsonPayload)
    {
        using (var sha256 = System.Security.Cryptography.SHA256.Create())
        {
            var bytes = System.Text.Encoding.UTF8.GetBytes(jsonPayload);
            var hash = sha256.ComputeHash(bytes);
            return Convert.ToBase64String(hash);
        }
    }
}