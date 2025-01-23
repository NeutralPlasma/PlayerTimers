package eu.virtusdevelops.playertimers.api;

import eu.virtusdevelops.playertimers.api.controllers.TimersController;

public interface PlayerTimers {

    /**
     * Retrieves the TimersController instance associated with this PlayerTimers instance.
     *
     * @return the TimersController that provides methods to manage player timers.
     * @since 1
     */
    TimersController getTimersController();

}
