package br.com.adrianolaselva.devtools.infrastructure.importer;

import br.com.adrianolaselva.devtools.domain.importer.enums.FileCharset;
import br.com.adrianolaselva.devtools.domain.importer.enums.InputType;
import br.com.adrianolaselva.devtools.domain.importer.exceptions.InputTypeNotDefined;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import br.com.adrianolaselva.devtools.infrastructure.configs.ReaderFileContextBean;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CustomFileVisitor implements FileVisitor<Path> {

    private static final Logger logger = Logger.getLogger(CustomFileVisitor.class);

    private FileCharset fileCharset;
    private Transform transform;
    private String moveToPathPathParameter;

    private final ReaderFileContextBean readerFileContext;

    public CustomFileVisitor(final ReaderFileContextBean readerFileContext) {
        this.readerFileContext = readerFileContext;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        logger.debugv("directory {0} scanning started", dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        final var elapsed = new StopWatch();
        elapsed.start();

        try {
            final InputType inputType = InputType.getInPutType(FilenameUtils.getExtension(file.toString()));
            try {
                final var readerFile = readerFileContext.getInstance(inputType);
                logger.infov("import of file {0} started", file);
                readerFile
                        .setFileCharset(fileCharset)
                        .setFilePath(file)
                        .setTransform(this.transform)
                        .process();

                if (moveToPathPathParameter != null) {
                    Files.move(file, Path.of(this.moveToPathPathParameter));
                    logger.infov("file moved to directory {}", this.moveToPathPathParameter);
                }
            } finally {
                logger.infov("import of file {0} completed, elapsed {1}ms",
                        file, elapsed.getTime(TimeUnit.MILLISECONDS));
            }
        } catch (InputTypeNotDefined e) {
            logger.warnv("{0} file format not implemented and will be ignored", file);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        logger.debugv("failed to import file {0}", file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        logger.debugv("directory {0} scanning completed", dir);
        return FileVisitResult.CONTINUE;
    }

    public CustomFileVisitor setMoveToPathPathParameter(String moveToPathPathParameter) {
        this.moveToPathPathParameter = moveToPathPathParameter;
        return this;
    }

    public CustomFileVisitor setFileCharset(FileCharset fileCharset) {
        this.fileCharset = fileCharset;
        return this;
    }

    public CustomFileVisitor setTransform(Transform transform) {
        this.transform = transform;
        return this;
    }
}
