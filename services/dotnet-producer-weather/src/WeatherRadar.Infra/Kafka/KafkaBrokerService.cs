using Confluent.Kafka;
using Microsoft.Extensions.Options;
using WeatherRadar.Application.MessageBroker;

namespace WeatherRadar.Infra.Kafka;

public class KafkaBrokerService : IMessageBrokerService
{
    private readonly IProducer<Null, string> _producer;
    private string _topicName { get; set; }

    public KafkaBrokerService(IOptions<KafkaConfig> kafkaConfig)
    {
        var config = new ProducerConfig
        {
            BootstrapServers = kafkaConfig.Value.Server,
        };
        _producer = new ProducerBuilder<Null, string>(config).Build();
        _topicName = kafkaConfig.Value.TopicName;   
    }

    public async Task SendMessageAsync(string message)
    {
        var response = await _producer.ProduceAsync(_topicName, new Message<Null, string> { Value = message });

        response.Message.ToString();
    }
}