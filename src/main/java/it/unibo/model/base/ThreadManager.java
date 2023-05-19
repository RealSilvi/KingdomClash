package it.unibo.model.base;

import java.util.Set;
import java.util.UUID;

public interface ThreadManager {
    /**
     * Time in milliseconds that the thread will wait for before doing another cycle
     */
    public static final int REFRESH_RATE_MS = 60;
    /**
     * A simple enum to select only on a kind of thread
     */
    public enum ThreadSelector {
        /**
         * Selects threads dedicated to building buildings
         */
        CONSTRUCTION,
        /**
         * Selects threads dedicated to generate resources for buildings
         */
        PRODUCTION
    }
    /**
     * Starts the threads dedicated to a kind of resource
     * @param threadType the type of threads that needs to be started
     */
    public void startThreads(ThreadSelector threadType);
    /**
     * Starts all threads
     */
    public void startThreads();
        /**
     * Pauses the threads dedicated to a kind of resource
     * @param threadType the type of threads that needs to be paused
     */
    public void pauseThreads(ThreadSelector threadType);
    /**
     * Pauses all threads
     */
    public void pauseThreads();

    /**
     * Checks if the given thread types are currently running
     * @param threadType the type of thread that needs to be checked
     * @return true if the thread is running
     */
    public boolean areThreadsRunning(ThreadSelector threadType);
    /**
     * Checks if all thread types are currently running
     * @return true if ALL threads are running, false if one or more are paused
     */
    public boolean areThreadsRunning();

    /**
     * Registers a map of buildings to keep track of time
     * @param buildingToAdd the map of buildings to keep track of
     */
    public void addBuilding(UUID buildingToAdd);
    /**
     * Unre a map of buildings to keep track of time
     * @param buildingToRemove the map of buildings to keep track of
     */
    public void removeBuilding(UUID buildingToRemove);
    /**
     * Unregisters a set of buildings with the corresponding ID
     * to keep track of time
     * @param buildingMap the id set of buildings to unregister
     */
    public void removeBuildings(Set<UUID> buildingMap);
    /**
     * Removes all tasks from buildings
     */
    public void clearBuildings();
}