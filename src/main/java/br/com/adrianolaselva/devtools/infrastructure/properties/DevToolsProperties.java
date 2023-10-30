package br.com.adrianolaselva.devtools.infrastructure.properties;


import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import jakarta.enterprise.context.Dependent;

import java.util.Map;


@Dependent
@ConfigMapping(prefix = "devtools.default-settings")
public interface DevToolsProperties {

    KafkaProperties kafkaConsumer();

    KafkaProperties kafkaProducer();

    interface KafkaProperties {

        @WithDefault("127.0.0.1:9092")
        String bootstrapServers();

        Map<String, String> properties();
    }
}
