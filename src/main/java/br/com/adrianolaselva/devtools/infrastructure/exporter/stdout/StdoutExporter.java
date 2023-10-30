package br.com.adrianolaselva.devtools.infrastructure.exporter.stdout;

import br.com.adrianolaselva.devtools.domain.exporter.interfaces.Exporter;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jboss.logging.Logger;

import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

@ApplicationScoped
public class StdoutExporter implements Exporter {

    private static final Logger logger = Logger.getLogger(StdoutExporter.class);

    private Long totalBytesExported = 0L;
    private Transform transform;

    @Override
    public void export(final ConsumerRecords<String, String> records) {
        final var elapsed = new StopWatch();
        try {
            elapsed.start();
            records.iterator().forEachRemaining(this::processRecord);
        } finally {
            logger.debugv("export execution, elapsed {0}ms", elapsed.getTime(TimeUnit.MILLISECONDS));
        }
    }

    private void processRecord(final ConsumerRecord<String, String> consumerRecord) {
        final String payload;
        try {
            payload = transform != null
                ? transform.apply(consumerRecord.value()) : consumerRecord.value();
            this.totalBytesExported += payload.getBytes().length;

            logger.infov("partition: {0}, offset: {1}, total exported: {2}, key: {3}, value: {4}",
                    consumerRecord.partition(),
                    consumerRecord.offset(),
                    byteCountToDisplaySize(totalBytesExported),
                    consumerRecord.key(), payload);
        } catch (ScriptException | NoSuchMethodException e) {
            logger.errorv("failed to apply transform: [payload: {0}, message: {1}]",
                consumerRecord.value(), e.getMessage());
        }
    }

    @Override
    public Exporter setOutputFilePath(final String outPutPath) {
        logger.debug("outPutPath parameter not applicable to this type of export");
        return this;
    }

    @Override
    public Exporter setOutputTopicName(final String outputTopicName) {
        logger.debug("outputTopicName parameter not applicable to this type of export");
        return this;
    }

    @Override
    public Exporter setOutputProperties(final Map<String, String> properties) {
        logger.debug("outputProperties parameter not applicable to this type of export");
        return this;
    }

    @Override
    public Exporter setTransform(Transform transform) {
        this.transform = transform;
        return this;
    }
}
