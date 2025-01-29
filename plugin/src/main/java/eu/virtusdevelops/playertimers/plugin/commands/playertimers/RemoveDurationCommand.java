package eu.virtusdevelops.playertimers.plugin.commands.playertimers;

import eu.virtusdevelops.playertimers.api.controllers.TimersController;
import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveDurationCommand implements AbstractCommand {
    private TimersController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimersPlugin plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        timerController = plugin.getTimersController();
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.removetime")
    @Command("timers player removetime <player> <name> <duration>")
    @CommandDescription("Adds time to the timer")
    public void removeTimeCommand(
            final CommandSender sender,
            @Argument(value = "player", suggestions = "player") final String playerName,
            @Argument(value = "name", suggestions = "timer_name") final String name,
            @Argument("duration") final long duration
    ){


        var oPlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        if(oPlayer == null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Invalid player!"));
            return;
        }


        var timer = timerController.getTimer(oPlayer.getUniqueId(), name);
        if(timer == null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Invalid timer!"));
            return;
        }
        timerController.removeTime(timer, duration);
        sender.sendMessage(TextUtil.MM.deserialize("<green>Updated timers time"));
    }


    @Suggestions("player")
    public List<String> getPlayers(CommandContext<CommandSender> sender, String input){
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(it -> it.contains(input)).collect(Collectors.toList());
    }

    @Suggestions("timer_name")
    public List<String> getTimers(CommandContext<CommandSender> sender, String input){
        var playerName = sender.getOrDefault("player", "none");
        var oPlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        if(oPlayer == null)
            return Collections.emptyList();

        var timers = timerController.getPlayerTimers(oPlayer.getUniqueId());
        if(timers == null)
            return Collections.emptyList();
        return timers.stream().map(PlayerTimer::getName).filter(it -> ((String) it).contains(input)).collect(Collectors.toList());

    }
}
