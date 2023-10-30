package br.com.adrianolaselva.devtools.infrastructure.importer;

import br.com.adrianolaselva.devtools.domain.importer.exceptions.ScanFilePathException;
import br.com.adrianolaselva.devtools.domain.importer.interfaces.ScanFilePath;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.time.StopWatch;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@ApplicationScoped
public class NIOScanFilePath implements ScanFilePath {

    private static final Logger logger = Logger.getLogger(NIOScanFilePath.class);

    private Path filePath;
    private FileVisitor<Path> fileVisitor;

    public void process() {
        final var elapsed = new StopWatch();
        elapsed.start();

        try {
            final var file = new File(this.filePath.toString());
            if (!file.exists()) {
                throw new ScanFilePathException(format("file or directory %s not found", filePath));
            }

            logger.infov("import from {0} {1} starting...", file.isFile() ? "file" : "directory", file);

            Files.walkFileTree(filePath, fileVisitor);
        } catch (IOException e) {
            throw new ScanFilePathException(e);
        } finally {
            logger.debugv("total execution time, elapsed {0}ms", elapsed.getTime(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public ScanFilePath setFilePath(Path filePath) {
        this.filePath = filePath;
        return this;
    }

    @Override
    public ScanFilePath setFileVisitor(FileVisitor<Path> fileVisitor) {
        this.fileVisitor = fileVisitor;
        return this;
    }
}
