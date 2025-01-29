package eu.virtusdevelops.playertimers.plugin.commands;

import eu.virtusdevelops.playertimers.plugin.PlayerTimersPlugin;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;

public interface AbstractCommand {
    void registerCommand(
            @NonNull PlayerTimersPlugin plugin,
            @NonNull AnnotationParser<CommandSender> annotationParser
    );
}