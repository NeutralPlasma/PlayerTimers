package eu.virtusdevelops.playertimers.commands;

import eu.virtusdevelops.playertimers.PlayerTimers;
import eu.virtusdevelops.playertimers.controllers.TimerController;
import eu.virtusdevelops.playertimers.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class CheckCommand implements AbstractCommand {
    private PlayerTimers plugin;
    private TimerController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimers plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        timerController = plugin.getTimerController();
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.check")
    @Command("ptimers check <player>")
    @CommandDescription("Shows all active timers for user")
    public void helpCommand(
            final CommandSender sender,
            @Argument(value = "player", suggestions = "player")  final String playerName
    ){


        var oPlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        if(oPlayer == null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Invalid player!"));
            return;
        }

        var timers = timerController.getPlayerTimers(oPlayer.getUniqueId());
        sender.sendMessage(TextUtil.MM.deserialize("<yellow>List of all the timers: "));
        if(timers != null){
            for(var timer : timers){
                int index = 0;
                StringBuilder commands = new StringBuilder();// String.join("\n     <dark_gray>├─  <green>", timer.getCommands());
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
                            .append(command);
                    index++;
                }
                sender.sendMessage(TextUtil.MM.deserialize("<dark_gray>└"
                        + "<green>" + timer.getName() + "\n"
                        + "  <dark_gray>├─ <yellow>Duration: <green>" + timer.getDuration() + "\n"
                        + "  <dark_gray>├─ <yellow>Start time: <green>" + timer.getStartTime() + "\n"
                        + "  <dark_gray>├─ <yellow>End time: <green>" + timer.getEndTime() + "\n"
                        + "  <dark_gray>└─ <yellow>Commands:"
                        + commands
                ));
            }
        }

        sender.sendMessage(TextUtil.MM.deserialize("<yellow>Timers awaiting execution:"));
        var toExecuteTimers = timerController.getPlayerAwaitngTimers(oPlayer.getUniqueId());

        if(toExecuteTimers != null){
            for(var timer : toExecuteTimers){
                int index = 0;
                StringBuilder commands = new StringBuilder();// String.join("\n     <dark_gray>├─  <green>", timer.getCommands());
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
                            .append(command);
                    index++;
                }
                sender.sendMessage(TextUtil.MM.deserialize("<dark_gray>└"
                        + "<green>" + timer.getName() + "\n"
                        + "  <dark_gray>├─ <yellow>Duration: <green>" + timer.getDuration() + "\n"
                        + "  <dark_gray>├─ <yellow>Start time: <green>" + timer.getStartTime() + "\n"
                        + "  <dark_gray>├─ <yellow>End time: <green>" + timer.getEndTime() + "\n"
                        + "  <dark_gray>└─ <yellow>Commands:"
                        + commands
                ));
            }
        }

    }



    @Suggestions("player")
    public List<String> getPlayers(CommandContext<CommandSender> sender, String input){
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(it -> it.contains(input)).collect(Collectors.toList());
    }
}
