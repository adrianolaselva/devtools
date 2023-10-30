package com.devtools.infrastructure.importer.csv;

import com.devtools.domain.importer.enums.FileCharset;
import com.devtools.domain.importer.interfaces.FileContentReader;
import com.devtools.domain.transform.interfaces.Transform;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.nio.file.Path;

@ApplicationScoped
public class JsonFileContentReader implements FileContentReader {

    private static final Logger logger = Logger.getLogger(JsonFileContentReader.class);

    public void process() {
        logger.warn("JSON file reading not implemented");
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
