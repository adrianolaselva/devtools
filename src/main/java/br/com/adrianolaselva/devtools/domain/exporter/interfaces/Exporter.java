package br.com.adrianolaselva.devtools.domain.exporter.interfaces;


import br.com.adrianolaselva.devtools.domain.exporter.exceptions.ExportOutputFilePathException;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

public interface Exporter extends Closeable {

    Exporter setOutputFilePath(final String outPutPath) throws ExportOutputFilePathException;

    Exporter setOutputTopicName(final String outputTopic);

    Exporter setOutputProperties(final Map<String, String> properties);

    Exporter setTransform(final Transform transform);

    void export(final ConsumerRecords<String, String> records) throws IOException;

    default void close() throws IOException {

    }
}
