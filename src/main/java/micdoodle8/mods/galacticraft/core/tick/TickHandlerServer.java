package micdoodle8.mods.galacticraft.core.tick;

import micdoodle8.mods.galacticraft.core.energy.grid.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.world.chunk.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.server.*;
import java.io.*;
import java.awt.image.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.oxygen.*;
import net.minecraft.block.*;
import java.util.concurrent.*;
import com.google.common.collect.*;

public class TickHandlerServer
{
    private static Map<Integer, CopyOnWriteArrayList<ScheduledBlockChange>> scheduledBlockChanges;
    private static Map<Integer, CopyOnWriteArrayList<BlockVec3>> scheduledTorchUpdates;
    private static Map<Integer, List<BlockVec3>> edgeChecks;
    private static LinkedList<EnergyNetwork> networkTicks;
    public static Map<Integer, Map<Long, List<Footprint>>> serverFootprintMap;
    public static List<BlockVec3Dim> footprintBlockChanges;
    public static WorldDataSpaceRaces spaceRaceData;
    public static ArrayList<EntityPlayerMP> playersRequestingMapData;
    private static long tickCount;
    public static LinkedList<TileEntityOxygenTransmitter> oxygenTransmitterUpdates;
    public static LinkedList<TileEntityHydrogenPipe> hydrogenTransmitterUpdates;
    public static LinkedList<TileBaseConductor> energyTransmitterUpdates;
    private final int MAX_BLOCKS_PER_TICK = 50000;

    public static void restart() {
        TickHandlerServer.scheduledBlockChanges.clear();
        TickHandlerServer.scheduledTorchUpdates.clear();
        TickHandlerServer.edgeChecks.clear();
        TickHandlerServer.networkTicks.clear();
        TickHandlerServer.serverFootprintMap.clear();
        TickHandlerServer.oxygenTransmitterUpdates.clear();
        TickHandlerServer.hydrogenTransmitterUpdates.clear();
        TickHandlerServer.energyTransmitterUpdates.clear();
        TickHandlerServer.playersRequestingMapData.clear();
        TickHandlerServer.networkTicks.clear();
        for (final SpaceRace race : SpaceRaceManager.getSpaceRaces()) {
            SpaceRaceManager.removeSpaceRace(race);
        }
        TickHandlerServer.spaceRaceData = null;
        TickHandlerServer.tickCount = 0L;
        MapUtil.reset();
    }

    public static void addFootprint(final long chunkKey, final Footprint print, final int dimID) {
        Map<Long, List<Footprint>> footprintMap = TickHandlerServer.serverFootprintMap.get(dimID);
        List<Footprint> footprints;
        if (footprintMap == null) {
            footprintMap = new HashMap<Long, List<Footprint>>();
            footprints = new ArrayList<Footprint>();
        }
        else {
            footprints = footprintMap.get(chunkKey);
            if (footprints == null) {
                footprints = new ArrayList<Footprint>();
            }
        }
        footprints.add(print);
        footprintMap.put(chunkKey, footprints);
        TickHandlerServer.serverFootprintMap.put(dimID, footprintMap);
    }

    public static void scheduleNewBlockChange(final int dimID, final ScheduledBlockChange change) {
        CopyOnWriteArrayList<ScheduledBlockChange> changeList = TickHandlerServer.scheduledBlockChanges.get(dimID);
        if (changeList == null) {
            changeList = new CopyOnWriteArrayList<ScheduledBlockChange>();
        }
        changeList.add(change);
        TickHandlerServer.scheduledBlockChanges.put(dimID, changeList);
    }

    public static void scheduleNewBlockChange(final int dimID, final List<ScheduledBlockChange> changeAdd) {
        CopyOnWriteArrayList<ScheduledBlockChange> changeList = TickHandlerServer.scheduledBlockChanges.get(dimID);
        if (changeList == null) {
            changeList = new CopyOnWriteArrayList<ScheduledBlockChange>();
        }
        changeList.addAll(changeAdd);
        TickHandlerServer.scheduledBlockChanges.put(dimID, changeList);
    }

    public static void scheduleNewTorchUpdate(final int dimID, final List<BlockVec3> torches) {
        CopyOnWriteArrayList<BlockVec3> updateList = TickHandlerServer.scheduledTorchUpdates.get(dimID);
        if (updateList == null) {
            updateList = new CopyOnWriteArrayList<BlockVec3>();
        }
        updateList.addAll(torches);
        TickHandlerServer.scheduledTorchUpdates.put(dimID, updateList);
    }

    public static void scheduleNewEdgeCheck(final int dimID, final BlockVec3 edgeBlock) {
        List<BlockVec3> updateList = TickHandlerServer.edgeChecks.get(dimID);
        if (updateList == null) {
            updateList = new ArrayList<BlockVec3>();
        }
        updateList.add(edgeBlock);
        TickHandlerServer.edgeChecks.put(dimID, updateList);
    }

    public static boolean scheduledForChange(final int dimID, final BlockVec3 test) {
        final CopyOnWriteArrayList<ScheduledBlockChange> changeList = TickHandlerServer.scheduledBlockChanges.get(dimID);
        if (changeList != null) {
            for (final ScheduledBlockChange change : changeList) {
                if (test.equals((Object)change.getChangePosition())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void scheduleNetworkTick(final EnergyNetwork grid) {
        TickHandlerServer.networkTicks.add(grid);
    }

    public static void removeNetworkTick(final EnergyNetwork grid) {
        TickHandlerServer.networkTicks.remove(grid);
    }

    @SubscribeEvent
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            if (MapUtil.calculatingMap.get()) {
                MapUtil.BiomeMapNextTick();
            }
            else if (!MapUtil.doneOverworldTexture) {
                MapUtil.makeOverworldTexture();
            }
            if (TickHandlerServer.spaceRaceData == null) {
                final World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
                TickHandlerServer.spaceRaceData = (WorldDataSpaceRaces)world.mapStorage.loadData(WorldDataSpaceRaces.class, "GCSpaceRaceData");
                if (TickHandlerServer.spaceRaceData == null) {
                    TickHandlerServer.spaceRaceData = new WorldDataSpaceRaces("GCSpaceRaceData");
                    world.mapStorage.setData("GCSpaceRaceData", TickHandlerServer.spaceRaceData);
                }
            }
            SpaceRaceManager.tick();
            TileEntityOxygenSealer.onServerTick();
            if (TickHandlerServer.tickCount % 100L == 0L) {
                final WorldServer[] worlds = server.worldServers;
                for (final WorldServer world2 : worlds) {
                    final ChunkProviderServer chunkProviderServer = world2.theChunkProviderServer;
                    final Map<Long, List<Footprint>> footprintMap = TickHandlerServer.serverFootprintMap.get(world2.provider.dimensionId);
                    if (footprintMap != null) {
                        boolean mapChanged = false;
                        if (chunkProviderServer != null) {
                            List<? extends Chunk> loadedChunks = chunkProviderServer.loadedChunks;
                            for (Chunk chunk : loadedChunks) {
                                final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition);
                                final List<Footprint> footprints = footprintMap.get(chunkKey);
                                if (footprints != null) {
                                    final List<Footprint> toRemove = new ArrayList<>();
                                    for (final Footprint footprint : footprints) {
                                        footprint.age += 100;
                                        if (footprint.age >= 3200) {
                                            toRemove.add(footprint);
                                        }
                                    }
                                    if (!toRemove.isEmpty()) {
                                        footprints.removeAll(toRemove);
                                    }
                                    footprintMap.put(chunkKey, footprints);
                                    mapChanged = true;
                                    GalacticraftCore.packetPipeline.sendToDimension((IPacket) new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_FOOTPRINT_LIST, new Object[]{chunkKey, footprints.toArray(new Footprint[footprints.size()])}), world2.provider.dimensionId);
                                }
                            }
                        }
                        if (mapChanged) {
                            TickHandlerServer.serverFootprintMap.put(world2.provider.dimensionId, footprintMap);
                        }
                    }
                }
            }
            if (!TickHandlerServer.footprintBlockChanges.isEmpty()) {
                for (final BlockVec3Dim targetPoint : TickHandlerServer.footprintBlockChanges) {
                    final WorldServer[] worlds2 = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
                    for (int k = 0; k < worlds2.length; ++k) {
                        final WorldServer world3 = worlds2[k];
                        if (world3.provider.dimensionId == targetPoint.dim) {
                            final long chunkKey2 = ChunkCoordIntPair.chunkXZ2Int(targetPoint.x >> 4, targetPoint.z >> 4);
                            GalacticraftCore.packetPipeline.sendToAllAround((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_FOOTPRINTS_REMOVED, new Object[] { chunkKey2, new BlockVec3(targetPoint.x, targetPoint.y, targetPoint.z) }), new NetworkRegistry.TargetPoint(targetPoint.dim, (double)targetPoint.x, (double)targetPoint.y, (double)targetPoint.z, 50.0));
                        }
                    }
                }
                TickHandlerServer.footprintBlockChanges.clear();
            }
            if (TickHandlerServer.tickCount % 20L == 0L && !TickHandlerServer.playersRequestingMapData.isEmpty()) {
                final File baseFolder = new File(MinecraftServer.getServer().worldServerForDimension(0).getChunkSaveLocation(), "galacticraft/overworldMap");
                if (!baseFolder.exists() && !baseFolder.mkdirs()) {
                    GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath());
                }
                else {
                    final ArrayList<EntityPlayerMP> copy = new ArrayList<EntityPlayerMP>(TickHandlerServer.playersRequestingMapData);
                    final BufferedImage reusable = new BufferedImage(400, 400, 1);
                    for (final EntityPlayerMP playerMP : copy) {
                        final GCPlayerStats stats = GCPlayerStats.get(playerMP);
                        MapUtil.makeVanillaMap(playerMP.dimension, (int)Math.floor(stats.coordsTeleportedFromX) >> 4, (int)Math.floor(stats.coordsTeleportedFromZ) >> 4, baseFolder, reusable);
                    }
                    TickHandlerServer.playersRequestingMapData.removeAll(copy);
                }
            }
            ++TickHandlerServer.tickCount;
            if (TickHandlerServer.tickCount >= Long.MAX_VALUE) {
                TickHandlerServer.tickCount = 0L;
            }
            ++EnergyNetwork.tickCount;
        }
        else if (event.phase == TickEvent.Phase.END) {
            int maxPasses = 10;
            while (!TickHandlerServer.networkTicks.isEmpty()) {
                final LinkedList<EnergyNetwork> pass = new LinkedList<EnergyNetwork>();
                pass.addAll(TickHandlerServer.networkTicks);
                TickHandlerServer.networkTicks.clear();
                for (final EnergyNetwork grid : pass) {
                    grid.tickEnd();
                }
                if (--maxPasses <= 0) {
                    break;
                }
            }
            maxPasses = 10;
            while (!TickHandlerServer.oxygenTransmitterUpdates.isEmpty()) {
                final LinkedList<TileEntityOxygenTransmitter> pass2 = new LinkedList<TileEntityOxygenTransmitter>();
                pass2.addAll(TickHandlerServer.oxygenTransmitterUpdates);
                TickHandlerServer.oxygenTransmitterUpdates.clear();
                for (final TileEntityOxygenTransmitter newTile : pass2) {
                    if (!newTile.isInvalid()) {
                        newTile.refresh();
                    }
                }
                if (--maxPasses <= 0) {
                    break;
                }
            }
            maxPasses = 10;
            while (!TickHandlerServer.hydrogenTransmitterUpdates.isEmpty()) {
                final LinkedList<TileEntityHydrogenPipe> pass3 = new LinkedList<TileEntityHydrogenPipe>();
                pass3.addAll(TickHandlerServer.hydrogenTransmitterUpdates);
                TickHandlerServer.hydrogenTransmitterUpdates.clear();
                for (final TileEntityHydrogenPipe newTile2 : pass3) {
                    if (!newTile2.isInvalid()) {
                        newTile2.refresh();
                    }
                }
                if (--maxPasses <= 0) {
                    break;
                }
            }
            maxPasses = 10;
            while (!TickHandlerServer.energyTransmitterUpdates.isEmpty()) {
                final LinkedList<TileBaseConductor> pass4 = new LinkedList<TileBaseConductor>();
                pass4.addAll(TickHandlerServer.energyTransmitterUpdates);
                TickHandlerServer.energyTransmitterUpdates.clear();
                for (final TileBaseConductor newTile3 : pass4) {
                    if (!newTile3.isInvalid()) {
                        newTile3.refresh();
                    }
                }
                if (--maxPasses <= 0) {
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            final WorldServer world = (WorldServer)event.world;
            final CopyOnWriteArrayList<ScheduledBlockChange> changeList = TickHandlerServer.scheduledBlockChanges.get(world.provider.dimensionId);
            if (changeList != null && !changeList.isEmpty()) {
                int blockCount = 0;
                this.getClass();
                final int blockCountMax = Math.max(50000, changeList.size() / 4);
                final List<ScheduledBlockChange> newList = new ArrayList<ScheduledBlockChange>(Math.max(0, changeList.size() - blockCountMax));
                for (final ScheduledBlockChange change : changeList) {
                    if (++blockCount > blockCountMax) {
                        newList.add(change);
                    }
                    else {
                        if (change == null) {
                            continue;
                        }
                        final BlockVec3 changePosition = change.getChangePosition();
                        final Block block = world.getBlock(changePosition.x, changePosition.y, changePosition.z);
                        if (changePosition == null || (!(block instanceof BlockAir) && block != Blocks.fire)) {
                            continue;
                        }
                        world.setBlock(changePosition.x, changePosition.y, changePosition.z, change.getChangeID(), change.getChangeMeta(), change.getChangeUpdateFlag());
                    }
                }
                changeList.clear();
                TickHandlerServer.scheduledBlockChanges.remove(world.provider.dimensionId);
                if (newList.size() > 0) {
                    TickHandlerServer.scheduledBlockChanges.put(world.provider.dimensionId, new CopyOnWriteArrayList<ScheduledBlockChange>(newList));
                }
            }
            final CopyOnWriteArrayList<BlockVec3> torchList = TickHandlerServer.scheduledTorchUpdates.get(world.provider.dimensionId);
            if (torchList != null && !torchList.isEmpty()) {
                for (final BlockVec3 torch : torchList) {
                    if (torch != null) {
                        final Block b = world.getBlock(torch.x, torch.y, torch.z);
                        if (!(b instanceof BlockUnlitTorch)) {
                            continue;
                        }
                        world.scheduleBlockUpdateWithPriority(torch.x, torch.y, torch.z, b, 2 + world.rand.nextInt(30), 0);
                    }
                }
                torchList.clear();
                TickHandlerServer.scheduledTorchUpdates.remove(world.provider.dimensionId);
            }
            if (world.provider instanceof IOrbitDimension) {
                final Object[] array;
                final Object[] entityList = array = world.loadedEntityList.toArray();
                for (final Object o : array) {
                    if (o instanceof Entity) {
                        final Entity e = (Entity)o;
                        if (e.worldObj.provider instanceof IOrbitDimension) {
                            final IOrbitDimension dimension = (IOrbitDimension)e.worldObj.provider;
                            if (e.posY <= dimension.getYCoordToTeleportToPlanet()) {
                                int dim = 0;
                                try {
                                    dim = WorldUtil.getProviderForNameServer(dimension.getPlanetToOrbit()).dimensionId;
                                }
                                catch (Exception ex) {}
                                WorldUtil.transferEntityToDimension(e, dim, world, false, null);
                            }
                        }
                    }
                }
            }
        }
        else if (event.phase == TickEvent.Phase.END) {
            final WorldServer world = (WorldServer)event.world;
            final List<BlockVec3> edgesList = TickHandlerServer.edgeChecks.get(world.provider.dimensionId);
            final HashSet<BlockVec3> checkedThisTick = new HashSet<BlockVec3>();
            if (edgesList != null && !edgesList.isEmpty()) {
                final List<BlockVec3> edgesListCopy = new ArrayList<BlockVec3>();
                edgesListCopy.addAll(edgesList);
                for (final BlockVec3 edgeBlock : edgesListCopy) {
                    if (edgeBlock != null && !checkedThisTick.contains(edgeBlock)) {
                        if (scheduledForChange(world.provider.dimensionId, edgeBlock)) {
                            continue;
                        }
                        final ThreadFindSeal done = new ThreadFindSeal((World)world, edgeBlock, 2000, (List)new ArrayList());
                        checkedThisTick.addAll(done.checkedAll());
                    }
                }
                TickHandlerServer.edgeChecks.remove(world.provider.dimensionId);
            }
        }
    }

    static {
        TickHandlerServer.scheduledBlockChanges = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<ScheduledBlockChange>>();
        TickHandlerServer.scheduledTorchUpdates = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<BlockVec3>>();
        TickHandlerServer.edgeChecks = new HashMap<Integer, List<BlockVec3>>();
        TickHandlerServer.networkTicks = new LinkedList<EnergyNetwork>();
        TickHandlerServer.serverFootprintMap = new HashMap<Integer, Map<Long, List<Footprint>>>();
        TickHandlerServer.footprintBlockChanges = Lists.newArrayList();
        TickHandlerServer.spaceRaceData = null;
        TickHandlerServer.playersRequestingMapData = Lists.newArrayList();
        TickHandlerServer.oxygenTransmitterUpdates = new LinkedList<TileEntityOxygenTransmitter>();
        TickHandlerServer.hydrogenTransmitterUpdates = new LinkedList<TileEntityHydrogenPipe>();
        TickHandlerServer.energyTransmitterUpdates = new LinkedList<TileBaseConductor>();
    }
}
