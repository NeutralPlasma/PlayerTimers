package eu.virtusdevelops.playertimers.api.timer;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface LinkedPlayer {
    UUID getId();
    UUID getPlayer_id();
    boolean isExecuted();
    void execute();
    Player getPlayer();
}
