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

    boolean isDependentExecuted();

    void addTime(long duration);

    void removeTime(long duration);

    void pause();

    void resume();

    void cancel();

    void finish();

    void tick();



    // those will be executed as soon as timer ends
    List<String> getIndependentCommands();

    void addIndependentCommand(String command);

    void removeIndependentCommand(int index);



    // those are linked to player so only get executed when player comes online
    // will be executed for each player separately
    List<String> getCommands();

    void addCommand(String command);

    void removeCommand(int index);

    void linkPlayer(UUID playerId);

    List<LinkedPlayer> getLinkedPlayers();
}
