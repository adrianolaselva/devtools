package br.com.adrianolaselva.devtools.application.commands;


import br.com.adrianolaselva.devtools.application.settings.CommandSettings;
import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;


@ApplicationScoped
@Command(name = "devtools", version = "1.0", mixinStandardHelpOptions = true, header = {
    CommandSettings.CLI_TOOLS_BANNER
}, description = CommandSettings.CLI_TOOLS_DESCRIPTION)
public class ContainerCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        return null;
    }
}
