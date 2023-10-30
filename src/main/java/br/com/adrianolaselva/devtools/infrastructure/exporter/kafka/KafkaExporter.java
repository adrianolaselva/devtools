package br.com.adrianolaselva.devtools.infrastructure.exporter.kafka;

import br.com.adrianolaselva.devtools.domain.exporter.interfaces.Exporter;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import br.com.adrianolaselva.devtools.infrastructure.properties.DevToolsProperties;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.logging.Logger;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

@ApplicationScoped
public final class KafkaExporter implements Exporter {

    private static final Logger logger = Logger.getLogger(KafkaExporter.class);

    private final Map<String, String> outputProperties = new HashMap<>();
    private String outputTopicName;
    private Long totalExported = 0L;
    private Long totalBytesExported = 0L;
    private Transform transform;
    private KafkaProducer<String, String> kafkaProducer;

    private final DevToolsProperties devToolsProperties;

    public KafkaExporter(final DevToolsProperties devToolsProperties) {
        this.devToolsProperties = devToolsProperties;
    }

    @Override
    public void export(final ConsumerRecords<String, String> records) {
        final var elapsed = new StopWatch();
        try {
            elapsed.start();
            initializeKafkaProducer();

            final var batchSize = records.count();
            records.iterator().forEachRemaining(this::processRecord);

            totalExported += batchSize;
            logger.infov("batch exported from {0}, totaling {1} records for topic {2}, total exported: {3}",
                batchSize, totalExported, outputTopicName, byteCountToDisplaySize(totalBytesExported));
        } finally {
            kafkaProducer.flush();
            logger.debugv("export execution, elapsed {0}ms", elapsed.getTime(TimeUnit.MILLISECONDS));
        }
    }

    private void processRecord(final ConsumerRecord<String, String> consumerRecord) {
        final String payload;
        try {
            payload = transform != null
                ? transform.apply(consumerRecord.value()) : consumerRecord.value();
            totalBytesExported += payload.getBytes().length;

            kafkaProducer.send(new ProducerRecord<>(outputTopicName, consumerRecord.key(), payload));
        } catch (ScriptException | NoSuchMethodException e) {
            logger.errorv("failed to apply transform: [payload: {0}, message: {1}]",
                consumerRecord.value(), e.getMessage());
        }
    }

    public void initializeKafkaProducer() {
        if (this.kafkaProducer == null) {
            return;
        }

        final var properties = new Properties();
        properties.putAll(Objects.nonNull(devToolsProperties.kafkaProducer())
                ? devToolsProperties.kafkaProducer().properties()
                : Collections.emptyMap());
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                devToolsProperties.kafkaProducer().bootstrapServers());
        properties.putAll(outputProperties);

        logger.infov("kafka loaded producer properties: {0}", properties);

        this.kafkaProducer = new KafkaProducer<>(properties);
    }

    @Override
    public Exporter setOutputProperties(final Map<String, String> properties) {
        this.outputProperties.putAll(properties);
        return this;
    }

    @Override
    public Exporter setOutputFilePath(final String outPutPath) {
        logger.debug("output parameter not applicable to this type of export");
        return this;
    }

    @Override
    public Exporter setOutputTopicName(final String outputTopic) {
        this.outputTopicName = outputTopic;
        return this;
    }

    @Override
    public Exporter setTransform(Transform transform) {
        this.transform = transform;
        return this;
    }

    @Override
    public void close() throws IOException {
        if (kafkaProducer == null) {
            return;
        }

        kafkaProducer.close();
    }
}
