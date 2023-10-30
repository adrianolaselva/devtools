package fixture.configs;

import fixture.handlers.LoggerCaptureTestHandler;
import jakarta.enterprise.inject.Produces;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger;

import javax.inject.Singleton;

@Singleton
public class LoggerCaptureTestBean {

    @Produces
    public LoggerCaptureTestHandler loggerCaptureHandler() {
        final var loggerHandler = new LoggerCaptureTestHandler();
        LogContext.getLogContext()
            .getLoggerNames().asIterator()
            .forEachRemaining(className -> Logger.getLogger(className).addHandler(loggerHandler));

        return loggerHandler;
    }

}
