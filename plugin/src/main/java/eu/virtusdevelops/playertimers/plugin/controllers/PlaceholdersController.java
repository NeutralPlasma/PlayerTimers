package eu.virtusdevelops.playertimers.plugin.controllers;

import eu.virtusdevelops.playertimers.api.controllers.GlobalTimersController;
import eu.virtusdevelops.playertimers.api.controllers.TimersController;
import eu.virtusdevelops.playertimers.plugin.PlayerTimersPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;


public class PlaceholdersController extends PlaceholderExpansion {
    private final PlayerTimersPlugin plugin;
    private final TimersController timerController;
    private final GlobalTimersController globalTimersController;
    public PlaceholdersController(PlayerTimersPlugin plugin){
        this.plugin = plugin;
        this.timerController = plugin.getTimersController();
        this.globalTimersController = plugin.getGlobalTimersController();
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
        if(ident.isBlank()) return null;
        var split = ident.split("_");
        if(split.length < 3) return null;

        if(split[0].equalsIgnoreCase("player")){

            switch (split[1]){
                case "time":{
                    var timer = timerController.getTimer(player.getUniqueId(), split[2]);
                    if(timer != null){
                        return timer.getDuration() + "";
                    }else{
                        return "0";
                    }
                }
                case "total":{
                    var timer = timerController.getTimer(player.getUniqueId(), split[2]);
                    if(timer != null){
                        return timer.getTotalDuration() + "";
                    }else{
                        return "0";
                    }
                }
                case "parsed":{
                    var timer = timerController.getTimer(player.getUniqueId(), split[2]);
                    if(timer != null){
                        return DurationFormatUtils.formatDuration(timer.getDuration() * 1000, "mm'm' ss's'", false);
                    }else{
                        return "0";
                    }
                }case "totalparsed":{
                    var timer = timerController.getTimer(player.getUniqueId(), split[2]);
                    if(timer != null){
                        return DurationFormatUtils.formatDuration(timer.getTotalDuration() * 1000, "mm'm' ss's'", false);
                    }else{
                        return "0";
                    }
                }
                case "status":{
                    var timer = timerController.getTimer(player.getUniqueId(), split[2]);
                    if(timer != null){
                        return timer.isPaused() ? "paused" : "running";
                    }else{
                        return "0";
                    }
                }
            }


        }else if(split[0].equalsIgnoreCase("global")){
            switch (split[1]){
                case "time":{
                    var timer = globalTimersController.getTimer(split[2]);
                    if(timer != null){
                        return timer.getDuration() + "";
                    }else{
                        return "0";
                    }
                }
                case "total":{
                    var timer = globalTimersController.getTimer(split[2]);
                    if(timer != null){
                        return timer.getTotalDuration() + "";
                    }else{
                        return "0";
                    }
                }
                case "parsed":{
                    var timer = globalTimersController.getTimer(split[2]);
                    if(timer != null){
                        return DurationFormatUtils.formatDuration(timer.getDuration() * 1000, "mm'm' ss's'", false);
                    }else{
                        return "0";
                    }
                }case "totalparsed":{
                    var timer = globalTimersController.getTimer(split[2]);
                    if(timer != null){
                        return DurationFormatUtils.formatDuration(timer.getTotalDuration() * 1000, "mm'm' ss's'", false);
                    }else{
                        return "0";
                    }
                }
                case "status":{
                    var timer = globalTimersController.getTimer(split[2]);
                    if(timer != null){
                        return timer.isPaused() ? "paused" : "running";
                    }else{
                        return "0";
                    }
                }
            }
        }




        return null;
    }
}
