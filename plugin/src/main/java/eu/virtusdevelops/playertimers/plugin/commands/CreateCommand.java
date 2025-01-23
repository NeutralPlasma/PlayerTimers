package eu.virtusdevelops.playertimers.plugin.commands;

import eu.virtusdevelops.playertimers.api.controllers.TimersController;
import eu.virtusdevelops.playertimers.plugin.PlayerTimers;
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

public class CreateCommand implements AbstractCommand {
    private PlayerTimers plugin;
    private TimersController timerController;

    @Override
    public void registerCommand(@NonNull PlayerTimers plugin, @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;
        timerController = plugin.getTimersController();
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

        if(timerController.createTimer(oPlayer.getUniqueId(), name, duration, offlineTick) != null){
            sender.sendMessage(TextUtil.MM.deserialize("<green>Successfully created new timer!"));
        }else{
            sender.sendMessage(TextUtil.MM.deserialize("<red>Something wen't wrong while creating timer!"));
        }
    }

    @Suggestions("player")
    public List<String> getPlayers(CommandContext<CommandSender> sender, String input){
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(it -> it.contains(input)).collect(Collectors.toList());
    }
}
