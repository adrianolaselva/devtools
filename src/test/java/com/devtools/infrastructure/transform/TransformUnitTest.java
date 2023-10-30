package com.devtools.infrastructure.transform;

import com.devtools.domain.transform.enums.TransformType;
import com.devtools.infrastructure.configs.TransformContextBean;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransformUnitTest {

    @ParameterizedTest
    @MethodSource("providePayloadJsonLineParameters")
    void shouldApplyTransformWithJsonLinePayload(final String filePath, final TransformType transformType, final String payload,
        final String expectedPayload)
        throws IOException, ScriptException, NoSuchMethodException {
        final var transformedPayload = TransformContextBean.getInstance(transformType)
            .setFilePath(filePath)
            .apply(payload);

        assertEquals(expectedPayload, transformedPayload);
    }

    @ParameterizedTest
    @MethodSource("providePayloadCsvColumnsParameters")
    void shouldApplyTransformWithCsvLinePayload(final String filePath, final TransformType transformType, final List<String> payload,
                                         final List<String> expectedPayload)
            throws IOException, ScriptException, NoSuchMethodException {
        final var transformedPayload = TransformContextBean.getInstance(transformType)
                .setFilePath(filePath)
                .apply(payload);

        assertArrayEquals(expectedPayload.toArray(), transformedPayload.toArray());
    }

    private static Stream<Arguments> providePayloadJsonLineParameters() {
        return Stream.of(
                Arguments.of(
                        "src/test/resources/scripts/groovy/transform_example_01.groovy",
                        TransformType.GROOVY,
                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\"}",
                        "{\"document_number\":\"999.999.999-99\",\"first_name\":\"first name\",\"last_name\":\"last name\",\"timestamp\":1696435137753}"),
                Arguments.of(
                        "src/test/resources/scripts/groovy/transform_example_02.groovy",
                        TransformType.GROOVY,
                        "{\"document_number\":\"999.999.999-99\"}",
                        "{\"document_number\":\"999.999.999-99\",\"timestamp\":1696435137753}"));
    }

    private static Stream<Arguments> providePayloadCsvColumnsParameters() {
        return Stream.of(
                Arguments.of(
                        "src/test/resources/scripts/groovy/transform_and_export_csv_example_01.groovy",
                        TransformType.GROOVY,
                        List.of( "1857020", "Pessoas com deficiência no Brasil enfrentam desafios para alcançar autonomia. Barreiras físicas", "Beneficíários diretos – 240 pessoas preferencialmente com deficiência de todas as idades e  Beneficiários indiretos – aproximadamente 720 pessoas não deficientes" ),
                        List.of( "1857020", "Pessoas com deficiência no Brasil enfrentam desafios para alcançar autonomia. Barreiras físicas", "Beneficíários diretos – 240 pessoas preferencialmente com deficiência de todas as idades e  Beneficiários indiretos – aproximadamente 720 pessoas não deficientes" )),
                Arguments.of(
                        "src/test/resources/scripts/groovy/transform_and_export_csv_example_02.groovy",
                        TransformType.GROOVY,
                        List.of("1857020", "Pessoas com deficiência no Brasil enfrentam desafios para alcançar autonomia. Barreiras físicas"),
                        List.of("1857020", "Pessoas com deficiência no Brasil enfrentam desafios para alcançar autonomia. Barreiras físicas", "1696435137753")));
    }
}
