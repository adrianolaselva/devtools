package br.com.adrianolaselva.devtools.domain.importer.interfaces;

import java.nio.file.FileVisitor;
import java.nio.file.Path;

public interface ScanFilePath {

    void process();

    ScanFilePath setFilePath(Path filePath);

    ScanFilePath setFileVisitor(FileVisitor<Path> fileVisitor);
}
