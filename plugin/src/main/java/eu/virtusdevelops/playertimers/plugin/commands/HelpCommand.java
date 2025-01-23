package eu.virtusdevelops.playertimers.plugin.commands;

import eu.virtusdevelops.playertimers.plugin.PlayerTimers;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HelpCommand implements AbstractCommand {
    private PlayerTimers plugin;

    @Override
    public void registerCommand(@NonNull PlayerTimers plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.help")
    @Command("ptimers help [query]")
    public void helpCommand(
            final CommandSender sender,
            @Argument("query") @Nullable final String[] query
    ){

        plugin.getMinecraftHelp().queryCommands(query != null ? String.join(" ", query) : "", sender);

    }


}
