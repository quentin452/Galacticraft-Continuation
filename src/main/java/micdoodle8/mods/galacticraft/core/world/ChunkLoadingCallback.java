package micdoodle8.mods.galacticraft.core.world;

import net.minecraft.util.*;
import net.minecraftforge.common.config.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraftforge.common.*;
import java.io.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;

public class ChunkLoadingCallback implements ForgeChunkManager.LoadingCallback
{
    private static boolean loaded;
    private static HashMap<String, HashMap<Integer, HashSet<ChunkCoordinates>>> chunkLoaderList;
    private static boolean configLoaded;
    private static Configuration config;
    private static boolean loadOnLogin;
    
    public void ticketsLoaded(final List<ForgeChunkManager.Ticket> tickets, final World world) {
        for (final ForgeChunkManager.Ticket ticket : tickets) {
            final NBTTagCompound nbt = ticket.getModData();
            if (nbt != null) {
                final int tileX = nbt.getInteger("ChunkLoaderTileX");
                final int tileY = nbt.getInteger("ChunkLoaderTileY");
                final int tileZ = nbt.getInteger("ChunkLoaderTileZ");
                final TileEntity tile = world.getTileEntity(tileX, tileY, tileZ);
                if (!(tile instanceof IChunkLoader)) {
                    continue;
                }
                ((IChunkLoader)tile).onTicketLoaded(ticket, false);
            }
        }
    }
    
    public static void loadConfig(final File file) {
        if (!ChunkLoadingCallback.configLoaded) {
            ChunkLoadingCallback.config = new Configuration(file);
        }
        try {
            ChunkLoadingCallback.loadOnLogin = ChunkLoadingCallback.config.get("CHUNKLOADING", "LoadOnLogin", true, "If you don't want each player's chunks to load when they log in, set to false.").getBoolean(true);
        }
        catch (Exception e) {
            GCLog.severe("Problem loading chunkloading config (\"core.conf\")");
        }
        finally {
            if (ChunkLoadingCallback.config.hasChanged()) {
                ChunkLoadingCallback.config.save();
            }
            ChunkLoadingCallback.configLoaded = true;
        }
    }
    
    public static void addToList(final World world, final int x, final int y, final int z, final String playerName) {
        HashMap<Integer, HashSet<ChunkCoordinates>> dimensionMap = ChunkLoadingCallback.chunkLoaderList.get(playerName);
        if (dimensionMap == null) {
            dimensionMap = new HashMap<Integer, HashSet<ChunkCoordinates>>();
            ChunkLoadingCallback.chunkLoaderList.put(playerName, dimensionMap);
        }
        HashSet<ChunkCoordinates> chunkLoaders = dimensionMap.get(world.provider.dimensionId);
        if (chunkLoaders == null) {
            chunkLoaders = new HashSet<ChunkCoordinates>();
        }
        chunkLoaders.add(new ChunkCoordinates(x, y, z));
        dimensionMap.put(world.provider.dimensionId, chunkLoaders);
        ChunkLoadingCallback.chunkLoaderList.put(playerName, dimensionMap);
    }
    
    public static void forceChunk(final ForgeChunkManager.Ticket ticket, final World world, final int x, final int y, final int z, final String playerName) {
        addToList(world, x, y, z, playerName);
        final ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(x >> 4, z >> 4);
        ForgeChunkManager.forceChunk(ticket, chunkPos);
    }
    
    public static void save(final WorldServer world) {
        final File saveDir = getSaveDir();
        if (saveDir != null) {
            final File saveFile = new File(saveDir, "chunkloaders.dat");
            if (!saveFile.exists()) {
                try {
                    if (!saveFile.createNewFile()) {
                        GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath());
                    }
                }
                catch (IOException e) {
                    GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(saveFile);
            }
            catch (FileNotFoundException e2) {
                e2.printStackTrace();
            }
            if (fos != null) {
                final DataOutputStream dataStream = new DataOutputStream(fos);
                try {
                    dataStream.writeInt(ChunkLoadingCallback.chunkLoaderList.size());
                    for (final Map.Entry<String, HashMap<Integer, HashSet<ChunkCoordinates>>> playerEntry : ChunkLoadingCallback.chunkLoaderList.entrySet()) {
                        dataStream.writeUTF(playerEntry.getKey());
                        dataStream.writeInt(playerEntry.getValue().size());
                        for (final Map.Entry<Integer, HashSet<ChunkCoordinates>> dimensionEntry : playerEntry.getValue().entrySet()) {
                            dataStream.writeInt(dimensionEntry.getKey());
                            dataStream.writeInt(dimensionEntry.getValue().size());
                            for (final ChunkCoordinates coords : dimensionEntry.getValue()) {
                                dataStream.writeInt(coords.posX);
                                dataStream.writeInt(coords.posY);
                                dataStream.writeInt(coords.posZ);
                            }
                        }
                    }
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
                try {
                    dataStream.close();
                    fos.close();
                }
                catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
    
    private static File getSaveDir() {
        if (DimensionManager.getWorld(0) != null) {
            final File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "galacticraft");
            if (!saveDir.exists() && !saveDir.mkdirs()) {
                GCLog.severe("Could not create chunk loader save data folder: " + saveDir.getAbsolutePath());
            }
            return saveDir;
        }
        return null;
    }
    
    public static void load(final WorldServer world) {
        if (ChunkLoadingCallback.loaded) {
            return;
        }
        DataInputStream dataStream = null;
        try {
            final File saveDir = getSaveDir();
            if (saveDir != null) {
                if (!saveDir.exists() && !saveDir.mkdirs()) {
                    GCLog.severe("Could not create chunk loader save data folder: " + saveDir.getAbsolutePath());
                }
                final File saveFile = new File(saveDir, "chunkloaders.dat");
                if (saveFile.exists()) {
                    dataStream = new DataInputStream(new FileInputStream(saveFile));
                    for (int playerCount = dataStream.readInt(), l = 0; l < playerCount; ++l) {
                        final String ownerName = dataStream.readUTF();
                        final int mapSize = dataStream.readInt();
                        final HashMap<Integer, HashSet<ChunkCoordinates>> dimensionMap = new HashMap<Integer, HashSet<ChunkCoordinates>>();
                        for (int i = 0; i < mapSize; ++i) {
                            final int dimensionID = dataStream.readInt();
                            final HashSet<ChunkCoordinates> coords = new HashSet<ChunkCoordinates>();
                            dimensionMap.put(dimensionID, coords);
                            for (int coordSetSize = dataStream.readInt(), j = 0; j < coordSetSize; ++j) {
                                coords.add(new ChunkCoordinates(dataStream.readInt(), dataStream.readInt(), dataStream.readInt()));
                            }
                        }
                        ChunkLoadingCallback.chunkLoaderList.put(ownerName, dimensionMap);
                    }
                    dataStream.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (dataStream != null) {
                try {
                    dataStream.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        ChunkLoadingCallback.loaded = true;
    }
    
    public static void onPlayerLogin(final EntityPlayer player) {
        for (final Map.Entry<String, HashMap<Integer, HashSet<ChunkCoordinates>>> playerEntry : ChunkLoadingCallback.chunkLoaderList.entrySet()) {
            if (player.getGameProfile().getName().equals(playerEntry.getKey())) {
                for (final Map.Entry<Integer, HashSet<ChunkCoordinates>> dimensionEntry : playerEntry.getValue().entrySet()) {
                    final int dimID = dimensionEntry.getKey();
                    if (ChunkLoadingCallback.loadOnLogin) {
                        MinecraftServer.getServer().worldServerForDimension(dimID);
                    }
                }
            }
        }
    }
    
    public static void onPlayerLogout(final EntityPlayer player) {
    }
    
    static {
        ChunkLoadingCallback.chunkLoaderList = new HashMap<String, HashMap<Integer, HashSet<ChunkCoordinates>>>();
    }
}
