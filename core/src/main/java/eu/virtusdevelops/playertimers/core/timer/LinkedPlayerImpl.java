package eu.virtusdevelops.playertimers.core.timer;

import eu.virtusdevelops.playertimers.api.timer.LinkedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LinkedPlayerImpl implements LinkedPlayer {
    private final UUID id;
    private final UUID player_id;
    private boolean executed;

    public LinkedPlayerImpl(UUID id, UUID player_id) {
        this.id = id;
        this.player_id = player_id;
        this.executed = false;
    }

    public LinkedPlayerImpl(UUID id, UUID uuid, boolean executed) {
        this.id = id;
        this.player_id = uuid;
        this.executed = executed;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public UUID getPlayer_id() {
        return player_id;
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
        return Bukkit.getPlayer(player_id);
    }
}
