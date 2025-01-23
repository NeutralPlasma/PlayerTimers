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

import java.util.List;
import java.util.stream.Collectors;

public class CreateCommand implements AbstractCommand {
    private PlayerTimers plugin;
    private TimerController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimers plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        timerController = plugin.getTimerController();
        annotationParser.parse(this);
    }

    @Permission("playertimers.command.create")
    @Command("ptimers create <player> <name> <duration> <offline_tick>")
    @CommandDescription("Creates new timer for specific user")
    public void helpCommand(
            final CommandSender sender,
            final @Argument(value = "player", suggestions = "player") @NonNull String playerName,
            final @Argument("name") @NonNull String name,
            final @Argument("duration") long duration,
            final @Argument("offline_tick") boolean offlineTick
    ){


        var oPlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        if(oPlayer == null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Invalid player!"));
            return;
        }

        var timers = timerController.getTimer(oPlayer.getUniqueId(), name);
        if(timers != null){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Timer with that name already exists!"));
            return;
        }

        if(duration <= 0){
            sender.sendMessage(TextUtil.MM.deserialize("<red>Duration cannot be negative!"));
            return;
        }

        var timer = timerController.createTimer(oPlayer.getUniqueId(), name, duration, offlineTick);
        sender.sendMessage(TextUtil.MM.deserialize("<green>Successfully created new timer!"));
    }

    @Suggestions("player")
    public List<String> getPlayers(CommandContext<CommandSender> sender, String input){
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(it -> it.contains(input)).collect(Collectors.toList());
    }
}
