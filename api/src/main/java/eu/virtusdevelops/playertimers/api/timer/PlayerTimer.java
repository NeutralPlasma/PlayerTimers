package eu.virtusdevelops.playertimers.api.timer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerTimer {

    /**
     * Retrieves the unique identifier (UUID) of the associated player.
     *
     * @return the UUID of the player associated with this timer.
     */
    UUID getPlayerID();

    /**
     * Retrieves the unique identifier (UUID) for this entity.
     *
     * @return the UUID associated with this entity.
     */
    UUID getId();

    /**
     * Retrieves the name of the timer.
     *
     * @return the name of the timer as a String.
     */
    String getName();

    /**
     * Retrieves the remaining duration of the timer in seconds.
     *
     * @return the remaining duration of the timer as a long value, representing the number of seconds left.
     */
    long getDuration();

    /**
     * Determines whether the timer executes ticks even when the player is offline.
     *
     * @return true if the timer processes ticks while the associated player is offline; false otherwise.
     */
    boolean isOfflineTick();

    /**
     * Checks if the timer has completed its duration.
     *
     * @return true if the timer's remaining duration is less than or equal to zero; false otherwise.
     */
    boolean isFinished();

    /**
     * Indicates whether the timer has been executed.
     * Timer only gets executed after it has finished its countdown, see {@link #isFinished()}
     *
     * @return true if the timer has been executed; false otherwise.
     */
    boolean isExecuted();

    /**
     * Retrieves the Player instance associated with the timer. If the Player instance
     * is not already initialized, it will be fetched using the player's UUID.
     *
     * @return the Player instance associated with this timer, or null if the player
     *         is not online or cannot be found.
     */
    Player getPlayer();

    /**
     * Retrieves the offline representation of the player associated with this timer.
     *
     * @return the OfflinePlayer instance representing the associated player.
     */
    OfflinePlayer getOfflinePlayer();


    /**
     * Retrieves the list of commands associated with the timer. These commands
     * are typically executed when the timer finishes.
     *
     * @return a list of strings, each representing a command associated with the timer.
     */
    List<String> getCommands();

    /**
     * Retrieves the start time of the timer in milliseconds.
     *
     * @return the start time of the timer as a long value, representing the
     *         time in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     */
    long getStartTime();

    /**
     * Retrieves the end time of the timer in milliseconds.
     *
     * @return the end time of the timer as a long value, representing the
     *         time in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     */
    long getEndTime();
}
