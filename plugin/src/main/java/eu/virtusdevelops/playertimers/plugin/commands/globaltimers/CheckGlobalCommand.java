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

public class CheckGlobalCommand implements AbstractCommand {
    private GlobalTimersController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimersPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        timerController = plugin.getGlobalTimersController();
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.check")
    @Command("timers global check <timer>")
    @CommandDescription("Shows data for specific global timer")
    public void helpCommand(
            final CommandSender sender,
            @Argument(value = "timer", suggestions = "timer_name")  final String timerName
    ){



        var timer = timerController.getTimer(timerName);
        if(timer == null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Invalid timer!"));
            return;
        }

        sender.sendMessage(TextUtil.MM.deserialize("<yellow>Timer information: "));

        int index = 0;
        StringBuilder commands = new StringBuilder();
        for(var command : timer.getCommands()){
            commands.append("\n        ");
            if(index+1 == timer.getCommands().size()){
                commands.append("<dark_gray>└─ <yellow>");
            }else{
                commands.append("<dark_gray>├─ <yellow>");
            }
            commands
                    .append(index)
                    .append(" <gray>: <green>")
                    .append(command.getCommand());
            index++;
        }

        index = 0;
        StringBuilder playerCommands = new StringBuilder();
        for(var playerCommand : timer.getPlayerCommands()){
            playerCommands.append("\n        ");
            if(index+1 == timer.getPlayerCommands().size()){
                playerCommands.append("<dark_gray>└─ <yellow>");
            }else{
                playerCommands.append("<dark_gray>├─ <yellow>");
            }
            playerCommands
                    .append(index)
                    .append(" <gray>: <green>")
                    .append(playerCommand.getCommand());
            index++;
        }

        index = 0;
        StringBuilder players = new StringBuilder();
        for(var linkedPlayer : timer.getLinkedPlayers()){
            players.append("\n        ");
            if(index+1 == timer.getLinkedPlayers().size()){
                players.append("<dark_gray>└─ <yellow>");
            }else{
                players.append("<dark_gray>├─ <yellow>");
            }
            players
                    .append(index)
                    .append(" <gray>: <green>")
                    .append(linkedPlayer.getPlayer_id());
            index++;
        }


        sender.sendMessage(TextUtil.MM.deserialize("<dark_gray>└"
                + "<green>" + timer.getName() + "\n"
                + "  <dark_gray>├─ <yellow>Duration: <green>" + timer.getDuration() + "\n"
                + "  <dark_gray>├─ <yellow>Total duration: <green>" + timer.getTotalDuration() + "\n"
                + "  <dark_gray>├─ <yellow>Start time: <green>" + timer.getStartTime() + "\n"
                + "  <dark_gray>├─ <yellow>End time: <green>" + timer.getEndTime() + "\n"
                + "  <dark_gray>├─ <yellow>Status: " + (timer.isPaused() ? "<red>Paused" : "<green>Running") + "\n"
                + "  <dark_gray>├─ <yellow>Commands:"
                + commands + "\n"
                + "  <dark_gray>├─ <yellow>Player commands:"
                + playerCommands + "\n"
                + "  <dark_gray>└─ <yellow>Players:"
                + players

        ));

    }


    @Suggestions("timer_name")
    public List<String> getTimers(CommandContext<CommandSender> sender, String input){
        var timers = timerController.getActiveTimers();
        return timers.stream().map(GlobalTimer::getName).filter(it -> it.contains(input)).collect(Collectors.toList());

    }

}
