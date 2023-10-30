package com.devtools.application.commands.kafka;

import com.devtools.domain.exporter.enums.OutputType;
import com.devtools.domain.exporter.exceptions.ExportOutputFilePathException;
import com.devtools.domain.exporter.interfaces.Exporter;
import com.devtools.domain.transform.enums.TransformType;
import com.devtools.domain.transform.exceptions.RetrieveTransformException;
import com.devtools.domain.transform.interfaces.Transform;
import com.devtools.application.settings.CommandSettings;
import com.devtools.infrastructure.configs.ExportContextBean;
import com.devtools.infrastructure.configs.TransformContextBean;
import com.devtools.infrastructure.properties.DevToolsProperties;
import jakarta.enterprise.context.Dependent;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.jboss.logging.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Dependent
@Command(name = "kafka-consumer", helpCommand = true, mixinStandardHelpOptions = true, description = {
    CommandSettings.CLI_TOOLS_KAFKA_CONSUMER_DESCRIPTION
})
public class ConsumerCommand implements Runnable {

    private static final Logger logger = Logger.getLogger(ConsumerCommand.class);

    private static final DateTimeFormatter offsetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Option(names = {"-b", "--bootstrap-servers"}, required = true, description = "Bootstrap Servers", defaultValue = "127.0.0.1:9092")
    public String bootstrapServersParameter = "127.0.0.1:9092";

    @Option(names = {"-t", "--topic"}, required = true, description = "Topic Name")
    public String topicNameParameter;

    @Option(names = {"--pool-interval"}, description = "Poll interval in milliseconds", defaultValue = "1000")
    public Long poolIntervalParameter = 1000L;

    @Option(names = {"-o", "--offset"}, description = "Offset to format ex: [2023-01-10T20:00:00.000-0300]")
    public String offsetDateTimeParameter;

    @Option(names = {"--output-type"}, description = "Output Type (STDOUT, JSON_LINE, KAFKA)", defaultValue = "STDOUT")
    public OutputType outputTypeParameter;

    @Option(names = {"--output-path"}, description = "Output FilePath", defaultValue = "content-output.out")
    public String outputPathParameter;

    @Option(names = {"--output-topic"}, description = "Output Topic (Example: Kafka, SNS, etc...)", defaultValue = "content-output.out")
    public String outputTopicParameter;

    @Option(names = "-O", description = "Generic Output Properties (example for Kafka output: -OPbootstrap.servers=localhost:9093)")
    private Map<String, String> outputPropertiesParameter = new HashMap<>();

    @Option(names = {"--transform-type"}, description = "Transformation script type", defaultValue = "GROOVY")
    public TransformType transformtypeParameter;

    @Option(names = {"--transform-path"}, description = "Definition of script path if necessary to apply transformation")
    public String transformPathParameter;

    @Option(names = {"--transform-method"}, description = "Method name in script implementation", defaultValue = "apply")
    public String transformMethodParameter;

    @Option(names = "-P", description = "Kafka Consumer Properties (example: -Pfetch.max.bytes=10)")
    private Map<String, String> customConsumerPropertiesParameter = new HashMap<>();

    private boolean running = true;

    private final ExportContextBean exportContextBean;
    private final DevToolsProperties devToolsProperties;

    public ConsumerCommand(final ExportContextBean exportContextBean, final DevToolsProperties devToolsProperties) {
        this.exportContextBean = exportContextBean;
        this.devToolsProperties = devToolsProperties;
    }

    @Override
    public void run() {
        final var elapsed = new StopWatch();
        elapsed.start();

        try (final var consumer = new KafkaConsumer<String, String>(loadConsumerProperties())) {
            final var topicPartitions = consumer.partitionsFor(topicNameParameter).stream()
                .map(partitionInfo -> new TopicPartition(topicNameParameter, partitionInfo.partition()))
                .collect(Collectors.toSet());
            consumer.assign(topicPartitions);

            final var timestampsToSearch = topicPartitions.stream()
                .collect(toMap(topicPartition -> topicPartition, entry -> loadOffsetTimestampStarts()));
            final var endOffsetsTimestamp = consumer.offsetsForTimes(timestampsToSearch);
            endOffsetsTimestamp
                .entrySet().stream()
                .filter(topicPartitionOffsetAndTimestamp -> topicPartitionOffsetAndTimestamp.getValue() != null)
                .collect(toMap(Map.Entry::getKey,
                    offsetMetadata -> new OffsetAndMetadata(offsetMetadata.getValue().offset())))
                .forEach(consumer::seek);

            processDataStream(consumer);
        } catch (Exception ex) {
            logger.error("failure to process data stream", ex);
        } finally {
            logger.debugv("total execution time, elapsed {0}ms", elapsed.getTime(TimeUnit.MILLISECONDS));
        }
    }

    private void processDataStream(final KafkaConsumer<String, String> consumer) throws IOException {
        try (final var exporterInstance = loadExporterInstance()) {
            do {
                final var records = consumer.poll(Duration.ofMillis(poolIntervalParameter));
                exporterInstance.export(records);

                if (records.isEmpty()) {
                    logger.debugv("there are no more records to return from topic {0}", topicNameParameter);
                    running = false;
                }
            } while (running);
        } catch (ExportOutputFilePathException ex) {
            logger.error("failed to get exporter instance", ex);
            throw ex;
        }
    }

    private long loadOffsetTimestampStarts() {
        return this.offsetDateTimeParameter == null ? Instant.now().toEpochMilli()
                : OffsetDateTime.parse(offsetDateTimeParameter, offsetFormatter).toInstant().toEpochMilli();
    }

    private Exporter loadExporterInstance() {
        return exportContextBean.getInstance(outputTypeParameter == null ? OutputType.STDOUT : outputTypeParameter)
                .setOutputFilePath(this.outputPathParameter)
                .setOutputTopicName(this.outputTopicParameter)
                .setOutputProperties(this.outputPropertiesParameter)
                .setTransform(loadTransformInstance());
    }

    private Transform loadTransformInstance() {
        try {
            return transformPathParameter != null ? TransformContextBean.getInstance(transformtypeParameter)
                    .setFilePath(transformPathParameter)
                    .setMethodName(transformMethodParameter) : null;
        } catch (FileNotFoundException | ScriptException e) {
            throw new RetrieveTransformException(e);
        }
    }

    private Properties loadConsumerProperties() {
        final var properties = new Properties();
        properties.putAll(Objects.nonNull(devToolsProperties.kafkaConsumer())
                ? devToolsProperties.kafkaConsumer().properties()
                : Collections.emptyMap());

        if (bootstrapServersParameter != null) {
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersParameter);
        }

        properties.putAll(outputPropertiesParameter);

        if (!customConsumerPropertiesParameter.isEmpty()) {
            properties.putAll(customConsumerPropertiesParameter);
        }

        logger.infov("kafka loaded consumer properties: {0}", properties);

        return properties;
    }
}
