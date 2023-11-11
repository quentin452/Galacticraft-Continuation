package micdoodle8.mods.galacticraft.core.util;

import org.apache.logging.log4j.*;
import cpw.mods.fml.relauncher.*;

public class GCLog
{
    public static void info(final String message) {
        FMLRelaunchLog.log("Galacticraft", Level.INFO, message);
    }

    public static void severe(final String message) {
        FMLRelaunchLog.log("Galacticraft", Level.ERROR, message);
    }

    public static void debug(final String message) {
        if (ConfigManagerCore.enableDebug) {
            FMLRelaunchLog.log("Galacticraft", Level.INFO, "Debug: " + message);
        }
    }

    public static void exception(final Exception e) {
        FMLRelaunchLog.log("Galacticraft", Level.ERROR, e.getMessage());
    }
}
