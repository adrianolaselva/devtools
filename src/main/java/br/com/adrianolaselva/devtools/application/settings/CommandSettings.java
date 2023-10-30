package br.com.adrianolaselva.devtools.application.settings;

public final class CommandSettings {

    private CommandSettings() {
        throw new IllegalStateException("this class cannot be instantiated");
    }

    public static final String CLI_TOOLS_BANNER = """
            ____                 __                         ______            __
           / __ \\___ _   _____  / /___  ____  ___  _____   /_  __/___  ____  / /____
          / / / / _ \\ | / / _ \\/ / __ \\/ __ \\/ _ \\/ ___/    / / / __ \\/ __ \\/ / ___/
         / /_/ /  __/ |/ /  __/ / /_/ / /_/ /  __/ /       / / / /_/ / /_/ / (__  )
        /_____/\\___/|___/\\___/_/\\____/ .___/\\___/_/       /_/  \\____/\\____/_/____/
                                    /_/
        """;

    public static final String CLI_TOOLS_DESCRIPTION = """
        DevTools is a command-line tool (CLI) designed to simplify routine tasks faced by developers in their
        day-to-day lives. This tool was created to group a series of essential commands, providing developers with
        a more productive and efficient.
        """;

    public static final String CLI_TOOLS_KAFKA_CONSUMER_DESCRIPTION = """
        Command responsible for consume kafka events
        """;

    public static final String CLI_TOOLS_IMPORTER_DESCRIPTION = """
        Command responsible for import files by path
        """;
}
