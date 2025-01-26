package eu.virtusdevelops.playertimers.plugin.controllers;

import eu.virtusdevelops.playertimers.api.controllers.TimersController;
import eu.virtusdevelops.playertimers.plugin.PlayerTimers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;


public class PlaceholdersController extends PlaceholderExpansion {
    private final PlayerTimers plugin;
    private final TimersController timerController;
    public PlaceholdersController(PlayerTimers plugin){
        this.plugin = plugin;
        this.timerController = plugin.getTimersController();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "playertimers";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NeutralPlasma";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }



    @Override
    public String onRequest(OfflinePlayer player, String ident){
        if(ident.isBlank() || ident.isEmpty()) return null;
        var splitted = ident.split("_");

        switch (splitted[0]){
            case "time":{
                var timer = timerController.getTimer(player.getUniqueId(), splitted[1]);
                if(timer != null){
                    return timer.getDuration() + "";
                }else{
                    return "0";
                }
            }
            case "total":{
                var timer = timerController.getTimer(player.getUniqueId(), splitted[1]);
                if(timer != null){
                    return timer.getTotalDuration() + "";
                }else{
                    return "0";
                }
            }
            case "parsed":{
                var timer = timerController.getTimer(player.getUniqueId(), splitted[1]);
                if(timer != null){
                    return DurationFormatUtils.formatDuration(timer.getDuration() * 1000, "mm'm' ss's'", false);
                }else{
                    return "0";
                }
            }case "totalparsed":{
                var timer = timerController.getTimer(player.getUniqueId(), splitted[1]);
                if(timer != null){
                    return DurationFormatUtils.formatDuration(timer.getTotalDuration() * 1000, "mm'm' ss's'", false);
                }else{
                    return "0";
                }
            }
            case "status":{
                var timer = timerController.getTimer(player.getUniqueId(), splitted[1]);
                if(timer != null){
                    return timer.isPaused() ? "paused" : "running";
                }else{
                    return "0";
                }
            }
        }

        return null;
    }
}
