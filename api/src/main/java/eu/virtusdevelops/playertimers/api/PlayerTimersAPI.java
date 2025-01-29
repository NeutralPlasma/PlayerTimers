package eu.virtusdevelops.playertimers.api;

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
        return 2;
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

    /**
     * Unloads the PlayerTimersAPI, resetting the API state.
     *
     * This method sets the API's loaded state to false and clears the current implementation
     * reference. It is intended for internal use only and should not be invoked directly
     * under normal circumstances.
     *
     * This is typically used during the plugin's disable process to ensure the API is
     * properly unloaded and cleaned up.
     */
    @ApiStatus.Internal
    public static void unload(){
        isLoaded = false;
        implementation = null;
    }
}