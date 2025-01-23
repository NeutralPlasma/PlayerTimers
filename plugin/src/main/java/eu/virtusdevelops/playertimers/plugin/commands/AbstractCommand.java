package eu.virtusdevelops.playertimers.plugin.commands;

import eu.virtusdevelops.playertimers.plugin.PlayerTimers;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;

public interface AbstractCommand {
    void registerCommand(
            @NonNull PlayerTimers plugin,
            @NonNull AnnotationParser<CommandSender> annotationParser
    );
}