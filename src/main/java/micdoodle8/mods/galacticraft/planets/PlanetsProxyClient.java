package micdoodle8.mods.galacticraft.planets;

import micdoodle8.mods.galacticraft.planets.mars.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import java.util.*;
import cpw.mods.fml.common.event.*;

public class PlanetsProxyClient extends PlanetsProxy
{
    public void preInit(final FMLPreInitializationEvent event) {
        GalacticraftPlanets.clientModules.put("MarsModule", new MarsModuleClient());
        GalacticraftPlanets.clientModules.put("AsteroidsModule", new AsteroidsModuleClient());
        super.preInit(event);
        for (final IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values()) {
            module.preInit(event);
        }
    }
    
    public void init(final FMLInitializationEvent event) {
        super.init(event);
        for (final IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values()) {
            module.init(event);
        }
    }
    
    public void postInit(final FMLPostInitializationEvent event) {
        super.postInit(event);
        for (final IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values()) {
            module.postInit(event);
        }
    }
    
    public void serverStarting(final FMLServerStartingEvent event) {
        super.serverStarting(event);
    }
}
