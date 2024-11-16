package micdoodle8.mods.galacticraft.planets;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;

public interface IPlanetsModule {

    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    void serverInit(FMLServerStartedEvent event);

    void serverStarting(FMLServerStartingEvent event);

    void getGuiIDs(List<Integer> idList);

    Object getGuiElement(Side side, int ID, EntityPlayer player, World world, int x, int y, int z);

    Configuration getConfiguration();

    void syncConfig();
}
