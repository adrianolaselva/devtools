package com.devtools.infrastructure.export;


import com.devtools.infrastructure.configs.ExportContextBean;
import com.devtools.domain.exporter.enums.OutputType;
import com.devtools.infrastructure.configs.TransformContextBean;
import com.devtools.domain.transform.enums.TransformType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.devtools.infrastructure.commons.FileCommon.*;
import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class ExportUnitTest {

    private final static String TEMP_EXPORTER_FILEPATH = ".out/exporter_tests.out";

    private ConsumerRecords<String, String> consumerRecordsMock;

    @Inject
    private ExportContextBean exportContextBean;

    @BeforeEach
    public void setUp() {
        removeFilesIfExists(TEMP_EXPORTER_FILEPATH);
        consumerRecordsMock = mock(ConsumerRecords.class);
    }

    @AfterEach
    public void setTearDown() {
        removeFilesIfExists(TEMP_EXPORTER_FILEPATH);
    }

    @ParameterizedTest
    @MethodSource("shouldExecuteExportWithSuccessProvider")
    void shouldExecuteExportWithSuccess(final List<ConsumerRecord<String, String>> records,
                                        final OutputType outPutType,
                                        final String outputFilePath,
                                        final String outPutContentExpected) throws IOException {
        when(consumerRecordsMock.iterator()).thenReturn(records.iterator());

        try(final var exporterContextInstance = exportContextBean.getInstance(outPutType)) {
            exporterContextInstance
                    .setOutputFilePath(outputFilePath)
                    .export(consumerRecordsMock);
        }

        assertEquals(records.size(), loadLinesByFilePath(outputFilePath).size());
        assertTrue(outPutContentExpected.contains(loadContentByFilePath(outputFilePath, "\n")));
    }

    @ParameterizedTest
    @MethodSource("shouldExecuteTransformAndExportWithSuccessProvider")
    void shouldExecuteTransformAndExportWithSuccess(final List<ConsumerRecord<String, String>> records,
                                                    final OutputType outPutType,
                                                    final String outputFilePath,
                                                    final TransformType transformType,
                                                    final String transformScriptPath,
                                                    final String methodName,
                                                    final String outPutContentExpected)
            throws IOException, ScriptException {
        when(consumerRecordsMock.iterator()).thenReturn(records.iterator());

        try(final var exporterContextInstance = exportContextBean.getInstance(outPutType)) {
            exporterContextInstance
                    .setOutputFilePath(outputFilePath)
                    .setTransform(
                            TransformContextBean.getInstance(transformType)
                                    .setFilePath(transformScriptPath)
                                    .setMethodName(methodName))
                    .export(consumerRecordsMock);
        }

        assertEquals(records.size(), loadLinesByFilePath(outputFilePath).size());
        assertEquals(outPutContentExpected, loadContentByFilePath(outputFilePath, "\n"));
    }

    private static Stream<Arguments> shouldExecuteExportWithSuccessProvider() throws IOException {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}")),
                        OutputType.JSON_LINE, TEMP_EXPORTER_FILEPATH,
                        loadContentByFilePath("src/test/resources/output/test_scenario_output_01.out", "\n")),
                Arguments.of(
                        Arrays.asList(
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]")),
                        OutputType.JSON_LINE, TEMP_EXPORTER_FILEPATH,
                        loadContentByFilePath("src/test/resources/output/test_scenario_output_02.out", "\n")),
                Arguments.of(
                        Arrays.asList(
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}")),
                        OutputType.JSON_LINE, TEMP_EXPORTER_FILEPATH,
                        loadContentByFilePath("src/test/resources/output/test_scenario_output_03.out", "\n")));
    }

    private static Stream<Arguments> shouldExecuteTransformAndExportWithSuccessProvider() throws IOException {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}")),
                        OutputType.JSON_LINE,
                        TEMP_EXPORTER_FILEPATH,
                        TransformType.GROOVY,
                        "src/test/resources/scripts/groovy/transform_and_export_example_01.groovy",
                        "apply",
                        loadContentByFilePath("src/test/resources/output/test_scenario_output_04.out", "\n")),
                Arguments.of(
                        Arrays.asList(
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "[{\"first_name\":\"first name\"},{\"first_name\":\"first name\"}]")),
                        OutputType.JSON_LINE,
                        TEMP_EXPORTER_FILEPATH,
                        TransformType.GROOVY,
                        "src/test/resources/scripts/groovy/transform_and_export_example_02.groovy",
                        "apply",
                        loadContentByFilePath("src/test/resources/output/test_scenario_output_05.out", "\n")),
                Arguments.of(
                        Arrays.asList(
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}"),
                                new ConsumerRecord<>("topic-streams", 1,
                                        now().toEpochMilli(), UUID.randomUUID().toString(),
                                        "{\"first_name\":\"first name\",\"last_name\":\"last name\",\"rules\":[\"ADMIN\",\"ATTENDANT\",\"USER\"]}")
                        ),
                        OutputType.JSON_LINE,
                        TEMP_EXPORTER_FILEPATH,
                        TransformType.GROOVY,
                        "src/test/resources/scripts/groovy/transform_and_export_example_03.groovy",
                        "apply",
                        loadContentByFilePath("src/test/resources/output/test_scenario_output_06.out", "\n")));
    }

}
