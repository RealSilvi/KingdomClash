package it.unibo.model.base;

import java.awt.geom.Point2D;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import it.unibo.model.base.api.BuildingObserver;
import it.unibo.model.base.exceptions.BuildingMaxedOutException;
import it.unibo.model.base.exceptions.InvalidBuildingPlacementException;
import it.unibo.model.base.exceptions.InvalidStructureReferenceException;
import it.unibo.model.base.exceptions.MaxBuildingLimitReachedException;
import it.unibo.model.base.exceptions.NotEnoughResourceException;
import it.unibo.model.base.internal.BuildingBuilder.BuildingTypes;
import it.unibo.model.data.GameData;
import it.unibo.model.data.Resource;
import it.unibo.model.data.Resource.ResourceType;

public class ThreadManagerImplTest {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private GameData gameData;
    private BaseModel baseModel;
    private void initModel(){
        this.gameData = new GameData();
        this.baseModel = new BaseModelImpl(this.gameData);
    }
    @Test
    public void testBuildingAndProductionCycle() throws NotEnoughResourceException, InvalidBuildingPlacementException, BuildingMaxedOutException, InvalidStructureReferenceException, MaxBuildingLimitReachedException {
        initModel();
        Object lock = new Object();
        if (gameData.getResources().add(new Resource(ResourceType.WHEAT, 30)) == false) {
            gameData.getResources().remove(new Resource(ResourceType.WHEAT, 30));
            gameData.getResources().add(new Resource(ResourceType.WHEAT, 30));
        }
        if (gameData.getResources().add(new Resource(ResourceType.WOOD, 50)) == false) {
            gameData.getResources().remove(new Resource(ResourceType.WOOD, 50));
            gameData.getResources().add(new Resource(ResourceType.WOOD, 50));
        }
        UUID builtStructureId = baseModel.buildStructure(new Point2D.Float(0.0f, 0.0f), BuildingTypes.FARM);
        long buildingTime = baseModel.getBuildingMap().get(builtStructureId).getBuildingTime();
        baseModel.addBuildingStateChangedObserver(new BuildingObserver() {
            @Override
            public void update(UUID buildingId) {
                if (gameData.getBuildings().get(buildingId).getLevel() != 1) {
                    logger.log(Level.INFO, "Checking building progress {0}%", gameData.getBuildings().get(buildingId).getBuildingProgress());
                    return;
                }
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });
        if (gameData.getResources().add(new Resource(ResourceType.WHEAT, 34)) == false) {
            gameData.getResources().remove(new Resource(ResourceType.WHEAT, 34));
            gameData.getResources().add(new Resource(ResourceType.WHEAT, 34));
        }
        if (gameData.getResources().add(new Resource(ResourceType.WOOD, 57)) == false) {
            gameData.getResources().remove(new Resource(ResourceType.WOOD, 57));
            gameData.getResources().add(new Resource(ResourceType.WOOD, 57));
        }
        long startTime = System.currentTimeMillis();
        baseModel.upgradeStructure(builtStructureId);
        synchronized (lock) {
            try {
                lock.wait();
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                logger.info("Time passed: "+elapsedTime);
                //Tolerance of 500ms
                boolean timeElapsedCorrect = (elapsedTime < (buildingTime+500)) && (elapsedTime > (buildingTime-500));
                Assertions.assertEquals(1, baseModel.getBuildingMap().get(builtStructureId).getLevel());
                Assertions.assertTrue(timeElapsedCorrect);
            } catch (InterruptedException e) {}
        }
        baseModel.addBuildingProductionObserver(new BuildingObserver() {
            @Override
            public void update(UUID buildingId) {
                if (buildingId.equals(builtStructureId)) {
                    Assertions.assertEquals(gameData.getBuildings().get(builtStructureId)
                        .getProductionAmount(), gameData.getResources());
                }
            }
        });
    }
}