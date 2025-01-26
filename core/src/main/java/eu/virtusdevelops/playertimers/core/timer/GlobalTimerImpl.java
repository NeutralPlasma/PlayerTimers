package eu.virtusdevelops.playertimers.core.timer;

import eu.virtusdevelops.playertimers.api.timer.GlobalTimer;
import eu.virtusdevelops.playertimers.api.timer.LinkedPlayer;

import java.util.List;
import java.util.UUID;

public class GlobalTimerImpl implements GlobalTimer {

    private final UUID id;
    private final String name;
    private final List<LinkedPlayer> linkedPlayers;
    private final List<String> independentCommands;
    private final List<String> commands;
    private final long startTime;

    private long duration;
    private long totalDuration;
    private boolean finished;
    private boolean executed;
    private boolean paused;
    private long endTime;

    private boolean updated;


    public GlobalTimerImpl(UUID id,
                           String name,
                           long duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.totalDuration = duration;
        this.finished = false;
        this.executed = false;
        this.paused = false;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.updated = false;

        this.linkedPlayers = List.of();
        this.commands = List.of();
        this.independentCommands = List.of();
    }

    public GlobalTimerImpl(UUID id, String name, long duration,
                           long totalDuration, boolean finished, boolean executed,
                           boolean paused, long startTime, long endTime,
                           List<LinkedPlayer> linkedPlayers, List<String> commands,
                           List<String> independentCommands){
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.totalDuration = totalDuration;
        this.finished = finished;
        this.executed = executed;
        this.paused = paused;
        this.startTime = startTime;
        this.endTime = endTime;
        this.linkedPlayers = linkedPlayers;
        this.commands = commands;
        this.independentCommands = independentCommands;
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
    public boolean isDependentExecuted() {
        return linkedPlayers.stream().allMatch(LinkedPlayer::isExecuted);
    }

    @Override
    public void addTime(long duration) {
        totalDuration+=duration;
        duration+=duration;
        updated = true;
    }

    @Override
    public void removeTime(long duration) {
        totalDuration-=duration;
        duration-=duration;
        updated = true;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
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
        duration--;
        if(duration <= 0){
            endTime = System.currentTimeMillis();
            finished = true;
        }
    }

    @Override
    public List<String> getIndependentCommands() {
        return List.of();
    }

    @Override
    public void addIndependentCommand(String command) {
        independentCommands.add(command);
    }

    @Override
    public void removeIndependentCommand(int index) {
        if(index < 0 || index >= independentCommands.size()) return;
        independentCommands.remove(index);
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }

    @Override
    public void addCommand(String command) {
        commands.add(command);
    }

    @Override
    public void removeCommand(int index) {
        if(index < 0 || index >= commands.size()) return;
        commands.remove(index);
    }

    @Override
    public void linkPlayer(UUID playerId) {
        linkedPlayers.add(new LinkedPlayerImpl(playerId));
    }

    @Override
    public List<LinkedPlayer> getLinkedPlayers() {
        return List.of();
    }


}
