package br.com.adrianolaselva.devtools.infrastructure.importer.csv;

import br.com.adrianolaselva.devtools.domain.importer.enums.FileCharset;
import br.com.adrianolaselva.devtools.domain.importer.exceptions.FileReaderException;
import br.com.adrianolaselva.devtools.domain.importer.interfaces.FileContentReader;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import javax.script.ScriptException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

@ApplicationScoped
public class CsvFileContentReader implements FileContentReader {

    private static final Logger logger = Logger.getLogger(CsvFileContentReader.class);

    private Path filePath;
    private FileCharset fileCharset;
    private Transform transform;

    public void process() {
        try (final var fileReader = new FileReader(filePath.toString(), FileCharset.getCharsetType(fileCharset))) {
            final var csvParser = new CSVParserBuilder()
                    .withSeparator(';')
                    .withIgnoreQuotations(true)
                    .build();

            try (final var reader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(csvParser)
                    .build()) {
                reader.iterator().forEachRemaining(this::readLine);
            }
        } catch (IOException e) {
            throw new FileReaderException(e);
        }
    }

    private void readLine(final String[] values) {
        try {
            final var payloadColumns = Arrays.asList(values);
            final var columns = transform != null
                    ? transform.apply(payloadColumns)
                    : payloadColumns;

            logger.infov("records read: {0}", columns);
        } catch (ScriptException | NoSuchMethodException e) {
            logger.error("failed to apply transform", e);
        }
    }

    public FileContentReader setFilePath(Path filePath) {
        this.filePath = filePath;
        return this;
    }

    public FileContentReader setFileCharset(FileCharset fileCharset) {
        this.fileCharset = fileCharset;
        return this;
    }

    public FileContentReader setTransform(Transform transform) {
        this.transform = transform;
        return this;
    }
}
