package eu.virtusdevelops.playertimers.models;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerTimer {
    private final UUID timerID, playerID;
    private long startTime, endTime, duration;
    private final String name;
    private boolean executed = false;
    private boolean offlineTick = false;
    private List<String> commands;

    private Player player;
    private OfflinePlayer oPlayer;

    public PlayerTimer(UUID timerID, UUID playerID, long startTime, long endTime, long duration, String name, boolean offlineTick, List<String> commands) {
        this.timerID = timerID;
        this.playerID = playerID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.name = name;
        this.offlineTick = offlineTick;
        this.commands = commands;
        this.player = Bukkit.getPlayer(playerID);
        this.oPlayer = Bukkit.getOfflinePlayer(playerID);
    }

    public void tick(){
        if(!offlineTick &&  Bukkit.getPlayer(playerID) == null) return;


        duration--;
        if(duration <= 0){
            endTime = System.currentTimeMillis();
        }
    }

    public void addTime(long duration){
        this.duration+=duration;
    }
    public void removeTime(long duration){
        this.duration-=duration;
    }

    public boolean isFinished() {
        return duration <= 0;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public List<String> getCommands() {
        return commands;
    }

    public Player getPlayer() {
        if(player == null){
            player = Bukkit.getPlayer(playerID);
        }
        return player;
    }

    public OfflinePlayer getoPlayer() {
        return oPlayer;
    }

    public UUID getTimerID() {
        return timerID;
    }

    public UUID getPlayerID() {
        return playerID;
    }


    public boolean isOfflineTick() {
        return offlineTick;
    }

    public void setOfflineTick(boolean offlineTick) {
        this.offlineTick = offlineTick;
    }
}
