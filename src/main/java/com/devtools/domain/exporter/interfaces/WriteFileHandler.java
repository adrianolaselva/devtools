package com.devtools.domain.exporter.interfaces;

import java.io.Flushable;
import java.io.IOException;

public interface WriteFileHandler<T> extends AutoCloseable, Flushable {

    void writeLine(final T lineContent) throws IOException;
}
