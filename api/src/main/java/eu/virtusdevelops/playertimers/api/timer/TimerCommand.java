package eu.virtusdevelops.playertimers.api.timer;

import java.util.UUID;

public interface TimerCommand {

    UUID getId();
    String getCommand();
}
