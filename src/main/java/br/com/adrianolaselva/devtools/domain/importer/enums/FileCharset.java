package br.com.adrianolaselva.devtools.domain.importer.enums;

import br.com.adrianolaselva.devtools.domain.importer.exceptions.InputTypeNotDefined;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

public enum FileCharset {
    ISO_8859_1,
    US_ASCII,
    UTF_8,
    UTF_16,
    UTF_16BE,
    UTF_16LE;

    public static FileCharset getCharset(String charsetType) {
        return switch (charsetType) {
            case "ISO_8859_1" -> FileCharset.ISO_8859_1;
            case "US_ASCII" -> FileCharset.US_ASCII;
            case "UTF_8" -> FileCharset.UTF_8;
            case "UTF_16" -> FileCharset.UTF_16;
            case "UTF_16BE" -> FileCharset.UTF_16BE;
            case "UTF_16LE" -> FileCharset.UTF_16LE;
            default -> throw new InputTypeNotDefined(format("charset type %s not defined", charsetType));
        };
    }

    public static Charset getCharsetType(FileCharset fileCharset) {
        if (fileCharset == null) {
            return StandardCharsets.UTF_8;
        }

        return switch (fileCharset) {
            case ISO_8859_1 -> StandardCharsets.ISO_8859_1;
            case US_ASCII -> StandardCharsets.US_ASCII;
            case UTF_8 -> StandardCharsets.UTF_8;
            case UTF_16 -> StandardCharsets.UTF_16;
            case UTF_16BE -> StandardCharsets.UTF_16BE;
            case UTF_16LE -> StandardCharsets.UTF_16LE;
        };
    }
}
