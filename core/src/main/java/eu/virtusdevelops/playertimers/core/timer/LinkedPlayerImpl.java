package eu.virtusdevelops.playertimers.core.timer;

import eu.virtusdevelops.playertimers.api.timer.LinkedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LinkedPlayerImpl implements LinkedPlayer {
    private final UUID uuid;
    private boolean executed;

    public LinkedPlayerImpl(UUID uuid) {
        this.uuid = uuid;
        this.executed = false;
    }

    public LinkedPlayerImpl(UUID uuid, boolean executed) {
        this.uuid = uuid;
        this.executed = executed;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void execute() {
        this.executed = true;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
