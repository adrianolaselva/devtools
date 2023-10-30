package unit.com.adrianolaselva.devtools.infrastructure.export;

import br.com.adrianolaselva.devtools.infrastructure.exporter.file.IOWriteFileHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IOWriteFileHandlerTest {

    @ParameterizedTest
    @MethodSource("shouldWriteFileLinesWithSuccessProvider")
    void shouldWriteFileLinesWithSuccess(final String filePath, final int lines) throws FileNotFoundException {
        try (final var writeFileHandler = new IOWriteFileHandler(filePath)) {
            IntStream.range(0, lines).forEach(value -> {
                try {
                    writeFileHandler.writeLine(format("line_%s\n", value));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final var reader = new BufferedReader(new FileReader(filePath));
        assertEquals(buildFileContentToAssert(lines), String.join("\n", reader.lines().toList()));
    }

    private static Stream<Arguments> shouldWriteFileLinesWithSuccessProvider() {
        return Stream.of(
            Arguments.of(".out/file_name_01.txt", 10000),
            Arguments.of(".out/file_name_02.txt", 999),
            Arguments.of(".out/file_name_03.txt", 34));
    }

    private String buildFileContentToAssert(final int lines) {
        var expectedContent = new StringBuilder();
        IntStream.range(0, lines)
            .forEach(value -> expectedContent.append(format("line_%s%s", value, value == lines - 1 ? "" : "\n")));

        return expectedContent.toString();
    }

}
