package micdoodle8.mods.galacticraft.api.prefab.world.gen;

import net.minecraft.world.gen.*;
import micdoodle8.mods.galacticraft.core.perlin.generator.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.init.*;
import net.minecraft.world.chunk.*;
import micdoodle8.mods.galacticraft.core.world.gen.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.prefab.core.*;

public abstract class ChunkProviderSpace extends ChunkProviderGenerate
{
    protected final Random rand;
    private final Gradient noiseGen1;
    private final Gradient noiseGen2;
    private final Gradient noiseGen3;
    private final Gradient noiseGen4;
    private final Gradient noiseGen5;
    private final Gradient noiseGen6;
    private final Gradient noiseGen7;
    protected final World worldObj;
    private BiomeGenBase[] biomesForGeneration;
    private final double TERRAIN_HEIGHT_MOD;
    private final double SMALL_FEATURE_HEIGHT_MOD;
    private final double MOUNTAIN_HEIGHT_MOD;
    private final double VALLEY_HEIGHT_MOD;
    private final int CRATER_PROB;
    private final int MID_HEIGHT;
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 256;
    private static final int CHUNK_SIZE_Z = 16;
    private static final double MAIN_FEATURE_FILTER_MOD = 4.0;
    private static final double LARGE_FEATURE_FILTER_MOD = 8.0;
    private static final double SMALL_FEATURE_FILTER_MOD = 8.0;
    private List<MapGenBaseMeta> worldGenerators;
    
    public ChunkProviderSpace(final World par1World, final long seed, final boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
        this.biomesForGeneration = this.getBiomesForGeneration();
        this.TERRAIN_HEIGHT_MOD = this.getHeightModifier();
        this.SMALL_FEATURE_HEIGHT_MOD = this.getSmallFeatureHeightModifier();
        this.MOUNTAIN_HEIGHT_MOD = this.getMountainHeightModifier();
        this.VALLEY_HEIGHT_MOD = this.getValleyHeightModifier();
        this.CRATER_PROB = this.getCraterProbability();
        this.MID_HEIGHT = this.getSeaLevel();
        this.worldObj = par1World;
        this.rand = new Random(seed);
        this.noiseGen1 = new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen2 = new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen3 = new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen4 = new Gradient(this.rand.nextLong(), 2, 0.25f);
        this.noiseGen5 = new Gradient(this.rand.nextLong(), 1, 0.25f);
        this.noiseGen6 = new Gradient(this.rand.nextLong(), 1, 0.25f);
        this.noiseGen7 = new Gradient(this.rand.nextLong(), 1, 0.25f);
    }
    
    public void generateTerrain(final int chunkX, final int chunkZ, final Block[] idArray, final byte[] metaArray) {
        this.noiseGen1.setFrequency(0.015f);
        this.noiseGen2.setFrequency(0.01f);
        this.noiseGen3.setFrequency(0.01f);
        this.noiseGen4.setFrequency(0.01f);
        this.noiseGen5.setFrequency(0.01f);
        this.noiseGen6.setFrequency(0.001f);
        this.noiseGen7.setFrequency(0.005f);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final double baseHeight = this.noiseGen1.getNoise((float)(chunkX * 16 + x), (float)(chunkZ * 16 + z)) * this.TERRAIN_HEIGHT_MOD;
                final double smallHillHeight = this.noiseGen2.getNoise((float)(chunkX * 16 + x), (float)(chunkZ * 16 + z)) * this.SMALL_FEATURE_HEIGHT_MOD;
                double mountainHeight = Math.abs(this.noiseGen3.getNoise((float)(chunkX * 16 + x), (float)(chunkZ * 16 + z)));
                double valleyHeight = Math.abs(this.noiseGen4.getNoise((float)(chunkX * 16 + x), (float)(chunkZ * 16 + z)));
                final double featureFilter = this.noiseGen5.getNoise((float)(chunkX * 16 + x), (float)(chunkZ * 16 + z)) * 4.0;
                final double largeFilter = this.noiseGen6.getNoise((float)(chunkX * 16 + x), (float)(chunkZ * 16 + z)) * 8.0;
                final double smallFilter = this.noiseGen7.getNoise((float)(chunkX * 16 + x), (float)(chunkZ * 16 + z)) * 8.0 - 0.5;
                mountainHeight = this.lerp(smallHillHeight, mountainHeight * this.MOUNTAIN_HEIGHT_MOD, this.fade(this.clamp(mountainHeight * 2.0, 0.0, 1.0)));
                valleyHeight = this.lerp(smallHillHeight, valleyHeight * this.VALLEY_HEIGHT_MOD - this.VALLEY_HEIGHT_MOD + 9.0, this.fade(this.clamp((valleyHeight + 2.0) * 4.0, 0.0, 1.0)));
                double yDev = this.lerp(valleyHeight, mountainHeight, this.fade(largeFilter));
                yDev = this.lerp(smallHillHeight, yDev, smallFilter);
                yDev = this.lerp(baseHeight, yDev, featureFilter);
                for (int y = 0; y < 256; ++y) {
                    if (y < this.MID_HEIGHT + yDev) {
                        idArray[this.getIndex(x, y, z)] = this.getStoneBlock().getBlock();
                        metaArray[this.getIndex(x, y, z)] = this.getStoneBlock().getMetadata();
                    }
                }
            }
        }
    }
    
    private double lerp(final double d1, final double d2, final double t) {
        if (t < 0.0) {
            return d1;
        }
        if (t > 1.0) {
            return d2;
        }
        return d1 + (d2 - d1) * t;
    }
    
    private double fade(final double n) {
        return n * n * n * (n * (n * 6.0 - 15.0) + 10.0);
    }
    
    private double clamp(final double x, final double min, final double max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }
        return x;
    }
    
    public void replaceBlocksForBiome(final int par1, final int par2, final Block[] arrayOfIDs, final byte[] arrayOfMeta, final BiomeGenBase[] par4ArrayOfBiomeGenBase) {
        final int var5 = 20;
        final float var6 = 0.03125f;
        this.noiseGen4.setFrequency(0.0625f);
        for (int var7 = 0; var7 < 16; ++var7) {
            for (int var8 = 0; var8 < 16; ++var8) {
                final int var9 = (int)(this.noiseGen4.getNoise((float)(par1 * 16 + var7), (float)(par2 * 16 + var8)) / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
                int var10 = -1;
                Block var11 = this.getGrassBlock().getBlock();
                byte var14m = this.getGrassBlock().getMetadata();
                Block var12 = this.getDirtBlock().getBlock();
                byte var15m = this.getDirtBlock().getMetadata();
                for (int var13 = 255; var13 >= 0; --var13) {
                    final int index = this.getIndex(var7, var13, var8);
                    if (var13 <= 0 + this.rand.nextInt(5)) {
                        arrayOfIDs[index] = Blocks.bedrock;
                    }
                    else {
                        final Block var14 = arrayOfIDs[index];
                        if (Blocks.air == var14) {
                            var10 = -1;
                        }
                        else if (var14 == this.getStoneBlock().getBlock()) {
                            arrayOfMeta[index] = this.getStoneBlock().getMetadata();
                            if (var10 == -1) {
                                if (var9 <= 0) {
                                    var11 = Blocks.air;
                                    var14m = 0;
                                    var12 = this.getStoneBlock().getBlock();
                                    var15m = this.getStoneBlock().getMetadata();
                                }
                                else if (var13 >= 36 && var13 <= 21) {
                                    var11 = this.getGrassBlock().getBlock();
                                    var14m = this.getGrassBlock().getMetadata();
                                    var11 = this.getDirtBlock().getBlock();
                                    var14m = this.getDirtBlock().getMetadata();
                                }
                                var10 = var9;
                                if (var13 >= 19) {
                                    arrayOfIDs[index] = var11;
                                    arrayOfMeta[index] = var14m;
                                }
                                else {
                                    arrayOfIDs[index] = var12;
                                    arrayOfMeta[index] = var15m;
                                }
                            }
                            else if (var10 > 0) {
                                --var10;
                                arrayOfIDs[index] = var12;
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
        this.generateTerrain(par1, par2, ids, meta);
        this.createCraters(par1, par2, ids, meta);
        this.replaceBlocksForBiome(par1, par2, ids, meta, this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16));
        if (this.worldGenerators == null) {
            this.worldGenerators = this.getWorldGenerators();
        }
        for (final MapGenBaseMeta generator : this.worldGenerators) {
            generator.generate((IChunkProvider)this, this.worldObj, par1, par2, ids, meta);
        }
        this.onChunkProvide(par1, par2, ids, meta);
        final Chunk var4 = new Chunk(this.worldObj, ids, meta, par1, par2);
        final byte[] var5 = var4.getBiomeArray();
        for (int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = (byte)this.biomesForGeneration[var6].biomeID;
        }
        var4.generateSkylightMap();
        return var4;
    }
    
    public void createCraters(final int chunkX, final int chunkZ, final Block[] chunkArray, final byte[] metaArray) {
        this.noiseGen5.setFrequency(0.015f);
        for (int cx = chunkX - 2; cx <= chunkX + 2; ++cx) {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; ++cz) {
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen5.getNoise((float)(cx * 16 + x), (float)(cz * 16 + z)) / this.CRATER_PROB) {
                            final Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
                            final EnumCraterSize cSize = EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.length)];
                            final int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE + 15;
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
                        if (helper > yDev) {
                            break;
                        }
                        if (chunkArray[this.getIndex(x, y, z)] != null) {
                            chunkArray[this.getIndex(x, y, z)] = Blocks.air;
                            metaArray[this.getIndex(x, y, z)] = 0;
                            ++helper;
                        }
                    }
                }
            }
        }
    }
    
    private int getIndex(final int x, final int y, final int z) {
        return (x * 16 + z) * 256 + y;
    }
    
    private double randFromPoint(final int x, final int z) {
        int n = x + z * 57;
        n ^= n << 13;
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE) / 1.073741824E9;
    }
    
    public boolean chunkExists(final int par1, final int par2) {
        return true;
    }
    
    public void decoratePlanet(final World par1World, final Random par2Random, final int par3, final int par4) {
        this.getBiomeGenerator().decorate(par1World, par2Random, par3, par4);
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
        this.decoratePlanet(this.worldObj, this.rand, var4, var5);
        this.onPopulate(par1IChunkProvider, par2, par3);
        BlockFalling.fallInstantly = false;
    }
    
    public boolean saveChunks(final boolean par1, final IProgressUpdate par2IProgressUpdate) {
        return true;
    }
    
    public boolean canSave() {
        return true;
    }
    
    public String makeString() {
        return "RandomLevelSource";
    }
    
    public List getPossibleCreatures(final EnumCreatureType par1EnumCreatureType, final int i, final int j, final int k) {
        if (par1EnumCreatureType == EnumCreatureType.monster) {
            final List monsters = new ArrayList();
            for (final BiomeGenBase.SpawnListEntry monster : this.getMonsters()) {
                monsters.add(monster);
            }
            return monsters;
        }
        if (par1EnumCreatureType == EnumCreatureType.creature) {
            final List creatures = new ArrayList();
            for (final BiomeGenBase.SpawnListEntry creature : this.getCreatures()) {
                creatures.add(creature);
            }
            return creatures;
        }
        return null;
    }
    
    protected abstract BiomeDecoratorSpace getBiomeGenerator();
    
    protected abstract BiomeGenBase[] getBiomesForGeneration();
    
    protected abstract int getSeaLevel();
    
    protected abstract List<MapGenBaseMeta> getWorldGenerators();
    
    protected abstract BiomeGenBase.SpawnListEntry[] getMonsters();
    
    protected abstract BiomeGenBase.SpawnListEntry[] getCreatures();
    
    protected abstract BlockMetaPair getGrassBlock();
    
    protected abstract BlockMetaPair getDirtBlock();
    
    protected abstract BlockMetaPair getStoneBlock();
    
    public abstract double getHeightModifier();
    
    public abstract double getSmallFeatureHeightModifier();
    
    public abstract double getMountainHeightModifier();
    
    public abstract double getValleyHeightModifier();
    
    public abstract int getCraterProbability();
    
    public abstract void onChunkProvide(final int p0, final int p1, final Block[] p2, final byte[] p3);
    
    public abstract void onPopulate(final IChunkProvider p0, final int p1, final int p2);
}
