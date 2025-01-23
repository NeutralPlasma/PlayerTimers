package eu.virtusdevelops.playertimers.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

public final class PlayerTimersAPI {

    private static PlayerTimers implementation = null;
    private static boolean isLoaded = false;

    private PlayerTimersAPI() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves the version of the API.
     *
     * @return The version as an integer.
     */
    public static int getVersion() {
        return 1;
    }

    /**
     * Provides an instance of the PlayerTimersAPI if it is loaded.
     *
     * @return The instance of PlayerTimersAPI.
     * @throws NullPointerException if the API is not loaded.
     * @since 1
     */
    public static PlayerTimers get() {
        if (isLoaded) {
            return implementation;
        }
        throw new NullPointerException("PlayerTimersAPI is not loaded!");
    }

    /**
     * Checks whether the PlayerTimersAPI is loaded.
     *
     * @return true if the API is loaded, false otherwise.
     * @since 1
     */
    public static boolean isLoaded() {
        return isLoaded;
    }


    /**
     * This is an internal method. Do not use it.
     * Loads the specified PlayerTimersAPI instance to enable the API.
     *
     * @param api the instance of the api
     * @throws IllegalStateException If the API is already loaded.
     */
    @ApiStatus.Internal
    public static void load(PlayerTimers api) {
        if (isLoaded) {
            throw new IllegalStateException("PlayerTimersAPI is already loaded!");
        }
        implementation = api;
        isLoaded = true;
    }
}