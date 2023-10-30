package br.com.adrianolaselva.devtools.application;

import br.com.adrianolaselva.devtools.application.commands.ContainerCommand;
import br.com.adrianolaselva.devtools.application.commands.file.ImporterCommand;
import br.com.adrianolaselva.devtools.application.commands.kafka.ConsumerCommand;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;

@QuarkusMain
public class DevToolsApplication implements QuarkusApplication {

    private final ContainerCommand containerCommand;
    private final ConsumerCommand consumerCommand;
    private final ImporterCommand importerCommand;

    public DevToolsApplication(final ContainerCommand containerCommand,
                               final ConsumerCommand consumerCommand,
                               final ImporterCommand importerCommand) {
        this.containerCommand = containerCommand;
        this.consumerCommand = consumerCommand;
        this.importerCommand = importerCommand;
    }

    @Override
    public int run(final String... args) throws Exception {
        return new CommandLine(containerCommand)
            .addSubcommand(consumerCommand)
            .addSubcommand(importerCommand)
            .execute(args.length == 0 ? new String[] {"-h"} : args);
    }

    public static void main(String... args) {
        Quarkus.run(DevToolsApplication.class, args);
    }
}
