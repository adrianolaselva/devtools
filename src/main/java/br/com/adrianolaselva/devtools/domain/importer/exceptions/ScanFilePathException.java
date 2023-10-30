package br.com.adrianolaselva.devtools.domain.importer.exceptions;

public class ScanFilePathException extends RuntimeException {

    public ScanFilePathException(Throwable cause) {
        super(cause);
    }

    public ScanFilePathException(final String message) {
        super(message);
    }
}
