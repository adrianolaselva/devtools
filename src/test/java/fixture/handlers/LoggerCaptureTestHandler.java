package fixture.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggerCaptureTestHandler extends Handler {

    private final Set<LogRecord> logRecords = new HashSet<>();

    @Override
    public void publish(LogRecord record) {
        logRecords.add(record);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    public Set<LogRecord> getLogRecords() {
        return logRecords;
    }

}
