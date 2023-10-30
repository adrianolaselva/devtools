package br.com.adrianolaselva.devtools.infrastructure.transform.groovy;

import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import org.apache.commons.lang3.time.StopWatch;
import org.jboss.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GroovyTransform implements Transform {

    private static final Logger logger = Logger.getLogger(GroovyTransform.class);
    private static final String GROOVY_ENGINE_NAME = "groovy";
    private static final String DEFAULT_METHOD_NAME = "apply";
    private static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(GROOVY_ENGINE_NAME);

    private String methodName = DEFAULT_METHOD_NAME;
    private Invocable invocable;

    public Transform setFilePath(final String filePath) throws FileNotFoundException, ScriptException {
        scriptEngine.eval(new FileReader(filePath));
        invocable = (Invocable) scriptEngine;
        return this;
    }

    @Override
    public Transform setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    @Override
    public String apply(final String payload) throws ScriptException, NoSuchMethodException {
        final var elapsed = new StopWatch();
        try {
            elapsed.start();
            return invocable.invokeFunction(this.methodName, payload).toString();
        } finally {
            logger.debugv("execution of {0} method transformation, elapsed {1}ms",
                    methodName,
                    elapsed.getTime(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public List<String> apply(final List<String> columns) throws ScriptException, NoSuchMethodException {
        final var elapsed = new StopWatch();
        try {
            elapsed.start();
            return (List<String>) invocable.invokeFunction(this.methodName, columns);
        } finally {
            logger.debugv("execution of {0} method transformation, elapsed {1}ms",
                    methodName,
                    elapsed.getTime(TimeUnit.MILLISECONDS));
        }
    }
}
