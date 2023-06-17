package it.unibo.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unibo.kingdomclash.config.GameConfiguration;
import it.unibo.model.data.GameData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.util.Optional;
import java.util.logging.Logger;

public final class LoadConfiguration {

    private GameConfiguration configuration;

    /**
     * Intended behaviour of File.mkdirs();
     */
    public LoadConfiguration() {

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        final String configDir = getAppData() + File.separator + "configuration.json";


        Logger logger = Logger.getLogger(this.getClass().getName());
        try (FileReader content = new FileReader(configDir)) {
            this.configuration = gson.fromJson(content, GameConfiguration.class);
        } catch (FileNotFoundException e) {
            this.configuration = new GameConfiguration();
            final File file = new File(configDir);
            file.getParentFile().mkdirs();

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(gson.toJson(this.configuration));
            } catch (IOException ex) {
                logger.severe("Configuration saving FAILURE");
                logger.severe(ex.getMessage());
            }
        } catch (IOException e) {
            this.configuration = new GameConfiguration();
            logger.severe("Configuration loading FAILURE");
            logger.severe(e.getMessage());
        }

    }

    /**
     * Detects the host's OS and returns a path to appdata folder.
     *
     * @return a path to the appdata folder
     */
    public static String getAppData() {
        final String osHome = System.getProperty("os.name").toLowerCase();
        String appData;

        if (osHome.contains("win")) {
            appData = System.getenv("APPDATA");
        } else if (osHome.contains("mac")) {
            appData = System.getProperty("user.home")
                    + File.separator + "Library"
                    + File.separator + "Application Support";
        } else {
            appData = System.getProperty("user.home")
                    + File.separator + ".local"
                    + File.separator + "share";
        }

        return appData + File.separator + "KingdomClash";
    }

    public GameConfiguration getConfiguration() {
        return this.configuration;
    }
}