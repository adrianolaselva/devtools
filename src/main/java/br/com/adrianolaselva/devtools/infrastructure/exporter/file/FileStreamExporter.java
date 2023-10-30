package br.com.adrianolaselva.devtools.infrastructure.exporter.file;

import br.com.adrianolaselva.devtools.domain.exporter.exceptions.ExportOutputFilePathException;
import br.com.adrianolaselva.devtools.domain.exporter.exceptions.ExportLineIOException;
import br.com.adrianolaselva.devtools.domain.exporter.interfaces.Exporter;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jboss.logging.Logger;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

@ApplicationScoped
public class FileStreamExporter implements Exporter {

    private static final Logger logger = Logger.getLogger(FileStreamExporter.class);

    private String outPutPath;
    private Long totalExported = 0L;
    private Long totalBytesExported = 0L;
    private Transform transform;
    private IOWriteFileHandler ioWriteFileHandler;

    @Override
    public void export(final ConsumerRecords<String, String> records) throws IOException {
        final var elapsed = new StopWatch();
        try {
            elapsed.start();
            final var batchSize = records.count();
            records.iterator().forEachRemaining(this::processRecord);

            totalExported += batchSize;
            logger.infov("batch exported from {0}, totaling {1} records for file {2}, total exported: {3}",
                batchSize, totalExported, outPutPath, byteCountToDisplaySize(totalBytesExported));
        } finally {
            this.ioWriteFileHandler.flush();
            logger.debugv("export execution, elapsed {0}ms", elapsed.getTime(TimeUnit.MILLISECONDS));
        }
    }

    private void processRecord(final ConsumerRecord<String, String> consumerRecord) {
        final String payload;
        try {
            payload = transform != null
                ? transform.apply(consumerRecord.value()) : consumerRecord.value();
            totalBytesExported += payload.getBytes().length;

            ioWriteFileHandler.writeLine(payload + "\n");
        } catch (ScriptException | NoSuchMethodException e) {
            logger.errorv("failed to apply transform: [payload: {0}, message: {1}]",
                consumerRecord.value(), e.getMessage());
        } catch (IOException e) {
            throw new ExportLineIOException(e);
        }
    }

    @Override
    public Exporter setOutputFilePath(final String outPutPath) throws ExportOutputFilePathException {
        try {
            this.ioWriteFileHandler = new IOWriteFileHandler(outPutPath);
            this.outPutPath = outPutPath;
        } catch (IOException e) {
            throw new ExportOutputFilePathException(e);
        }

        return this;
    }

    @Override
    public Exporter setOutputTopicName(final String outputTopic) {
        logger.debug("outputTopicName parameter not applicable to this type of export");
        return this;
    }

    @Override
    public Exporter setOutputProperties(final Map<String, String> properties) {
        logger.debug("outPutProperties parameter not applicable to this type of export");
        return this;
    }

    @Override
    public Exporter setTransform(Transform transform) {
        this.transform = transform;
        return this;
    }

    @Override
    public void close() throws IOException {
        try {
            ioWriteFileHandler.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
