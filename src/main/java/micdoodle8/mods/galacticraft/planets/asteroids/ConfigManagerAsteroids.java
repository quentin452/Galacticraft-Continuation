package micdoodle8.mods.galacticraft.planets.asteroids;

import java.io.*;
import micdoodle8.mods.galacticraft.planets.mars.*;
import java.util.*;
import org.apache.logging.log4j.*;
import cpw.mods.fml.common.*;
import net.minecraftforge.common.config.*;

public class ConfigManagerAsteroids
{
    public static boolean loaded;
    static Configuration config;
    public static int dimensionIDAsteroids;
    public static int idSchematicRocketT3;
    public static boolean disableGalacticraftHelium;
    public static int astroMinerMax;
    public static boolean disableIlmeniteGen;
    public static boolean disableIronGen;
    public static boolean disableAluminumGen;
    
    public ConfigManagerAsteroids(final File file) {
        if (!ConfigManagerAsteroids.loaded) {
            if (file.exists()) {
                ConfigManagerAsteroids.config = new Configuration(file);
                syncConfig(true, true);
                file.delete();
                ConfigManagerAsteroids.config = ConfigManagerMars.config;
            }
            else {
                ConfigManagerAsteroids.config = ConfigManagerMars.config;
                syncConfig(true, false);
            }
        }
    }
    
    public static void syncConfig(final boolean load, final boolean update) {
        try {
            if (!ConfigManagerAsteroids.config.isChild && update) {
                ConfigManagerAsteroids.config.load();
            }
            Property prop = ConfigManagerAsteroids.config.get("dimensions", "dimensionIDAsteroids", -30);
            prop.comment = "Dimension ID for Asteroids";
            prop.setLanguageKey("gc.configgui.dimensionIDAsteroids").setRequiresMcRestart(true);
            if (update) {
                final Property propCopy = ConfigManagerMars.config.get("dimensions", prop.getName(), prop.getInt(), prop.comment);
                propCopy.setLanguageKey(prop.getLanguageKey());
                propCopy.setRequiresMcRestart(prop.requiresMcRestart());
            }
            ConfigManagerAsteroids.dimensionIDAsteroids = prop.getInt();
            prop = ConfigManagerAsteroids.config.get("schematic", "idSchematicRocketT3", 4);
            prop.comment = "Schematic ID for Tier 3 Rocket, must be unique.";
            prop.setLanguageKey("gc.configgui.idSchematicRocketT3");
            if (update) {
                final Property propCopy = ConfigManagerMars.config.get("schematic", prop.getName(), prop.getInt(), prop.comment);
                propCopy.setLanguageKey(prop.getLanguageKey());
            }
            ConfigManagerAsteroids.idSchematicRocketT3 = prop.getInt(4);
            prop = ConfigManagerAsteroids.config.get("general", "disableGalacticraftHelium", false);
            prop.comment = "Option to disable Helium gas in Galacticraft (because it will be registered by another mod eg GregTech).";
            prop.setLanguageKey("gc.configgui.disableGalacticraftHelium");
            if (update) {
                final Property propCopy = ConfigManagerMars.config.get("general", prop.getName(), prop.getBoolean(), prop.comment);
                propCopy.setLanguageKey(prop.getLanguageKey());
            }
            ConfigManagerAsteroids.disableGalacticraftHelium = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            prop = ConfigManagerAsteroids.config.get("general", "maximumAstroMiners", 6);
            prop.comment = "Maximum number of Astro Miners each player is allowed to have active (default 4).";
            prop.setLanguageKey("gc.configgui.astroMinersMax");
            if (update) {
                final Property propCopy = ConfigManagerMars.config.get("general", prop.getName(), prop.getInt(), prop.comment);
                propCopy.setLanguageKey(prop.getLanguageKey());
            }
            ConfigManagerAsteroids.astroMinerMax = prop.getInt(6);
            ConfigManagerMars.propOrder.add(prop.getName());
            prop = ConfigManagerAsteroids.config.get(update ? "general" : "worldgen", "Disable Iron Ore Gen on Asteroids", false);
            prop.comment = "Disable Iron Ore Gen on Asteroids.";
            prop.setLanguageKey("gc.configgui.disableIronGenAsteroids");
            if (update) {
                final Property propCopy = ConfigManagerMars.config.get("worldgen", prop.getName(), prop.getBoolean(), prop.comment);
                propCopy.setLanguageKey(prop.getLanguageKey());
            }
            ConfigManagerAsteroids.disableIronGen = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            prop = ConfigManagerAsteroids.config.get(update ? "general" : "worldgen", "Disable Aluminum Ore Gen on Asteroids", false);
            prop.comment = "Disable Aluminum Ore Gen on Asteroids.";
            prop.setLanguageKey("gc.configgui.disableAluminumGenAsteroids");
            if (update) {
                final Property propCopy = ConfigManagerMars.config.get("worldgen", prop.getName(), prop.getBoolean(), prop.comment);
                propCopy.setLanguageKey(prop.getLanguageKey());
            }
            ConfigManagerAsteroids.disableAluminumGen = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            prop = ConfigManagerAsteroids.config.get(update ? "general" : "worldgen", "Disable Ilmenite Ore Gen on Asteroids", false);
            prop.comment = "Disable Ilmenite Ore Gen on Asteroids.";
            prop.setLanguageKey("gc.configgui.disableIlmeniteGenAsteroids");
            if (update) {
                final Property propCopy = ConfigManagerMars.config.get("worldgen", prop.getName(), prop.getBoolean(), prop.comment);
                propCopy.setLanguageKey(prop.getLanguageKey());
            }
            ConfigManagerAsteroids.disableIlmeniteGen = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            if (load) {
                ConfigManagerMars.config.setCategoryPropertyOrder("worldgen", (List)ConfigManagerMars.propOrder);
            }
            if (ConfigManagerMars.config.hasChanged()) {
                ConfigManagerMars.config.save();
            }
        }
        catch (Exception e) {
            FMLLog.log(Level.ERROR, (Throwable)e, "Galacticraft Asteroids (Planets) has a problem loading it's config", new Object[0]);
        }
    }
}
