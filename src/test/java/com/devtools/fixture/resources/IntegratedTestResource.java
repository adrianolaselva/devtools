package com.devtools.fixture.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class IntegratedTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String CONFLUENTINC_CP_KAFKA_IMAGE_NAME = "confluentinc/cp-kafka:6.2.1";
    private static final DockerImageName dockerImageName = DockerImageName.parse(CONFLUENTINC_CP_KAFKA_IMAGE_NAME);
    private static final KafkaContainer kafka = new KafkaContainer(dockerImageName);

    @Override
    public Map<String, String> start() {
        kafka.start();
        return configurationParameters();
    }

    private Map<String, String> configurationParameters() {
        final Map<String, String> conf = new HashMap<>();
        conf.put("%test-integrated.kafka.bootstrap.servers", kafka.getBootstrapServers());
        return conf;
    }

    @Override
    public void stop() {
        kafka.stop();
    }
}
