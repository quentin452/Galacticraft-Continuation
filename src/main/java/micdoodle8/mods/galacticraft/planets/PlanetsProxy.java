package micdoodle8.mods.galacticraft.planets;

import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;

public class PlanetsProxy implements IGuiHandler
{
    public void preInit(final FMLPreInitializationEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.preInit(event);
        }
    }
    
    public void init(final FMLInitializationEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.init(event);
        }
    }
    
    public void postInit(final FMLPostInitializationEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.postInit(event);
        }
    }
    
    public void serverStarting(final FMLServerStartingEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.serverStarting(event);
        }
    }
    
    public void serverInit(final FMLServerStartedEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.serverInit(event);
        }
    }
    
    public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            final List<Integer> guiIDs = new ArrayList<Integer>();
            module.getGuiIDs((List)guiIDs);
            if (guiIDs.contains(ID)) {
                return module.getGuiElement(Side.SERVER, ID, player, world, x, y, z);
            }
        }
        return null;
    }
    
    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        for (final IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values()) {
            final List<Integer> guiIDs = new ArrayList<Integer>();
            module.getGuiIDs((List)guiIDs);
            if (guiIDs.contains(ID)) {
                return module.getGuiElement(Side.CLIENT, ID, player, world, x, y, z);
            }
        }
        return null;
    }
}
