package it.unibo.model.base;

import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import it.unibo.model.base.api.BuildingObserver;
import it.unibo.model.base.basedata.Building;
import it.unibo.model.base.exceptions.BuildingMaxedOutException;
import it.unibo.model.base.exceptions.InvalidBuildingPlacementException;
import it.unibo.model.base.exceptions.InvalidStructureReferenceException;
import it.unibo.model.base.exceptions.NotEnoughResourceException;
import it.unibo.model.base.internal.BuildingBuilder;
import it.unibo.model.base.internal.BuildingBuilder.BuildingTypes;
import it.unibo.model.base.internal.BuildingBuilderImpl;
import it.unibo.model.data.GameData;
import it.unibo.model.data.Resource;
import it.unibo.model.data.Resource.ResourceType;

public class BaseModelImpl implements BaseModel {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private GameData gameData;
    private ThreadManager threadManager;

    private List<BuildingObserver> buildingStateChangedObservers;
    private List<BuildingObserver> buildingProductionObservers;

    public BaseModelImpl(@NotNull GameData gameData) {
        this();
        Objects.requireNonNull(gameData);
        this.gameData = gameData;
        logger.info("Base model succesfully initialized");
    }
    private BaseModelImpl(){
        this.threadManager = new ThreadManagerImpl(this, gameData.getBuildings());
        this.buildingStateChangedObservers = new ArrayList<>();
        this.buildingProductionObservers = new ArrayList<>();
    }

    @Override
    public UUID buildStructure(final Point2D position, final BuildingTypes type, final int startingLevel, final boolean cheatMode)
            throws NotEnoughResourceException, InvalidBuildingPlacementException {
        BuildingBuilder buildingBuilder = new BuildingBuilderImpl();
        Building newStructure = buildingBuilder.makeStandardBuilding(type, position, startingLevel);
        gameData.setResources(subtractResources(gameData.getResources(),
            BaseModel
            .applyMultiplierToResources(newStructure.getType().getCost(),
                startingLevel)));
        UUID newStructureId = generateBuildingId();
        gameData.getBuildings().put(newStructureId, newStructure);
        return newStructureId;
    }

    @Override
    public UUID buildStructure(final Point2D position, final BuildingTypes type, final int startingLevel)
            throws NotEnoughResourceException, InvalidBuildingPlacementException {
        return buildStructure(position, type, startingLevel, false);
    }

    @Override
    public UUID buildStructure(Point2D position, BuildingTypes type)
            throws NotEnoughResourceException, InvalidBuildingPlacementException {
        return buildStructure(position, type, 0, false);
    }
    @Override
    public void upgradeStructure(UUID structureId, boolean cheatMode)
            throws NotEnoughResourceException, BuildingMaxedOutException, InvalidStructureReferenceException {
        Building selectedBuilding = checkAndGetBuilding(structureId);
        if (selectedBuilding.getLevel() >= Building.MAXLEVEL) {
            throw new BuildingMaxedOutException();
        }
        gameData.setResources(subtractResources(gameData.getResources(), BaseModel.applyMultiplierToResources(selectedBuilding.getType().getCost(), selectedBuilding.getLevel()+1)));
        threadManager.addBuilding(structureId);
    }

    @Override
    public void upgradeStructure(UUID structureId)
            throws NotEnoughResourceException, BuildingMaxedOutException, InvalidStructureReferenceException {
        upgradeStructure(structureId, false);
    }

    @Override
    public Set<Resource> demolishStructure(UUID structureId) throws InvalidStructureReferenceException {
        Building selectedBuilding = checkAndGetBuilding(structureId);
        Set<Resource> refund = BaseModel.applyMultiplierToResources(selectedBuilding.getType().getCost(), selectedBuilding.getLevel());
        for (Resource resource : refund) {
            resource.setAmount(resource.getAmount()%Building.REFUND_TAX_PERCENTAGE);
        }
        return refund;
    }

    @Override
    public void relocateStructure(Point2D position, UUID structureId)
            throws InvalidBuildingPlacementException, InvalidStructureReferenceException {
        Building selectedBuilding = checkAndGetBuilding(structureId);
        Set<UUID> keys = gameData.getBuildings().keySet();
        for (UUID key : keys) {
            if (gameData.getBuildings().get(key).getStructurePos().equals(position) && !structureId.equals(key)) {
                throw new InvalidBuildingPlacementException();
            }
        }
        selectedBuilding.setStructurePos(position);
    }

    @Override
    public Path getStructureTexture(UUID structureId) throws InvalidStructureReferenceException {
        throw new UnsupportedOperationException("Unimplemented method 'getStructureTexture'");
    }

    @Override
    public int getBuildingProgress(UUID structureId) throws InvalidStructureReferenceException {
        Building selectedBuilding = checkAndGetBuilding(structureId);
        return selectedBuilding.getBuildingProgress();
    }

    @Override
    public Set<Resource> getBuildingProduction(UUID structureId) throws InvalidStructureReferenceException {
        Building selectedBuilding = checkAndGetBuilding(structureId);
        return Collections.unmodifiableSet(selectedBuilding.getProductionAmount());
    }

    @Override
    public boolean isBuildingBeingBuilt(UUID structureId) throws InvalidStructureReferenceException {
        Building selectedBuilding = checkAndGetBuilding(structureId);
        return selectedBuilding.isBeingBuilt();
    }

    @Override
    public Set<UUID> getBuildingIds() {
        return Collections.unmodifiableSet(gameData.getBuildings().keySet());
    }

    @Override
    public int getResourceCount(ResourceType type) {
        Optional<Resource> resourceCounter = gameData
            .getResources()
            .stream().filter(x->x.getResource().equals(type)).findFirst();
        if (resourceCounter.isEmpty()) {
            return Integer.valueOf(0);
        }
        return Integer.valueOf(resourceCounter.get().getAmount());
    }

    @Override
    public Set<Resource> getResourceCount() {
        return Collections.unmodifiableSet(gameData.getResources());
    }

    @Override
    public void addBuildingStateChangedObserver(BuildingObserver observer) {
        this.buildingStateChangedObservers.add(observer);
    }

    @Override
    public void removeBuildingStateChangedObserver(BuildingObserver observer) {
        this.buildingProductionObservers.remove(observer);
    }

    @Override
    public void addBuildingProductionObserver(BuildingObserver observer) {
        this.buildingProductionObservers.add(observer);
    }

    @Override
    public void removeBuildingProductionObserver(BuildingObserver observer) {
        this.buildingProductionObservers.remove(observer);
    }

    @Override
    public void setClockTicking(boolean ticktime) {
        if (ticktime) {
            this.threadManager.pauseThreads();
        } else {
            this.threadManager.startThreads();
        }
    }

    @Override
    public boolean isClockTicking() {
        return this.threadManager.areThreadsRunning();
    }

    @Override
    public GameData obtainGameData() {
        return this.gameData;
    }

    @Override
    public synchronized void applyResources(Set<Resource> resource) throws NotEnoughResourceException {
        applyResources(resource, OperationType.ADDITION);
    }
    @Override
    public synchronized void applyResources(Set<Resource> resource, OperationType operation) throws NotEnoughResourceException {
        switch(operation) {
            case SUBTRACTION:
                this.gameData.setResources(subtractResources(this.gameData.getResources(), resource));
            break;
            case ADDITION:
                this.gameData.setResources(addResources(this.gameData.getResources(), resource));
            break;
        }
    }

    @Override
    public Map<UUID, Building> getBuildingMap() {
        Map<UUID, Building> unmodMap = gameData.getBuildings();
        return Collections.unmodifiableMap(unmodMap);
    }

    @Override
    public void notifyBuildingStateChangedObservers(UUID building) {
        this.buildingStateChangedObservers.forEach(buildingStateObserver->buildingStateObserver.update(building));
    }
    @Override
    public void notifyBuildingProductionObservers(UUID building) {
        this.buildingProductionObservers.forEach(productionObserver->productionObserver.update(building));
    }

    /**
     * Executes an addition between resources of the same type inside the set
     * this operation is unsafe because it doesn't check for negative results
     * @param resourceStorage the resource set that is going to be affected
     * @param resourceCost the second set that contains resources that will
     * be used or added
     * @return a set with the result of the operation
     */
    private Set<Resource> unsafeOperation(final Set<Resource> resourceStorage, Set<Resource> resourceCost) {
        Set<Resource> storageResult = new HashSet<>();
        Iterator<Resource> storageIterator = resourceStorage.iterator();
        Iterator<Resource> costIterator = resourceCost.iterator();
        while (storageIterator.hasNext()) {
            Resource currentStorageResource = storageIterator.next();
            while (costIterator.hasNext()) {
                Resource currentCostResource = costIterator.next();
                if (currentStorageResource.equals(currentCostResource)) {
                    storageResult.add(new Resource(currentStorageResource.getResource(),
                    currentStorageResource.getAmount()+currentCostResource.getAmount()));
                }
            }
        }
        return storageResult;
    }
    /**
     * Checks for negative values in the resources inside the resources in the set
     * @param resourcesToCheck the set that needs to be checked
     * @return the resources with negative values within the set
     */
    private Set<Resource> filterNegativeValues(final Set<Resource> resourcesToCheck) {
        Set<Resource> missingResources = new HashSet<>();
        resourcesToCheck.forEach(x->{
            if (x.getAmount()<0) {
                missingResources.add(x);
            }
        });
        return missingResources;
    }
    /**
     * Inverts the sign of values inside a set of resources
     * @param resourceToNegate the set of resources to negate
     * @return a negated set
     */
    private Set<Resource> negateResources(Set<Resource> resourceToNegate) {
        Set<Resource> negatedResources = new HashSet<>(resourceToNegate);
        negatedResources.stream().forEach(x->x.setAmount(-x.getAmount()));
        return negatedResources;
    }

    private Set<Resource> subtractResources(Set<Resource> resourceStorage, Set<Resource> resourceCost) throws NotEnoughResourceException {
        return addResources(resourceStorage, negateResources(resourceCost));
    }

    private Set<Resource> addResources(Set<Resource> resourceStorage, Set<Resource> resourcesAdded)  throws NotEnoughResourceException {
        Set<Resource> updatedList = unsafeOperation(resourceStorage, resourcesAdded);
        Set<Resource> missingResources = filterNegativeValues(updatedList);
        if (missingResources.isEmpty()) {
            return updatedList;
        }
        throw new NotEnoughResourceException(missingResources);
    }
    /**
     * Checks if the referenced UUID corresponds to a building
     * and returns a building
     * @param structureId the id to check for
     * @return the building with the corresponding UUID
     * @throws InvalidStructureReferenceException thrown when the given UUID
     * does not correspond to an existing building
     */
    private Building checkAndGetBuilding(UUID structureId) throws InvalidStructureReferenceException {
        Building selectedBuilding = gameData.getBuildings().get(structureId);
        if (selectedBuilding == null) {
            throw new InvalidStructureReferenceException(structureId);
        }
        return selectedBuilding;
    }
    /**
     * Makes sure that a non-conflicting UUID is generated for a building
     * @return a freshly generated UUID
     */
    private UUID generateBuildingId() {
        return Stream.generate(UUID::randomUUID)
        .filter(x->!gameData.getBuildings().containsKey(x)).findFirst()
        .orElseThrow();
    }
}