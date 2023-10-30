package br.com.adrianolaselva.devtools.infrastructure.importer.csv;

import br.com.adrianolaselva.devtools.domain.importer.enums.FileCharset;
import br.com.adrianolaselva.devtools.domain.importer.interfaces.FileContentReader;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.nio.file.Path;

@ApplicationScoped
public class JsonLineFileContentReader implements FileContentReader {

    private static final Logger logger = Logger.getLogger(JsonLineFileContentReader.class);

    public void process() {
        logger.warn("JSON_LINE file reading not implemented");
    }

    @Override
    public FileContentReader setFilePath(Path filePath) {
        return this;
    }

    @Override
    public FileContentReader setFileCharset(FileCharset fileCharset) {
        return this;
    }

    @Override
    public FileContentReader setTransform(Transform transform) {
        return this;
    }
}
