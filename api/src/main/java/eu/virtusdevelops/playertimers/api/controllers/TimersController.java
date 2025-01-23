package eu.virtusdevelops.playertimers.api.controllers;

import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;

import java.util.List;
import java.util.UUID;

public interface TimersController {

    List<PlayerTimer> getPlayerTimers(UUID player);

    List<PlayerTimer> getAwaitingPlayerTimers(UUID player);

    PlayerTimer getTimer(UUID player, String timerName);

    PlayerTimer createTimer(UUID player, String timerName, long duration, boolean offlineTick);

    boolean cancelTimer(PlayerTimer timer);

    boolean stopTimer(PlayerTimer timer);

    PlayerTimer addTime(PlayerTimer timer, long duration);

    PlayerTimer removeTime(PlayerTimer timer, long duration);

    PlayerTimer addCommands(PlayerTimer timer, List<String> commands);

    PlayerTimer addCommand(PlayerTimer timer, String command);

}
