package com.devtools.fixture.configs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Map;

import static org.eclipse.microprofile.config.ConfigProvider.getConfig;

@ApplicationScoped
public class KafkaProducerTestBean {

    @Produces
    public KafkaProducer<String, String> kafkaProducer() {
        return new KafkaProducer<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getConfig().getValue("kafka.bootstrap.servers", String.class),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer",
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer"));
    }
}
