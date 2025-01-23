package eu.virtusdevelops.playertimers.controllers;

import eu.virtusdevelops.playertimers.PlayerTimers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;


public class PlaceholdersController extends PlaceholderExpansion {
    private final PlayerTimers plugin;
    private final TimerController timerController;
    public PlaceholdersController(PlayerTimers plugin){
        this.plugin = plugin;
        this.timerController = plugin.getTimerController();
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
                    return "invalid";
                }
            }
            case "parsed":{
                var timer = timerController.getTimer(player.getUniqueId(), splitted[1]);
                if(timer != null){
                    return DurationFormatUtils.formatDuration(timer.getDuration() * 1000, "mm'm' ss's'", false);
                }else{
                    return "invalid";
                }
            }
        }

        return null;
    }
}
