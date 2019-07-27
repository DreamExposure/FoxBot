package org.dreamexposure.foxbot.service;

import org.dreamexposure.foxbot.modules.misc.PresenceUpdater;

import java.util.ArrayList;
import java.util.Timer;

public class TimerManager {
    private static TimerManager instance;

    private final ArrayList<Timer> timers = new ArrayList<>();

    private TimerManager() {
    } //Prevent initialization

    /**
     * Gets the instance of the TimeManager that is loaded.
     *
     * @return The instance of the TimeManager.
     */
    public static TimerManager getManager() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    /**
     * Initializes the TimeManager and schedules the appropriate Timers.
     */
    public void init() {
        Timer timer = new Timer(true);
        timer.schedule(new PresenceUpdater(), 10 * 1000, 10 * 1000);

        timers.add(timer);
    }

    /**
     * Gracefully shuts down the TimeManager and exits all timer threads preventing errors.
     */
    void shutdown() {
        for (Timer t : timers) {
            t.cancel();
        }
    }
}
