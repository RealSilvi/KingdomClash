package it.unibo.model.base.basedata;

import java.util.Collections;
import java.util.Set;

import it.unibo.kingdomclash.util.Pair;
import it.unibo.model.base.internal.BuildingBuilder.BuildingTypes;
import it.unibo.model.data.Resource;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * A simple data class that stores information about a building in the game
 */
public class Building implements Serializable {
    /**
     * Maximum level that the buildings can reach
     */
    public static final int MAXLEVEL = 3;
    /**
     * Max number of allowed buildings for the player
     */
    public static final int MAXBUILDINGS = 4;
    /**
     * Tax as a percentage that gets applied when reimboursing materials after demolition
     */
    public static final int REFUND_TAX_PERCENTAGE = 25;
    /**
     * Tax that gets applied to the cost of the building when upgrading
     */
    public static final int UPGRADE_TAX_PERCENTAGE = 15;
    /**
     * Increment that gets applied to the building per turn producion set
     */
    public static final int PRODUCTION_MULTIPLIER_PERCENTAGE = 0;
    /**
     * Decrement that gets applied to the production time
     */
    public static final int PRODUCTION_TIME_REDUCITON_PERCENTAGE = 0;

    /*All timings should be in milliseconds*/
    private BuildingTypes type;
    private int level;
    private long buildingTime;
    private long productionTime;
    private boolean beingBuilt;
    private int buildingProgress;
    private int productionProgress;
    private Pair<Float, Float> structurePos;
    private Set<Resource> productionAmount;
    private Set<Resource> buildingValue;

    //The high parameter count is necessary to set all of the properties of the class
    @SuppressWarnings("java:S107")
    public Building(BuildingTypes type, int level, long buildingTime, long productionTime,
                    boolean beingBuilt, int buildingProgress, int productionProgress, Point2D structurePos,
                    Set<Resource> productionAmount, Set<Resource> buildingValue) {
        this.type = type;
        this.level = level;
        this.buildingTime = buildingTime;
        this.productionTime = productionTime;
        this.beingBuilt = beingBuilt;
        this.buildingProgress = buildingProgress;
        this.productionProgress = productionProgress;
        this.structurePos = pointToFloatPair(structurePos);
        this.productionAmount = productionAmount;
        this.buildingValue = buildingValue;
    }

    /**
     * Returns the type of building, thread safe
     *
     * @return the type of building
     * @see {@link it.unibo.model.base.internal.BuildingBuilder.BuildingTypes}
     */
    public synchronized BuildingTypes getType() {
        return type;
    }

    /**
     * Sets the type of building, thread safe
     *
     * @see {@link it.unibo.model.base.internal.BuildingBuilder.BuildingTypes}
     */
    public synchronized void setType(BuildingTypes type) {
        this.type = type;
    }

    /**
     * Returns the level of the building, thread safe
     *
     * @return the building's current level
     */
    public synchronized int getLevel() {
        return level;
    }

    /**
     * Sets the level of the building, thread safe
     */
    public synchronized void setLevel(int level) {
        this.level = level;
    }

    /**
     * Returns the time to build the structure
     *
     * @return building time in milliseconds
     */
    public synchronized long getBuildingTime() {
        return buildingTime;
    }

    /**
     * Sets the time to build the structure, thread safe
     */
    public synchronized void setBuildingTime(long buildingTime) {
        this.buildingTime = buildingTime;
    }

    /**
     * Checks if a structure is currently being built
     *
     * @return true if it is being built
     */
    public synchronized boolean isBeingBuilt() {
        return beingBuilt;
    }

    /**
     * Sets if a building is currently being built
     *
     * @param beingBuilt true if it is being built
     */
    public synchronized void setBeingBuilt(boolean beingBuilt) {
        this.beingBuilt = beingBuilt;
    }

    /**
     * Gets the building progress as a percentage
     *
     * @return an integer that represents a percentage
     */
    public synchronized int getBuildingProgress() {
        return buildingProgress;
    }

    /**
     * Sets the building progress as a percentage
     *
     * @param buildingProgress an integer representing a percentage
     */
    public synchronized void setBuildingProgress(int buildingProgress) {
        this.buildingProgress = buildingProgress;
    }

    /**
     * Returns the progress for creating a set of resources
     *
     * @return an integer that represents the progress as a percentage
     */
    public synchronized int getProductionProgress() {
        return productionProgress;
    }

    /**
     * Set the progress for creating a set of resources
     *
     * @param productionProgress an integer that represents the progress as a percentage
     */
    public synchronized void setProductionProgress(int productionProgress) {
        this.productionProgress = productionProgress;
    }

    /**
     * Gets the current position of the building
     *
     * @return a Point2D that represents the building's current location
     */
    public synchronized Point2D getStructurePos() {
        return new Point2D.Float(structurePos.getFirst(), structurePos.getSecond());
    }

    /**
     * Sets the position of the building
     *
     * @param structurePos a Point2D that represents the building's location
     */
    public synchronized void setStructurePos(Point2D structurePos) {
        this.structurePos = pointToFloatPair(structurePos);
    }

    /**
     * Gets an unmodifiable set of resources produced every production cycle
     *
     * @return a set of resources produced
     */
    public synchronized Set<Resource> getProductionAmount() {
        return Collections.unmodifiableSet(productionAmount);
    }

    /**
     * Sets a set of resources that the building will produce every production cycle
     *
     * @param productionAmount the resources that will be produced
     */
    public synchronized void setProductionAmount(Set<Resource> productionAmount) {
        this.productionAmount = productionAmount;
    }

    /**
     * Returns an unmodifiable set containing the value of this building
     *
     * @return an unmodifiable set with the values of this building
     */
    public synchronized Set<Resource> getBuildingValue() {
        return Collections.unmodifiableSet(buildingValue);
    }

    /**
     * Sets the value of the current building
     *
     * @param buildingValue the value of this building
     */
    public synchronized void setBuildingValue(Set<Resource> buildingValue) {
        this.buildingValue = buildingValue;
    }

    /**
     * Returns in milliseconds the time that it takes to produce a set of resources
     *
     * @return time in milliseconds
     */
    public long getProductionTime() {
        return productionTime;
    }

    /**
     * Sets in milliseconds the time that it takes to produce a set of resources
     *
     * @param productionTime time in milliseconds
     */
    public void setProductionTime(long productionTime) {
        this.productionTime = productionTime;
    }

    //Double is boxed for a safe conversion
    @SuppressWarnings("java:S2153")
    private Pair<Float, Float> pointToFloatPair(Point2D positionToConvert) {
        return new Pair<>(Double.valueOf(positionToConvert.getX()).floatValue(),
            Double.valueOf(positionToConvert.getY()).floatValue());
    }
}