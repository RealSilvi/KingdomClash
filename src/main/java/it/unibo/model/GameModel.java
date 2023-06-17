package it.unibo.model;

import it.unibo.controller.LoadConfiguration;
import it.unibo.kingdomclash.config.GameConfiguration;
import it.unibo.model.data.GameData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.util.Optional;
import java.util.logging.Logger;

public final class GameModel {

    private GameData gameData;

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final File saveDataLocation;

    private final GameConfiguration configuration;

    /**
     * Intended behaviour of File.mkdirs();
     */
    public GameModel() {
        LoadConfiguration loadConfiguration= new LoadConfiguration();
        this.saveDataLocation = new File(LoadConfiguration.getAppData() + File.separator + "game.dat");
        this.configuration=loadConfiguration.getConfiguration();
    }

    public void resetSaved(){
        this.saveDataLocation.delete();
        this.gameData=new GameData();
    }

    public boolean load() {
        final Optional<GameData> gameDataOptional = deserializeGameData(this.configuration);
        if (gameDataOptional.isPresent()) {
            this.gameData = new GameData(gameDataOptional.get(), this.configuration);
            return true;
        } else {
            this.gameData = new GameData(this.configuration);
            logger.warning("Generating new game data!");
            return false;
        }
    }

    public boolean serializeGameData() {
        try (ObjectOutputStream gameDataOutputStream = new ObjectOutputStream(new FileOutputStream(saveDataLocation))) {
            gameDataOutputStream.writeObject(gameData);
        } catch (IOException exc) {
            logger.severe("Could not write game data to folder!");
            return false;
        }
        return true;
    }

    /**
     * Creates a GameData object by reading a compatible
     * serialized object on disk.
     *
     * @param configuration the configuration to apply to gameData
     * @return if readable, a GameData object
     */
    private Optional<GameData> deserializeGameData(final GameConfiguration configuration) {
        if (this.saveDataLocation.exists()) {
            try (ObjectInputStream gameDataInputStream =
                         new ObjectInputStream(
                                 new FileInputStream(this.saveDataLocation))) {

                final Object data = gameDataInputStream.readObject();
                if (data instanceof GameData retrievedGameData) {
                    return Optional.of(new GameData(retrievedGameData, configuration));
                } else {
                    throw new IllegalClassFormatException();
                }
            } catch (FileNotFoundException | ClassNotFoundException exc) {
                logger.warning("Cannot read game data!" + exc);
            } catch (IOException exc) {
                logger.severe("IOException occourred while loading GameData!" + exc);
            } catch (IllegalArgumentException | SecurityException
                     | IllegalClassFormatException exc) {
                logger.severe("Data class is incompatible or a different version!" + exc);
            }
        }
        return Optional.empty();
    }


    public GameData getGameData() {
        return this.gameData;
    }

    public int getCurrentLevel() {
        return this.gameData.getCurrentLevel();
    }

    public boolean isSaved(){
        return saveDataLocation.exists();
    }

    public String getPlayerName() {
        return this.gameData.getPlayerName();
    }

    public void setPlayerName(final String name) {
        this.gameData.setPlayerName(name);
    }
}
