package br.com.adrianolaselva.devtools.infrastructure.exporter.file;

import br.com.adrianolaselva.devtools.domain.exporter.interfaces.WriteFileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class IOWriteFileHandler implements WriteFileHandler<String> {

    protected final ReentrantLock lock = new ReentrantLock();
    private final FileWriter fileWriter;
    private final BufferedWriter bufferedWriter;

    public IOWriteFileHandler(final String fileName) throws IOException {
        this.fileWriter = new FileWriter(loadFileReferenceForExport(fileName), true);
        this.bufferedWriter = new BufferedWriter(this.fileWriter);
    }

    private File loadFileReferenceForExport(final String fileName) throws IOException {
        lock.lock();
        try {
            final var file = new File(fileName);
            final var fileDirectory = new File(file.getParent());
            assert fileDirectory.exists() || fileDirectory.mkdirs() :
                    String.format("failed to create %s directory for export", fileDirectory.getName());

            file.deleteOnExit();
            if (!file.createNewFile()) {
                throw new IOException(String.format("Failed to create %s file for export", fileName));
            }

            return file;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void writeLine(final String lineContent) throws IOException {
        if (lineContent == null) {
            return;
        }

        lock.lock();
        try {
            fileWriter.write(lineContent);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void flush() throws IOException {
        lock.lock();
        try {
            fileWriter.flush();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        lock.lock();
        try {
            fileWriter.close();
            bufferedWriter.close();
        } finally {
            lock.unlock();
        }
    }
}
