package micdoodle8.mods.galacticraft.planets.asteroids.tick;

import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.common.*;
import net.minecraft.server.*;
import net.minecraft.world.*;
import cpw.mods.fml.common.eventhandler.*;

public class AsteroidsTickHandlerServer
{
    public static ShortRangeTelepadHandler spaceRaceData;
    
    public static void restart() {
        AsteroidsTickHandlerServer.spaceRaceData = null;
    }
    
    @SubscribeEvent
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return;
        }
        if (event.phase == TickEvent.Phase.START && AsteroidsTickHandlerServer.spaceRaceData == null) {
            final World world = (World)server.worldServerForDimension(0);
            AsteroidsTickHandlerServer.spaceRaceData = (ShortRangeTelepadHandler)world.mapStorage.loadData((Class)ShortRangeTelepadHandler.class, "ShortRangeTelepads");
            if (AsteroidsTickHandlerServer.spaceRaceData == null) {
                AsteroidsTickHandlerServer.spaceRaceData = new ShortRangeTelepadHandler("ShortRangeTelepads");
                world.mapStorage.setData("ShortRangeTelepads", (WorldSavedData)AsteroidsTickHandlerServer.spaceRaceData);
            }
        }
    }
    
    static {
        AsteroidsTickHandlerServer.spaceRaceData = null;
    }
}
