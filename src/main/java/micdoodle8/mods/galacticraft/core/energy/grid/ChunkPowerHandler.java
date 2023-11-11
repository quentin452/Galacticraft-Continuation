package micdoodle8.mods.galacticraft.core.energy.grid;

import net.minecraftforge.common.*;
import net.minecraftforge.event.world.*;
import com.google.common.collect.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.api.transmission.tile.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.event.*;
import java.util.*;
import cpw.mods.fml.common.eventhandler.*;

public class ChunkPowerHandler
{
    private static boolean initiated;
    
    public static void initiate() {
        if (!ChunkPowerHandler.initiated) {
            ChunkPowerHandler.initiated = true;
            MinecraftForge.EVENT_BUS.register((Object)new ChunkPowerHandler());
        }
    }
    
    @SubscribeEvent
    public void onChunkLoad(final ChunkEvent.Load event) {
        if (!event.world.isRemote && event.getChunk() != null) {
            try {
                final ArrayList<Object> tileList = (ArrayList<Object>)Lists.newArrayList();
                tileList.addAll(event.getChunk().chunkTileEntityMap.values());
                for (final Object o : tileList) {
                    if (o instanceof TileEntity) {
                        final TileEntity tile = (TileEntity)o;
                        if (!(tile instanceof INetworkConnection)) {
                            continue;
                        }
                        ((INetworkConnection)tile).refresh();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (ConfigManagerCore.retrogenOil && event.world.provider.dimensionId == 0) {
                EventHandlerGC.retrogenOil(event.world, event.getChunk());
            }
        }
    }
    
    static {
        ChunkPowerHandler.initiated = false;
    }
}
