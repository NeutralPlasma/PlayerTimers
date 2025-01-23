package eu.virtusdevelops.playertimers.plugin.commands;

import eu.virtusdevelops.playertimers.plugin.PlayerTimers;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;

import java.util.Arrays;
import java.util.List;

public class CommandsRegistry {


    private static final List<AbstractCommand> COMMANDS = Arrays.asList(
            new HelpCommand(),
            new CheckCommand(),
            new CreateCommand(),
            new AddTimerCommandCommand(),
            new AddDurationCommand(),
            new RemoveDurationCommand(),
            new CancelCommand(),
            new StopCommand(),
            new PauseCommand(),
            new ResumeCommand()
    );

    private final PlayerTimers plugin;
    private final AnnotationParser<CommandSender> annotationParser;

    public CommandsRegistry(
            final @NonNull PlayerTimers plugin,
            final @NonNull CommandManager<CommandSender> manager
    ){
        this.plugin = plugin;
        this.annotationParser = new AnnotationParser<>(manager, CommandSender.class);

        this.setupCommands();
    }

    private void setupCommands() {
        COMMANDS.forEach(feature -> feature.registerCommand(this.plugin, this.annotationParser));
    }

}
