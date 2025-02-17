package eu.virtusdevelops.playertimers.plugin.commands.globaltimers;

import eu.virtusdevelops.playertimers.api.controllers.GlobalTimersController;
import eu.virtusdevelops.playertimers.plugin.PlayerTimersPlugin;
import eu.virtusdevelops.playertimers.plugin.commands.AbstractCommand;
import eu.virtusdevelops.playertimers.plugin.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.*;
import org.jetbrains.annotations.NotNull;

public class CreateGlobalCommand implements AbstractCommand {
    private GlobalTimersController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimersPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        timerController = plugin.getGlobalTimersController();
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.create")
    @Command("timers global create <name> <duration>")
    @CommandDescription("Creates new global timer")
    public void createCommand(
            final CommandSender sender,
            final @Argument("name") @NonNull String name,
            final @Argument("duration") long duration
    ){
        var timers = timerController.getActiveTimer(name);
        if(timers != null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Timer with that name already exists!"));
            return;
        }

        if(duration <= 0){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Duration cannot be negative!"));
            return;
        }

        if(timerController.createTimer(name, duration) != null){
            sender.sendMessage(TextUtil.MM.deserialize("<green>Successfully created new timer!"));
        }else{
            sender.sendMessage(TextUtil.MM.deserialize("<red>Something went wrong while creating timer!"));
        }
    }
}
