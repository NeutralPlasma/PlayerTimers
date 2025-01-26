package eu.virtusdevelops.playertimers.api.controllers;

import eu.virtusdevelops.playertimers.api.timer.GlobalTimer;

import java.util.List;
import java.util.UUID;

public interface GlobalTimersController {

    List<GlobalTimer> getActiveTimers();

    List<GlobalTimer> getAwaitingExecutionTimers();

    GlobalTimer getTimer(String name);

    GlobalTimer getTimer(UUID uuid);

    GlobalTimer createTimer(String name, long duration);
}