package eu.virtusdevelops.playertimers.core.timer;

import eu.virtusdevelops.playertimers.api.timer.TimerCommand;

import java.util.UUID;

public class TimerCommandImpl implements TimerCommand {

    private final UUID id;
    private final String command;

    public TimerCommandImpl(UUID id, String command) {
        this.id = id;
        this.command = command;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getCommand() {
        return command;
    }
}
