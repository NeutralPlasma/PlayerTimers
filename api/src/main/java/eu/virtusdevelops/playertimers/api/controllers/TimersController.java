package eu.virtusdevelops.playertimers.api.controllers;

import eu.virtusdevelops.playertimers.api.timer.PlayerTimer;

import java.util.List;
import java.util.UUID;

public interface TimersController {

    /**
     * Retrieves a list of player timers associated with a specific player.
     *
     * @param player the UUID of the player whose timers are to be retrieved.
     * @return a list of PlayerTimer objects associated with the specified player, or an empty list if no timers are found.
     * @since 1
     */
    List<PlayerTimer> getPlayerTimers(UUID player);

    /**
     * Retrieves a list of timers associated with the specified player that are awaiting execution.
     *
     * @param player the UUID of the player whose awaiting timers are to be retrieved.
     * @return a list of PlayerTimer objects that are pending execution for the specified player,
     *         or an empty list if no such timers are found.
     * @since 1
     */
    List<PlayerTimer> getAwaitingPlayerTimers(UUID player);

    /**
     * Retrieves a specific timer associated with a given player and timer name.
     *
     * @param player the UUID of the player whose timer is to be retrieved.
     * @param timerName the name of the timer to retrieve.
     * @return the PlayerTimer object associated with the specified player and timer name,
     *         or null if no such timer is found.
     * @since 1
     */
    PlayerTimer getTimer(UUID player, String timerName);

    /**
     * Creates a new timer for a specific player with the provided name, duration, and offline tick setting.
     *
     * @param player the UUID of the player for whom the timer will be created
     * @param timerName the name of the timer to be created
     * @param duration the duration of the timer in seconds
     * @param offlineTick whether the timer should tick while the player is offline
     * @return the created PlayerTimer object, or null if the timer could not be created
     * @since 1
     */
    PlayerTimer createTimer(UUID player, String timerName, long duration, boolean offlineTick);

    /**
     * Cancels the specified player timer, preventing it from completing or executing any associated commands.
     *
     * @param timer the PlayerTimer instance to be canceled
     * @return true if the timer was successfully canceled, false if the cancellation failed or the timer was not active
     * @since 1
     */
    boolean cancelTimer(PlayerTimer timer);

    /**
     * Stops the specified player timer, ensuring it halts its countdown and executes any associated commands.
     *
     * @param timer the PlayerTimer instance to be stopped
     * @return true if the timer was successfully stopped, false if the operation failed or the timer was not active
     * @since 1
     */
    boolean stopTimer(PlayerTimer timer);

    /**
     * Adds the specified duration to the given player timer.
     *
     * @param timer the PlayerTimer instance to which the duration will be added
     * @param duration the additional time, in seconds, to be added to the timer
     * @return the updated PlayerTimer instance with the added duration
     * @since 1
     */
    PlayerTimer addTime(PlayerTimer timer, long duration);

    /**
     * Removes a specified duration from the given player timer.
     *
     * @param timer the PlayerTimer instance from which the duration will be subtracted
     * @param duration the amount of time, in seconds, to be removed from the timer's duration
     * @return the updated PlayerTimer instance with the reduced duration
     * @since 1
     */
    PlayerTimer removeTime(PlayerTimer timer, long duration);

    /**
     * Adds a list of commands to the specified PlayerTimer.
     *
     * @param timer the PlayerTimer instance to which the commands will be added
     * @param commands a list of commands to add to the specified timer
     * @return the updated PlayerTimer instance with the added commands
     * @since 1
     */
    PlayerTimer addCommands(PlayerTimer timer, List<String> commands);

    /**
     * Adds a single command to the specified PlayerTimer. The command will be executed
     * when the timer finishes.
     *
     * @param timer the PlayerTimer instance to which the command will be added
     * @param command the command to add to the specified timer
     * @return the updated PlayerTimer instance with the added command
     * @since 1
     */
    PlayerTimer addCommand(PlayerTimer timer, String command);

    /**
     * Pauses the specified player timer, preventing it from counting down
     * until it is resumed. The timer's remaining duration will be preserved.
     *
     * @param timer the PlayerTimer instance to be paused
     * @return true if the timer was successfully paused, false if the operation
     *         failed or the timer was already paused
     */
    boolean pauseTimer(PlayerTimer timer);

    /**
     * Resumes a previously paused player timer, allowing it to continue counting down
     * from where it was paused.
     *
     * @param timer the PlayerTimer instance to be resumed
     * @return true if the timer was successfully resumed, false if the operation failed or
     *         the timer was not paused
     */
    boolean resumeTimer(PlayerTimer timer);

}
