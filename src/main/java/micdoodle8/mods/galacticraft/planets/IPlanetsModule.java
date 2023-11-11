package micdoodle8.mods.galacticraft.planets;

import cpw.mods.fml.common.event.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraftforge.common.config.*;

public interface IPlanetsModule
{
    void preInit(final FMLPreInitializationEvent p0);
    
    void init(final FMLInitializationEvent p0);
    
    void postInit(final FMLPostInitializationEvent p0);
    
    void serverInit(final FMLServerStartedEvent p0);
    
    void serverStarting(final FMLServerStartingEvent p0);
    
    void getGuiIDs(final List<Integer> p0);
    
    Object getGuiElement(final Side p0, final int p1, final EntityPlayer p2, final World p3, final int p4, final int p5, final int p6);
    
    Configuration getConfiguration();
    
    void syncConfig();
}
