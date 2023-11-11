package micdoodle8.mods.miccore;

import net.minecraftforge.common.config.*;
import java.io.*;
import cpw.mods.fml.common.*;

public class ConfigManagerMicCore
{
    public static boolean loaded;
    static Configuration configuration;
    public static boolean enableSmallMoons;
    public static boolean enableDebug;
    
    public static void init() {
        if (!ConfigManagerMicCore.loaded) {
            ConfigManagerMicCore.configuration = new Configuration(new File(MicdoodlePlugin.canonicalConfigDir, "Galacticraft/miccore.conf"));
        }
        ConfigManagerMicCore.configuration.load();
        syncConfig();
    }
    
    public static void syncConfig() {
        try {
            ConfigManagerMicCore.enableSmallMoons = ConfigManagerMicCore.configuration.get("general", "Enable Small Moons", true, "This will cause some dimensions to appear round, disable if render transformations cause a conflict.").getBoolean(true);
            ConfigManagerMicCore.enableDebug = ConfigManagerMicCore.configuration.get("general", "Enable Debug messages", false, "Enable debug messages during Galacticraft bytecode injection at startup.").getBoolean(false);
        }
        catch (Exception e) {
            FMLLog.severe("Problem loading core config (\"miccore.conf\")", new Object[0]);
        }
        finally {
            if (ConfigManagerMicCore.configuration.hasChanged()) {
                ConfigManagerMicCore.configuration.save();
            }
            ConfigManagerMicCore.loaded = true;
        }
    }
}
