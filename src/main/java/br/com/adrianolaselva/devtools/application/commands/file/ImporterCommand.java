package br.com.adrianolaselva.devtools.application.commands.file;

import br.com.adrianolaselva.devtools.application.settings.CommandSettings;
import br.com.adrianolaselva.devtools.domain.importer.enums.FileCharset;
import br.com.adrianolaselva.devtools.domain.importer.interfaces.ScanFilePath;
import br.com.adrianolaselva.devtools.domain.transform.enums.TransformType;
import br.com.adrianolaselva.devtools.domain.transform.exceptions.RetrieveTransformException;
import br.com.adrianolaselva.devtools.domain.transform.interfaces.Transform;
import br.com.adrianolaselva.devtools.infrastructure.configs.TransformContextBean;
import br.com.adrianolaselva.devtools.infrastructure.importer.CustomFileVisitor;
import jakarta.enterprise.context.Dependent;
import org.apache.commons.lang3.time.StopWatch;
import org.jboss.logging.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Dependent
@Command(name = "file-importer", helpCommand = true, mixinStandardHelpOptions = true, description = {
        CommandSettings.CLI_TOOLS_IMPORTER_DESCRIPTION
})
public class ImporterCommand implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ImporterCommand.class);

    @Option(names = {"-f", "--file-path"}, required = true, description = "Directory or file to be imported")
    public String filePathParameter;

    @Option(names = {"-m", "--move-to-path"}, required = true, description = "Directory to be moved after imported")
    public String moveToPathPathParameter;

    @Option(names = {"-c", "--charset"}, description = "Directory to be moved after imported", defaultValue = "UTF_8")
    public FileCharset fileCharsetParameter = FileCharset.UTF_8;

    @Option(names = {"--transform-type"}, description = "Transformation script type", defaultValue = "GROOVY")
    public TransformType transformtypeParameter;

    @Option(names = {"--transform-path"}, description = "Definition of script path if necessary to apply transformation")
    public String transformPathParameter;

    @Option(names = {"--transform-method"}, description = "Method name in script implementation", defaultValue = "apply")
    public String transformMethodParameter;

    private final ScanFilePath scanFilePath;
    private final CustomFileVisitor customFileVisitor;

    public ImporterCommand(final ScanFilePath scanFilePath, final CustomFileVisitor customFileVisitor) {
        this.scanFilePath = scanFilePath;
        this.customFileVisitor = customFileVisitor;
    }

    @Override
    public Integer call() throws Exception {
        final var elapsed = new StopWatch();
        elapsed.start();

        try {
            scanFilePath
                    .setFileVisitor(
                            customFileVisitor
                                    .setMoveToPathPathParameter(this.moveToPathPathParameter)
                                    .setFileCharset(this.fileCharsetParameter)
                                    .setTransform(loadTransformInstance())
                    )
                    .setFilePath(Path.of(this.filePathParameter))
                    .process();
        } catch (Exception e) {
            logger.error("failed to import data", e);
        } finally {
            elapsed.stop();
        }

        return null;
    }

    private Transform loadTransformInstance() {
        try {
            return transformPathParameter != null ? TransformContextBean.getInstance(transformtypeParameter)
                    .setFilePath(transformPathParameter)
                    .setMethodName(transformMethodParameter) : null;
        } catch (FileNotFoundException | ScriptException e) {
            throw new RetrieveTransformException(e);
        }
    }
}
