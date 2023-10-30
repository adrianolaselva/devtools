package com.devtools.infrastructure.commons;

import com.devtools.infrastructure.commons.exceptions.FileHandlerException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileCommon {

    private FileCommon() {
        throw new IllegalStateException("this class cannot be instantiated");
    }

    public static String loadContentByFilePath(final String filePath, final String delimiter) throws IOException {
        try (final var reader = new BufferedReader(new FileReader(filePath))) {
            return String.join(delimiter, reader.lines().toList());
        }
    }

    public static List<String> loadLinesByFilePath(final String filePath) throws IOException {
        final List<String> lines = new ArrayList<>();
        try (final var streamLines = Files.lines(Path.of(filePath), StandardCharsets.UTF_8)) {
            streamLines.forEach(lines::add);
        }

        return lines;
    }

    public static void removeFilesIfExists(final String... filePaths) {
        Arrays.stream(filePaths).iterator().forEachRemaining(filePath -> {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                throw new FileHandlerException(e);
            }
        });
    }

    public static void removeAllFilesByPath(final String filePath) throws IOException {
        Files.walkFileTree(Path.of(filePath), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
