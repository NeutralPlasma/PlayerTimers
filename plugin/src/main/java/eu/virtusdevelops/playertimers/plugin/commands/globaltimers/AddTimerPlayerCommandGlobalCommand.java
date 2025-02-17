package eu.virtusdevelops.playertimers.plugin.commands.globaltimers;

import eu.virtusdevelops.playertimers.api.controllers.GlobalTimersController;
import eu.virtusdevelops.playertimers.api.timer.GlobalTimer;
import eu.virtusdevelops.playertimers.plugin.PlayerTimersPlugin;
import eu.virtusdevelops.playertimers.plugin.commands.AbstractCommand;
import eu.virtusdevelops.playertimers.plugin.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class AddTimerPlayerCommandGlobalCommand implements AbstractCommand {
    private GlobalTimersController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimersPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        timerController = plugin.getGlobalTimersController();
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.addcommand")
    @Command("timers global addplayercommand <name> <command>")
    @CommandDescription("Adds command to be executed when timer finishes")
    public void addCommand(
            final CommandSender sender,
            @Argument(value = "name", suggestions = "timer_name") final String name,
            @Argument("command") final String[] command
    ){


        var timer = timerController.getActiveTimer(name);
        if(timer == null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Invalid timer!"));
            return;
        }
        timer.addPlayerCommand(String.join(" ", command));

        sender.sendMessage(TextUtil.MM.deserialize("<green>Added player command to timer"));
    }


    @Suggestions("timer_name")
    public List<String> getTimers(CommandContext<CommandSender> sender, String input){
        var timers = timerController.getActiveTimers();
        return timers.stream().map(GlobalTimer::getName).filter(it -> it.contains(input)).collect(Collectors.toList());

    }
}
