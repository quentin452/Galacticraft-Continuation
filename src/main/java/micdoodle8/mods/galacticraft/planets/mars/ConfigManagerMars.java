package micdoodle8.mods.galacticraft.planets.mars;

import java.io.*;
import org.apache.logging.log4j.*;
import cpw.mods.fml.common.*;
import net.minecraftforge.common.config.*;
import java.util.*;

public class ConfigManagerMars
{
    public static boolean loaded;
    public static Configuration config;
    public static List<String> propOrder;
    public static int dimensionIDMars;
    public static int idSchematicRocketT2;
    public static int idSchematicCargoRocket;
    public static boolean launchControllerChunkLoad;
    public static boolean launchControllerAllDims;
    public static boolean disableDeshGen;
    public static boolean disableTinGen;
    public static boolean disableCopperGen;
    public static boolean disableIronGen;
    
    public ConfigManagerMars(final File file, final boolean update) {
        if (!ConfigManagerMars.loaded) {
            ConfigManagerMars.config = new Configuration(file);
            syncConfig(true, update);
        }
    }
    
    public static void syncConfig(final boolean load, final boolean update) {
        try {
            if (!ConfigManagerMars.config.isChild && load) {
                ConfigManagerMars.config.load();
            }
            Property prop = ConfigManagerMars.config.get("dimensions", "dimensionIDMars", -29);
            prop.comment = "Dimension ID for Mars";
            prop.setLanguageKey("gc.configgui.dimensionIDMars").setRequiresMcRestart(true);
            ConfigManagerMars.dimensionIDMars = prop.getInt();
            prop = ConfigManagerMars.config.get("schematic", "idSchematicRocketT2", 2);
            prop.comment = "Schematic ID for Tier 2 Rocket, must be unique.";
            prop.setLanguageKey("gc.configgui.idSchematicRocketT2");
            ConfigManagerMars.idSchematicRocketT2 = prop.getInt(2);
            prop = ConfigManagerMars.config.get("schematic", "idSchematicCargoRocket", 3);
            prop.comment = "Schematic ID for Cargo Rocket, must be unique.";
            prop.setLanguageKey("gc.configgui.idSchematicCargoRocket");
            ConfigManagerMars.idSchematicCargoRocket = prop.getInt(3);
            prop = ConfigManagerMars.config.get("general", "launchControllerChunkLoad", true);
            prop.comment = "Whether or not the launch controller acts as a chunk loader. Will cause issues if disabled!";
            prop.setLanguageKey("gc.configgui.launchControllerChunkLoad");
            ConfigManagerMars.launchControllerChunkLoad = prop.getBoolean(true);
            prop = ConfigManagerMars.config.get("general", "launchControllerAllDims", false);
            prop.comment = "May rarely cause issues if enabled, depends on how the other mod's dimensions are.";
            prop.setLanguageKey("gc.configgui.launchControllerAllDims");
            ConfigManagerMars.launchControllerAllDims = prop.getBoolean(false);
            prop = ConfigManagerMars.config.get(update ? "general" : "worldgen", "Disable Iron Ore Gen on Mars", false);
            prop.comment = "Disable Iron Ore Gen on Mars.";
            prop.setLanguageKey("gc.configgui.disableIronGenMars");
            if (update) {
                prop = ConfigManagerMars.config.get("worldgen", prop.getName(), prop.getBoolean(), prop.comment);
                prop.setLanguageKey(prop.getLanguageKey());
                ConfigManagerMars.config.getCategory("general").remove((Object)prop.getName());
            }
            ConfigManagerMars.disableIronGen = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            prop = ConfigManagerMars.config.get(update ? "general" : "worldgen", "Disable Copper Ore Gen on Mars", false);
            prop.comment = "Disable Copper Ore Gen on Mars.";
            prop.setLanguageKey("gc.configgui.disableCopperGenMars");
            if (update) {
                prop = ConfigManagerMars.config.get("worldgen", prop.getName(), prop.getBoolean(), prop.comment);
                prop.setLanguageKey(prop.getLanguageKey());
                ConfigManagerMars.config.getCategory("general").remove((Object)prop.getName());
            }
            ConfigManagerMars.disableCopperGen = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            prop = ConfigManagerMars.config.get(update ? "general" : "worldgen", "Disable Tin Ore Gen on Mars", false);
            prop.comment = "Disable Tin Ore Gen on Mars.";
            prop.setLanguageKey("gc.configgui.disableTinGenMars");
            if (update) {
                prop = ConfigManagerMars.config.get("worldgen", prop.getName(), prop.getBoolean(), prop.comment);
                prop.setLanguageKey(prop.getLanguageKey());
                ConfigManagerMars.config.getCategory("general").remove((Object)prop.getName());
            }
            ConfigManagerMars.disableTinGen = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            prop = ConfigManagerMars.config.get(update ? "general" : "worldgen", "Disable Desh Ore Gen on Mars", false);
            prop.comment = "Disable Desh Ore Gen on Mars.";
            prop.setLanguageKey("gc.configgui.disableDeshGenMars");
            if (update) {
                prop = ConfigManagerMars.config.get("worldgen", prop.getName(), prop.getBoolean(), prop.comment);
                prop.setLanguageKey(prop.getLanguageKey());
                ConfigManagerMars.config.getCategory("general").remove((Object)prop.getName());
            }
            ConfigManagerMars.disableDeshGen = prop.getBoolean(false);
            ConfigManagerMars.propOrder.add(prop.getName());
            if (!load && ConfigManagerMars.config.hasChanged()) {
                ConfigManagerMars.config.save();
            }
        }
        catch (Exception e) {
            FMLLog.log(Level.ERROR, (Throwable)e, "Galacticraft Mars (Planets) has a problem loading it's config", new Object[0]);
        }
    }
    
    static {
        ConfigManagerMars.propOrder = new ArrayList<String>();
    }
}
