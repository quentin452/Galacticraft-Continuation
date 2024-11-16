package micdoodle8.mods.galacticraft.core.energy.grid;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection;
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;

public class ChunkPowerHandler {

    private static boolean initiated = false;

    public static void initiate() {
        if (!ChunkPowerHandler.initiated) {
            ChunkPowerHandler.initiated = true;
            MinecraftForge.EVENT_BUS.register(new ChunkPowerHandler());
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!event.world.isRemote && event.getChunk() != null) {
            try {
                final ArrayList<Object> tileList = Lists.newArrayList();
                tileList.addAll(event.getChunk().chunkTileEntityMap.values());

                for (final Object o : tileList) {
                    if (o instanceof TileEntity && o instanceof INetworkConnection) {
                        TileEntity tile = (TileEntity) o;
                        ((INetworkConnection) tile).refresh();
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if (ConfigManagerCore.retrogenOil && event.world.provider.dimensionId == 0) {
                EventHandlerGC.retrogenOil(event.world, event.getChunk());
            }
        }
    }
}
