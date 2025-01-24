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
     * @since 1
     */
    UUID getPlayerID();

    /**
     * Retrieves the unique identifier (UUID) for this entity.
     *
     * @return the UUID associated with this entity.
     * @since 1
     */
    UUID getId();

    /**
     * Retrieves the name of the timer.
     *
     * @return the name of the timer as a String.
     * @since 1
     */
    String getName();

    /**
     * Retrieves the remaining duration of the timer in seconds.
     *
     * @return the remaining duration of the timer as a long value, representing the number of seconds left.
     * @since 1
     */
    long getDuration();

    /**
     * Determines whether the timer executes ticks even when the player is offline.
     *
     * @return true if the timer processes ticks while the associated player is offline; false otherwise.
     * @since 1
     */
    boolean isOfflineTick();

    /**
     * Checks if the timer has completed its duration.
     *
     * @return true if the timer's remaining duration is less than or equal to zero; false otherwise.
     * @since 1
     */
    boolean isFinished();

    /**
     * Indicates whether the timer has been executed.
     * Timer only gets executed after it has finished its countdown, see {@link #isFinished()}
     *
     * @return true if the timer has been executed; false otherwise.
     * @since 1
     */
    boolean isExecuted();

    /**
     * Retrieves the Player instance associated with the timer. If the Player instance
     * is not already initialized, it will be fetched using the player's UUID.
     *
     * @return the Player instance associated with this timer, or null if the player
     *         is not online or cannot be found.
     * @since 1
     */
    Player getPlayer();

    /**
     * Retrieves the offline representation of the player associated with this timer.
     *
     * @return the OfflinePlayer instance representing the associated player.
     * @since 1
     */
    OfflinePlayer getOfflinePlayer();


    /**
     * Retrieves the list of commands associated with the timer. These commands
     * are typically executed when the timer finishes.
     *
     * @return a list of strings, each representing a command associated with the timer.
     * @since 1
     */
    List<String> getCommands();

    /**
     * Retrieves the start time of the timer in milliseconds.
     *
     * @return the start time of the timer as a long value, representing the
     *         time in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     * @since 1
     */
    long getStartTime();

    /**
     * Retrieves the end time of the timer in milliseconds.
     *
     * @return the end time of the timer as a long value, representing the
     *         time in milliseconds since the epoch (January 1, 1970, 00:00:00 GMT).
     * @since 1
     */
    long getEndTime();

    /**
     * Checks if the timer is currently paused.
     *
     * @return true if the timer is in a paused state; false otherwise.
     */
    boolean isPaused();

    /**
     * Adds the specified duration to the timer's remaining time.
     *
     * @param duration the amount of time to be added, in seconds
     */
    void addTime(long duration);


    /**
     * Reduces the remaining duration of the timer by the specified amount.
     *
     * @param duration the amount of time to remove, in seconds
     */
    void removeTime(long duration);


    /**
     * Pauses the timer, freezing its current state and stopping the countdown.
     * If the timer is already paused, this method will not perform any additional actions.
     *
     * @return true if the timer was successfully paused; false if the timer was already paused.
     */
    boolean pause();


    /**
     * Resumes the timer, allowing it to continue its countdown if it was previously paused.
     *
     * @return true if the timer was successfully resumed; false if the timer was not paused.
     */
    boolean resume();
}
