package integration.com.adrianolaselva.devtools.application.commands.kafka;

import br.com.adrianolaselva.devtools.application.commands.kafka.ConsumerCommand;
import br.com.adrianolaselva.devtools.domain.exporter.enums.OutputType;
import fixture.handlers.LoggerCaptureTestHandler;
import fixture.profiles.IntegrationTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.adrianolaselva.devtools.infrastructure.commons.FileCommon.loadContentByFilePath;
import static br.com.adrianolaselva.devtools.infrastructure.commons.FileCommon.removeAllFilesByPath;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.eclipse.microprofile.config.ConfigProvider.getConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestProfile(IntegrationTestProfile.class)
class ConsumerCommandIntegrationTest {

    @Inject
    private KafkaProducer<String, String> kafkaProducer;

    @Inject
    private LoggerCaptureTestHandler loggerCaptureTestHandler;

    @Inject
    private ConsumerCommand consumerCommand;

    @BeforeAll
    public static void setUp() throws IOException {
        removeAllFilesByPath(".out/");
    }

    @AfterAll
    public static void tearDown() throws IOException {
        removeAllFilesByPath(".out/");
    }

    @ParameterizedTest
    @MethodSource("provideParametersForStdout")
    void shouldExecuteTheCommandAndPrintTheMessageProduced(final String bootstrapServers,
        final String topicName,
        final String key,
        final String value) {
        final var parameters = List.of(
            format("--bootstrap-servers=%s", bootstrapServers),
            format("--topic=%s", topicName),
            "--pool-interval=10000");

        kafkaProducer.send(new ProducerRecord<>(topicName, key, value));

        new CommandLine(consumerCommand).execute(parameters.toArray(new String[0]));

        final var loggerParameters = loggerCaptureTestHandler.getLogRecords().stream()
            .filter(logRecord -> nonNull(logRecord.getParameters()) && logRecord.getLevel().equals(Level.INFO))
            .flatMap(logRecord -> Arrays.stream(logRecord.getParameters()))
            .map(Object::toString)
            .collect(Collectors.toSet());
        final var loggerLevels = loggerCaptureTestHandler.getLogRecords().stream()
            .map(LogRecord::getLevel)
            .map(Level::toString)
            .collect(Collectors.toSet());

        assertTrue(loggerParameters.contains(key));
        assertTrue(loggerParameters.contains(value));
        assertFalse(loggerLevels.contains("ERROR"));
    }

    @ParameterizedTest
    @MethodSource("provideParametersForJsonL")
    void shouldExecuteTheCommandToExportTheMessageProducedInJsonLine(final String bootstrapServers,
        final String topicName,
        final String key,
        final List<String> values,
        final String outputPath) throws IOException {
        final var parameters = List.of(
            format("--bootstrap-servers=%s", bootstrapServers),
            format("--topic=%s", topicName),
            format("--output-type=%s", OutputType.JSON_LINE),
            format("--output-path=%s", outputPath),
            "--pool-interval=10000");
        final var outputContentExpected = String.join("\n", values);

        values.forEach(value -> kafkaProducer.send(new ProducerRecord<>(topicName, key, value)));

        new CommandLine(consumerCommand).execute(parameters.toArray(new String[0]));

        assertEquals(outputContentExpected, loadContentByFilePath(outputPath, "\n"));
    }

    private static Stream<Arguments> provideParametersForStdout() {
        return Stream.of(
            Arguments.of(
                getConfig().getValue("kafka.bootstrap.servers", String.class),
                "devtools-stream-stdout-tests-01", "da386bf4-0360-45a1-bbab-452d95153da5",
                "{\"_id\":\"65270bc4807b4756255e876b\",\"index\":0,\"guid\":\"da386bf4-0360-45a1-bbab-452d95153da5\",\"isActive\":false}"),
            Arguments.of(
                getConfig().getValue("kafka.bootstrap.servers", String.class),
                "devtools-stream-stdout-tests-02", "da386bf4-0360-45a1-bbab-452d95153da5",
                "{\"_id\":\"65270bc4807b4756255e876b\",\"firstName\":null,\"isActive\":false}"),
            Arguments.of(
                getConfig().getValue("kafka.bootstrap.servers", String.class),
                "devtools-stream-stdout-tests-03", "da386bf4-0360-45a1-bbab-452d95153da5",
                "{\"_id\":\"65270bc4807b4756255e876b\",\"first_name\":\"\",\"is_active\":false}"));
    }

    private static Stream<Arguments> provideParametersForJsonL() {
        return Stream.of(
            Arguments.of(
                getConfig().getValue("kafka.bootstrap.servers", String.class),
                "devtools-stream-jsonl-tests-01", UUID.randomUUID().toString(),
                List.of(
                    "{\"_id\":\"65270bc4807b4756255e8761\",\"index\":0,\"guid\":\"da386bf4-0360-45a1-bbab-452d95153da5\",\"isActive\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8762\",\"index\":0,\"guid\":\"da386bf4-0360-45a1-bbab-452d95153da5\",\"isActive\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8763\",\"index\":0,\"guid\":\"da386bf4-0360-45a1-bbab-452d95153da5\",\"isActive\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8764\",\"index\":0,\"guid\":\"da386bf4-0360-45a1-bbab-452d95153da5\",\"isActive\":false}"),
                format(".out/temp_%s.jsonl", UUID.randomUUID())),
            Arguments.of(
                getConfig().getValue("kafka.bootstrap.servers", String.class),
                "devtools-stream-jsonl-tests-02", UUID.randomUUID().toString(),
                List.of(
                    "{\"_id\":\"65270bc4807b4756255e8761\",\"firstName\":null,\"isActive\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8762\",\"firstName\":null,\"isActive\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8763\",\"firstName\":null,\"isActive\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8764\",\"firstName\":null,\"isActive\":false}"),
                format(".out/temp_%s.jsonl", UUID.randomUUID())),
            Arguments.of(
                getConfig().getValue("kafka.bootstrap.servers", String.class),
                "devtools-stream-jsonl-tests-03", UUID.randomUUID().toString(),
                List.of(
                    "{\"_id\":\"65270bc4807b4756255e8761\",\"first_name\":\"\",\"is_active\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8762\",\"first_name\":\"\",\"is_active\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8763\",\"first_name\":\"\",\"is_active\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8764\",\"first_name\":\"\",\"is_active\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8765\",\"first_name\":\"\",\"is_active\":false}",
                    "{\"_id\":\"65270bc4807b4756255e8766\",\"first_name\":\"\",\"is_active\":false}"),
                format(".out/temp_%s.jsonl", UUID.randomUUID())));
    }

}
