package com.devtools.domain.importer.enums;

import com.devtools.domain.importer.exceptions.InputTypeNotDefined;

import static java.lang.String.*;

public enum InputType {
    CSV,
    JSON_LINE,
    JSON,
    XML;

    public static InputType getInPutType(String inputType) {
        return switch (inputType.toLowerCase()) {
            case "csv" -> CSV;
            case "jsonl" -> JSON_LINE;
            case "json" -> JSON;
            case "xml" -> XML;
            default -> throw new InputTypeNotDefined(format("input format type %s not defined", inputType));
        };
    }
}
