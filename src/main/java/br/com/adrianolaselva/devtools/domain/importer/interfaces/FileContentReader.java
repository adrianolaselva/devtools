package br.com.adrianolaselva.devtools.domain.importer.interfaces;

import br.com.adrianolaselva.devtools.domain.importer.enums.FileCharset;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;

import java.nio.file.Path;

public interface FileContentReader {

    void process();

    FileContentReader setFilePath(Path filePath);

    FileContentReader setFileCharset(FileCharset fileCharset);

    FileContentReader setTransform(Transform transform);

}
