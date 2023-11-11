package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.gen.*;
import micdoodle8.mods.galacticraft.core.perlin.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.*;
import micdoodle8.mods.galacticraft.core.perlin.generator.*;
import net.minecraft.init.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import java.util.*;

public class ChunkProviderMoon extends ChunkProviderGenerate
{
    final Block topBlockID;
    final byte topBlockMeta = 5;
    final Block fillBlockID;
    final byte fillBlockMeta = 3;
    final Block lowerBlockID;
    final byte lowerBlockMeta = 4;
    private final Random rand;
    private final NoiseModule noiseGen1;
    private final NoiseModule noiseGen2;
    private final NoiseModule noiseGen3;
    private final NoiseModule noiseGen4;
    public BiomeDecoratorMoon biomedecoratorplanet;
    private final World worldObj;
    private final MapGenVillageMoon villageGenerator;
    private final MapGenDungeon dungeonGenerator;
    private BiomeGenBase[] biomesForGeneration;
    private final MapGenBaseMeta caveGenerator;
    private static final int CRATER_PROB = 300;
    private static final int MID_HEIGHT = 63;
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 128;
    private static final int CHUNK_SIZE_Z = 16;
    
    public ChunkProviderMoon(final World par1World, final long par2, final boolean par4) {
        super(par1World, par2, par4);
        this.topBlockID = GCBlocks.blockMoon;
        this.fillBlockID = GCBlocks.blockMoon;
        this.lowerBlockID = GCBlocks.blockMoon;
        this.biomedecoratorplanet = new BiomeDecoratorMoon(BiomeGenBaseMoon.moonFlat);
        this.villageGenerator = new MapGenVillageMoon();
        this.dungeonGenerator = new MapGenDungeon(GCBlocks.blockMoon, 14, 8, 16, 3);
        this.dungeonGenerator.otherRooms.add(new RoomEmptyMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomChestsMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomSpawnerMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomChestsMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.otherRooms.add(new RoomChestsMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.bossRooms.add(new RoomBossMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.dungeonGenerator.treasureRooms.add(new RoomTreasureMoon(null, 0, 0, 0, ForgeDirection.UNKNOWN));
        this.biomesForGeneration = new BiomeGenBase[] { BiomeGenBaseMoon.moonFlat };
        this.caveGenerator = new MapGenCavesMoon();
        this.worldObj = par1World;
        this.rand = new Random(par2);
        this.noiseGen1 = (NoiseModule)new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen2 = (NoiseModule)new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen3 = (NoiseModule)new Gradient(this.rand.nextLong(), 1, 0.25f);
        this.noiseGen4 = (NoiseModule)new Gradient(this.rand.nextLong(), 1, 0.25f);
    }
    
    public void generateTerrain(final int chunkX, final int chunkZ, final Block[] idArray, final byte[] metaArray) {
        this.noiseGen1.setFrequency(0.0125f);
        this.noiseGen2.setFrequency(0.015f);
        this.noiseGen3.setFrequency(0.01f);
        this.noiseGen4.setFrequency(0.02f);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final double d = this.noiseGen1.getNoise((float)(x + chunkX * 16), (float)(z + chunkZ * 16)) * 8.0f;
                final double d2 = this.noiseGen2.getNoise((float)(x + chunkX * 16), (float)(z + chunkZ * 16)) * 24.0f;
                double d3 = this.noiseGen3.getNoise((float)(x + chunkX * 16), (float)(z + chunkZ * 16)) - 0.1;
                d3 *= 4.0;
                double yDev = 0.0;
                if (d3 < 0.0) {
                    yDev = d;
                }
                else if (d3 > 1.0) {
                    yDev = d2;
                }
                else {
                    yDev = d + (d2 - d) * d3;
                }
                for (int y = 0; y < 128; ++y) {
                    if (y < 63.0 + yDev) {
                        idArray[this.getIndex(x, y, z)] = this.lowerBlockID;
                        final int index = this.getIndex(x, y, z);
                        this.getClass();
                        metaArray[index] = 4;
                    }
                }
            }
        }
    }
    
    public void replaceBlocksForBiome(final int par1, final int par2, final Block[] arrayOfIDs, final byte[] arrayOfMeta, final BiomeGenBase[] par4ArrayOfBiomeGenBase) {
        final int var5 = 20;
        for (int var6 = 0; var6 < 16; ++var6) {
            for (int var7 = 0; var7 < 16; ++var7) {
                final int var8 = (int)(this.noiseGen4.getNoise((float)(var6 + par1 * 16), (float)(var7 * par2 * 16)) / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
                int var9 = -1;
                Block var10 = this.topBlockID;
                this.getClass();
                byte var14m = 5;
                Block var11 = this.fillBlockID;
                this.getClass();
                byte var15m = 3;
                for (int var12 = 127; var12 >= 0; --var12) {
                    final int index = this.getIndex(var6, var12, var7);
                    arrayOfMeta[index] = 0;
                    if (var12 <= 0 + this.rand.nextInt(5)) {
                        arrayOfIDs[index] = Blocks.bedrock;
                    }
                    else {
                        final Block var13 = arrayOfIDs[index];
                        if (Blocks.air == var13) {
                            var9 = -1;
                        }
                        else if (var13 == this.lowerBlockID) {
                            final int n = index;
                            this.getClass();
                            arrayOfMeta[n] = 4;
                            if (var9 == -1) {
                                if (var8 <= 0) {
                                    var10 = Blocks.air;
                                    var14m = 0;
                                    var11 = this.lowerBlockID;
                                    this.getClass();
                                    var15m = 4;
                                }
                                else if (var12 >= 36 && var12 <= 21) {
                                    var10 = this.topBlockID;
                                    this.getClass();
                                    var14m = 5;
                                    var10 = this.fillBlockID;
                                    this.getClass();
                                    var14m = 3;
                                }
                                var9 = var8;
                                if (var12 >= 19) {
                                    arrayOfIDs[index] = var10;
                                    arrayOfMeta[index] = var14m;
                                }
                                else if (var12 < 19 && var12 >= 18) {
                                    arrayOfIDs[index] = var11;
                                    arrayOfMeta[index] = var15m;
                                }
                            }
                            else if (var9 > 0) {
                                --var9;
                                arrayOfIDs[index] = var11;
                                arrayOfMeta[index] = var15m;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public Chunk provideChunk(final int par1, final int par2) {
        this.rand.setSeed(par1 * 341873128712L + par2 * 132897987541L);
        final Block[] ids = new Block[65536];
        final byte[] meta = new byte[65536];
        Arrays.fill(ids, Blocks.air);
        this.generateTerrain(par1, par2, ids, meta);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
        this.createCraters(par1, par2, ids, meta);
        this.replaceBlocksForBiome(par1, par2, ids, meta, this.biomesForGeneration);
        this.caveGenerator.generate((IChunkProvider)this, this.worldObj, par1, par2, ids, meta);
        this.dungeonGenerator.generateUsingArrays(this.worldObj, this.worldObj.getSeed(), par1 * 16, 25, par2 * 16, par1, par2, ids, meta);
        final Chunk var4 = new Chunk(this.worldObj, ids, meta, par1, par2);
        var4.generateSkylightMap();
        return var4;
    }
    
    public void createCraters(final int chunkX, final int chunkZ, final Block[] chunkArray, final byte[] metaArray) {
        for (int cx = chunkX - 2; cx <= chunkX + 2; ++cx) {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; ++cz) {
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen4.getNoise((float)(x * 16 + x), (float)(cz * 16 + z)) / 300.0f) {
                            final Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
                            final EnumCraterSize cSize = EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.length)];
                            final int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE;
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, chunkArray, metaArray);
                        }
                    }
                }
            }
        }
    }
    
    public void makeCrater(final int craterX, final int craterZ, final int chunkX, final int chunkZ, final int size, final Block[] chunkArray, final byte[] metaArray) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = xDev * xDev + zDev * zDev;
                    double yDev = sqrtY * sqrtY * 6.0;
                    yDev = 5.0 - yDev;
                    int helper = 0;
                    for (int y = 127; y > 0; --y) {
                        if (Blocks.air != chunkArray[this.getIndex(x, y, z)] && helper <= yDev) {
                            chunkArray[this.getIndex(x, y, z)] = Blocks.air;
                            metaArray[this.getIndex(x, y, z)] = 0;
                            ++helper;
                        }
                        if (helper > yDev) {
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public boolean chunkExists(final int par1, final int par2) {
        return true;
    }
    
    public boolean unloadQueuedChunks() {
        return false;
    }
    
    public int getLoadedChunkCount() {
        return 0;
    }
    
    private int getIndex(final int x, final int y, final int z) {
        return (x * 16 + z) * 256 + y;
    }
    
    private double randFromPoint(final int x, final int z) {
        int n = x + z * 57;
        n ^= n << 13;
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE) / 1.073741824E9;
    }
    
    public void decoratePlanet(final World par1World, final Random par2Random, final int par3, final int par4) {
        this.biomedecoratorplanet.decorate(par1World, par2Random, par3, par4);
    }
    
    public void populate(final IChunkProvider par1IChunkProvider, final int par2, final int par3) {
        BlockFalling.fallInstantly = true;
        final int var4 = par2 * 16;
        final int var5 = par3 * 16;
        this.worldObj.getBiomeGenForCoords(var4 + 16, var5 + 16);
        this.rand.setSeed(this.worldObj.getSeed());
        final long var6 = this.rand.nextLong() / 2L * 2L + 1L;
        final long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(par2 * var6 + par3 * var7 ^ this.worldObj.getSeed());
        this.dungeonGenerator.handleTileEntities(this.rand);
        if (!ConfigManagerCore.disableMoonVillageGen) {
            this.villageGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
        }
        this.decoratePlanet(this.worldObj, this.rand, var4, var5);
        BlockFalling.fallInstantly = false;
    }
    
    public boolean saveChunks(final boolean par1, final IProgressUpdate par2IProgressUpdate) {
        return true;
    }
    
    public boolean canSave() {
        return true;
    }
    
    public String makeString() {
        return "MoonLevelSource";
    }
    
    public List getPossibleCreatures(final EnumCreatureType par1EnumCreatureType, final int i, final int j, final int k) {
        if (par1EnumCreatureType == EnumCreatureType.monster) {
            return BiomeGenBaseMoon.moonFlat.getSpawnableList(par1EnumCreatureType);
        }
        return null;
    }
    
    public void recreateStructures(final int par1, final int par2) {
        if (!ConfigManagerCore.disableMoonVillageGen) {
            this.villageGenerator.func_151539_a((IChunkProvider)this, this.worldObj, par1, par2, (Block[])null);
        }
    }
}
