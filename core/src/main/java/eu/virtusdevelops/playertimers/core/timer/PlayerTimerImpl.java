package eu.virtusdevelops.playertimers.core.timer;

import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerTimerImpl implements PlayerTimer {
    private final UUID timerID, playerID;
    private long startTime, endTime, duration;
    private final String name;
    private boolean executed = false;
    private boolean offlineTick = false;
    private boolean paused = false;
    private List<String> commands;

    private Player player;
    private OfflinePlayer oPlayer;

    private boolean updated = false;

    public PlayerTimerImpl(UUID timerID, UUID playerID, long startTime, long endTime, long duration, String name, boolean offlineTick, boolean paused, List<String> commands) {
        this.timerID = timerID;
        this.playerID = playerID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.name = name;
        this.offlineTick = offlineTick;
        this.commands = commands;
        this.paused = paused;
        this.player = Bukkit.getPlayer(playerID);
        this.oPlayer = Bukkit.getOfflinePlayer(playerID);
    }

    public void tick(){
        if(paused) return;
        if(!offlineTick &&  Bukkit.getPlayer(playerID) == null) return;
        updated = true;
        duration--;
        if(duration <= 0){
            endTime = System.currentTimeMillis();
        }
    }

    @Override
    public void addTime(long duration){
        this.duration+=duration;
        updated = true;
    }

    @Override
    public void removeTime(long duration){
        this.duration-=duration;
        updated = true;
    }

    @Override
    public boolean isFinished() {
        return duration <= 0;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
        updated = true;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        updated = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }

    @Override
    public Player getPlayer() {
        if(player == null){
            player = Bukkit.getPlayer(playerID);
        }
        return player;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return oPlayer;
    }


    @Override
    public UUID getPlayerID() {
        return playerID;
    }

    @Override
    public UUID getId() {
        return timerID;
    }

    @Override
    public boolean isOfflineTick() {
        return offlineTick;
    }

    public void setOfflineTick(boolean offlineTick) {
        this.offlineTick = offlineTick;
        updated = true;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        if(paused != this.paused)
            this.updated = true;
        this.paused = paused;
    }

    @Override
    public boolean pause() {
        if(paused) return false;
        paused = true;
        return true;
    }

    @Override
    public boolean resume() {
        if(!paused) return false;
        paused = false;
        return true;
    }
}
