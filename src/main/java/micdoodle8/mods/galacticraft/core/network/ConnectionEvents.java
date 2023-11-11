package micdoodle8.mods.galacticraft.core.network;

import cpw.mods.fml.common.gameevent.*;
import micdoodle8.mods.galacticraft.core.world.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import cpw.mods.fml.common.network.*;
import io.netty.util.concurrent.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.network.*;

public class ConnectionEvents
{
    private static boolean clientConnected;

    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        ChunkLoadingCallback.onPlayerLogout(event.player);
    }

    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        ChunkLoadingCallback.onPlayerLogin(event.player);
        if (event.player instanceof EntityPlayerMP) {
            final EntityPlayerMP thePlayer = (EntityPlayerMP)event.player;
            final GCPlayerStats stats = GCPlayerStats.get(thePlayer);
            SpaceStationWorldData.checkAllStations(thePlayer, stats);
            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SPACESTATION_CLIENT_ID, new Object[] { WorldUtil.spaceStationDataToString(stats.spaceStationDimensionData) }), thePlayer);
            final SpaceRace raceForPlayer = SpaceRaceManager.getSpaceRaceFromPlayer(thePlayer.getGameProfile().getName());
            if (raceForPlayer != null) {
                SpaceRaceManager.sendSpaceRaceData(thePlayer, raceForPlayer);
            }
        }
        if (event.player.worldObj.provider instanceof WorldProviderSpaceStation && event.player instanceof EntityPlayerMP) {
            ((WorldProviderSpaceStation)event.player.worldObj.provider).getSpinManager().sendPacketsToClient((EntityPlayerMP)event.player);
        }
    }

    @SubscribeEvent
    public void onConnectionReceived(final FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        if (ConfigManagerCore.enableDebug) {
            final Integer[] idList = (Integer[]) WorldUtil.getPlanetList().get(0);
            String ids = "";
            for (int j = 0; j < idList.length; ++j) {
                ids = ids + idList[j].toString() + " ";
            }
            GCLog.info("Galacticraft server sending dimension IDs to connecting client: " + ids);
        }
        event.manager.scheduleOutboundPacket((Packet)ConnectionPacket.createDimPacket(WorldUtil.getPlanetListInts()), new GenericFutureListener[0]);
        event.manager.scheduleOutboundPacket((Packet)ConnectionPacket.createSSPacket(WorldUtil.getSpaceStationListInts()), new GenericFutureListener[0]);
        event.manager.scheduleOutboundPacket((Packet)ConnectionPacket.createConfigPacket(ConfigManagerCore.getServerConfigOverride()), new GenericFutureListener[0]);
    }

    @SubscribeEvent
    public void onConnectionOpened(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!event.isLocal) {
            ConnectionEvents.clientConnected = true;
        }
        MapUtil.resetClient();
    }

    @SubscribeEvent
    public void onConnectionClosed(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (ConnectionEvents.clientConnected) {
            ConnectionEvents.clientConnected = false;
            WorldUtil.unregisterPlanets();
            WorldUtil.unregisterSpaceStations();
            ConfigManagerCore.restoreClientConfigOverrideable();
        }
    }

    static {
        ConnectionEvents.clientConnected = false;
        EnumConnectionState.field_150761_f.put(PacketSimple.class, EnumConnectionState.PLAY);
        EnumConnectionState.PLAY.field_150770_i.put((Object)2515, (Object)PacketSimple.class);
    }
}
