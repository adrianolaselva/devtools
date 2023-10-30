package br.com.adrianolaselva.devtools.infrastructure.configs;


import br.com.adrianolaselva.devtools.domain.importer.enums.InputType;
import br.com.adrianolaselva.devtools.domain.importer.interfaces.FileContentReader;
import br.com.adrianolaselva.devtools.infrastructure.importer.csv.CsvFileContentReader;
import br.com.adrianolaselva.devtools.infrastructure.importer.csv.JsonFileContentReader;
import br.com.adrianolaselva.devtools.infrastructure.importer.csv.JsonLineFileContentReader;
import br.com.adrianolaselva.devtools.infrastructure.importer.csv.XmlFileContentReader;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReaderFileContextBean {

    private final CsvFileContentReader csvImporter;
    private final JsonLineFileContentReader jsonLineImporter;
    private final JsonFileContentReader jsonImporter;
    private final XmlFileContentReader xmlImporter;

    public ReaderFileContextBean(final CsvFileContentReader csvImporter,
                                 final JsonLineFileContentReader jsonLineImporter,
                                 final JsonFileContentReader jsonImporter,
                                 final XmlFileContentReader xmlImporter) {
        this.csvImporter = csvImporter;
        this.jsonLineImporter = jsonLineImporter;
        this.jsonImporter = jsonImporter;
        this.xmlImporter = xmlImporter;
    }

    public FileContentReader getInstance(final InputType inPutType) {
        return switch (inPutType) {
            case CSV -> csvImporter;
            case JSON_LINE -> jsonLineImporter;
            case JSON -> jsonImporter;
            case XML -> xmlImporter;
        };
    }
}
