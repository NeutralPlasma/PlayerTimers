package eu.virtusdevelops.playertimers.api.timer;

import java.util.List;
import java.util.UUID;

public interface GlobalTimer {

    UUID getId();

    String getName();

    long getDuration();

    long getTotalDuration();

    boolean isFinished();

    boolean isExecuted();

    boolean isPlayerExecuted();

    void setExecuted(boolean executed);

    void addTime(long duration);

    void removeTime(long duration);

    boolean pause();

    boolean resume();

    void cancel();

    void finish();

    void tick();

    long getStartTime();

    long getEndTime();

    boolean isPaused();




    // those will be executed as soon as timer ends
    List<TimerCommand> getCommands();

    void addCommand(String command);

    void removeCommand(int index);



    // those are linked to player so only get executed when player comes online
    // will be executed for each player separately
    List<TimerCommand> getPlayerCommands();

    void addPlayerCommand(String command);

    void removePlayerCommand(int index);

    void linkPlayer(UUID playerId);

    List<LinkedPlayer> getLinkedPlayers();
}
