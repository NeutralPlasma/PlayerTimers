package eu.virtusdevelops.playertimers.core.timer;

import eu.virtusdevelops.playertimers.api.timer.GlobalTimer;
import eu.virtusdevelops.playertimers.api.timer.LinkedPlayer;
import eu.virtusdevelops.playertimers.api.timer.TimerCommand;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GlobalTimerImpl implements GlobalTimer {

    private final UUID id;
    private final String name;
    private final List<LinkedPlayer> linkedPlayers;
    private final List<TimerCommand> independentCommands;
    private final List<TimerCommand> commands;
    private final long startTime;

    private long duration;
    private long totalDuration;
    private boolean finished;
    private boolean executed;
    private boolean dependentExecuted;
    private boolean paused;
    private long endTime;

    private boolean updated;
    private boolean commandsUpdated;
    private boolean playersUpdated;


    public GlobalTimerImpl(UUID id,
                           String name,
                           long duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.totalDuration = duration;
        this.finished = false;
        this.executed = false;
        this.dependentExecuted = false;
        this.paused = false;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.updated = false;
        this.commandsUpdated = false;
        this.playersUpdated = false;

        this.linkedPlayers = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.independentCommands = new ArrayList<>();
    }

    public GlobalTimerImpl(UUID id, String name, long duration,
                           long totalDuration, boolean finished, boolean executed,
                           boolean dependentExecuted,
                           boolean paused, long startTime, long endTime,
                           List<LinkedPlayer> linkedPlayers, List<TimerCommand> commands,
                           List<TimerCommand> independentCommands){
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.totalDuration = totalDuration;
        this.finished = finished;
        this.executed = executed;
        this.dependentExecuted = dependentExecuted;
        this.paused = paused;
        this.startTime = startTime;
        this.endTime = endTime;
        this.linkedPlayers = linkedPlayers;
        this.commands = commands;
        this.independentCommands = independentCommands;

        this.updated = false;
        this.commandsUpdated = false;
        this.playersUpdated = false;
    }


    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public long getTotalDuration() {
        return totalDuration;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public boolean isPlayerExecuted() {
        if(!executed) return false;
        if(dependentExecuted) return true;
        dependentExecuted = linkedPlayers.stream().allMatch(LinkedPlayer::isExecuted);
        updated = true;
        return dependentExecuted;
    }

    @Override
    public void setExecuted(boolean executed) {
        this.executed = executed;
        updated = true;
    }

    @Override
    public void addTime(long duration) {
        this.totalDuration+=duration;
        this.duration+=duration;
        updated = true;
    }

    @Override
    public void removeTime(long duration) {
        this.totalDuration-=duration;
        this.duration-=duration;
        updated = true;
    }

    @Override
    public boolean pause() {
        if(paused) return false;
        paused = true;
        updated = true;
        return true;
    }

    @Override
    public boolean resume() {
        if(!paused) return false;
        paused = false;
        updated = true;
        return false;
    }

    @Override
    public void cancel() {
        duration = 0;
        executed = true;
        finished = true;
        updated = true;
    }

    @Override
    public void finish() {
        duration = 0;
        finished = true;
        updated = true;
    }

    @Override
    public void tick() {
        if(paused) return;
        if(duration < 0 || finished) return;
        duration--;
        updated = true;
        if(duration <= 0){
            endTime = System.currentTimeMillis();
            finished = true;
        }
    }

    @Override
    public List<TimerCommand> getCommands() {
        return independentCommands;
    }

    @Override
    public void addCommand(String command) {
        if(finished) return;
        this.independentCommands.add(new TimerCommandImpl(UUID.randomUUID(), command));
        this.commandsUpdated = true;
        this.updated = true;
    }

    @Override
    public void removeCommand(int index) {
        if(finished) return;
        if(index < 0 || index >= independentCommands.size()) return;
        this.independentCommands.remove(index);
        this.commandsUpdated = true;
        this.updated = true;
    }

    @Override
    public List<TimerCommand> getPlayerCommands() {
        return commands;
    }

    @Override
    public void addPlayerCommand(String command) {
        if(finished) return;
        this.commands.add(new TimerCommandImpl(UUID.randomUUID(), command));
        this.commandsUpdated = true;
        this.updated = true;
    }

    @Override
    public void removePlayerCommand(int index) {
        if(finished) return;
        if(index < 0 || index >= commands.size()) return;
        this.commands.remove(index);
        this.commandsUpdated = true;
        this.updated = true;
    }

    @Override
    public void linkPlayer(UUID playerId) {
        if(finished) return;
        if(linkedPlayers.stream().anyMatch(lp -> lp.getPlayer_id().equals(playerId))) return;
        linkedPlayers.add(new LinkedPlayerImpl(UUID.randomUUID(), playerId));
        this.dependentExecuted = false;
        this.playersUpdated = true;
        this.updated = true;
    }

    @Override
    public List<LinkedPlayer> getLinkedPlayers() {
        return linkedPlayers;
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
    public boolean isPaused() {
        return paused;
    }

    // just for database updating
    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isCommandsUpdated() {
        return commandsUpdated;
    }

    public void setCommandsUpdated(boolean commandsUpdated) {
        this.commandsUpdated = commandsUpdated;
    }

    public boolean isPlayersUpdated() {
        return playersUpdated;
    }

    public void setPlayersUpdated(boolean playersUpdated) {
        this.playersUpdated = playersUpdated;
    }
}
