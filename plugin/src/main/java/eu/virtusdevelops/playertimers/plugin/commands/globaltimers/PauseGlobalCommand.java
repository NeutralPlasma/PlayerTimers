package eu.virtusdevelops.playertimers.plugin.commands.globaltimers;

import eu.virtusdevelops.playertimers.api.controllers.GlobalTimersController;
import eu.virtusdevelops.playertimers.api.timer.GlobalTimer;
import eu.virtusdevelops.playertimers.plugin.PlayerTimersPlugin;
import eu.virtusdevelops.playertimers.plugin.commands.AbstractCommand;
import eu.virtusdevelops.playertimers.plugin.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class PauseGlobalCommand implements AbstractCommand {
    private GlobalTimersController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimersPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        timerController = plugin.getGlobalTimersController();
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.pause")
    @Command("timers global pause <name>")
    @CommandDescription("Pauses the timer")
    public void cancelCommand(
            final CommandSender sender,
            @Argument(value = "name", suggestions = "timer_name") final String name
    ){


        var timer = timerController.getActiveTimer(name);
        if(timer == null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Invalid timer!"));
            return;
        }

        timer.pause();
        sender.sendMessage(TextUtil.MM.deserialize("<green>Paused timer"));

    }


    @Suggestions("timer_name")
    public List<String> getTimers(CommandContext<CommandSender> sender, String input){
        var timers = timerController.getActiveTimers();
        return timers.stream().map(GlobalTimer::getName).filter(it -> it.contains(input)).collect(Collectors.toList());

    }
}
