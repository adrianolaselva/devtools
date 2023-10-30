package br.com.adrianolaselva.devtools.infrastructure.configs;


import br.com.adrianolaselva.devtools.domain.exporter.enums.OutputType;
import br.com.adrianolaselva.devtools.domain.exporter.interfaces.Exporter;
import br.com.adrianolaselva.devtools.infrastructure.exporter.file.FileStreamExporter;
import br.com.adrianolaselva.devtools.infrastructure.exporter.kafka.KafkaExporter;
import br.com.adrianolaselva.devtools.infrastructure.exporter.stdout.StdoutExporter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExportContextBean {

    private final StdoutExporter stdoutExporter;
    private final FileStreamExporter fileStreamExporter;
    private final KafkaExporter kafkaExporter;

    public ExportContextBean(final StdoutExporter stdoutExporter,
                             final FileStreamExporter fileStreamExporter,
                             final KafkaExporter kafkaExporter) {
        this.stdoutExporter = stdoutExporter;
        this.fileStreamExporter = fileStreamExporter;
        this.kafkaExporter = kafkaExporter;
    }

    public Exporter getInstance(final OutputType outPutType) {
        return switch (outPutType) {
            case STDOUT -> stdoutExporter;
            case JSON_LINE -> fileStreamExporter;
            case KAFKA -> kafkaExporter;
        };
    }
}
