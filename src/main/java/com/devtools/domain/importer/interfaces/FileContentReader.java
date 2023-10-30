package com.devtools.domain.importer.interfaces;

import com.devtools.domain.importer.enums.FileCharset;
import com.devtools.domain.transform.interfaces.Transform;

import java.nio.file.Path;

public interface FileContentReader {

    void process();

    FileContentReader setFilePath(Path filePath);

    FileContentReader setFileCharset(FileCharset fileCharset);

    FileContentReader setTransform(Transform transform);

}
