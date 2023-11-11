package micdoodle8.mods.galacticraft.planets.asteroids.world.gen;

import net.minecraft.world.gen.*;
import micdoodle8.mods.galacticraft.core.perlin.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.perlin.generator.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.chunk.storage.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import java.util.*;

public class ChunkProviderAsteroids extends ChunkProviderGenerate
{
    final Block ASTEROID_STONE;
    final byte ASTEROID_STONE_META_0 = 0;
    final byte ASTEROID_STONE_META_1 = 1;
    final byte ASTEROID_STONE_META_2 = 2;
    final Block DIRT;
    final byte DIRT_META = 0;
    final Block GRASS;
    final byte GRASS_META = 0;
    final Block LIGHT;
    final byte LIGHT_META = 0;
    final Block TALL_GRASS;
    final byte TALL_GRASS_META = 1;
    final Block FLOWER;
    final Block LAVA;
    final byte LAVA_META = 0;
    final Block WATER;
    final byte WATER_META = 0;
    private final Random rand;
    private final World worldObj;
    private final NoiseModule asteroidDensity;
    private final NoiseModule asteroidTurbulance;
    private final NoiseModule asteroidSkewX;
    private final NoiseModule asteroidSkewY;
    private final NoiseModule asteroidSkewZ;
    private final SpecialAsteroidBlockHandler coreHandler;
    private final SpecialAsteroidBlockHandler shellHandler;
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 256;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int MAX_ASTEROID_RADIUS = 25;
    private static final int MIN_ASTEROID_RADIUS = 5;
    private static final int MAX_ASTEROID_SKEW = 8;
    private static final int MIN_ASTEROID_Y = 48;
    private static final int MAX_ASTEROID_Y = 208;
    private static final int ASTEROID_CHANCE = 800;
    private static final int ASTEROID_CORE_CHANCE = 2;
    private static final int ASTEROID_SHELL_CHANCE = 2;
    private static final int MIN_BLOCKS_PER_CHUNK = 50;
    private static final int MAX_BLOCKS_PER_CHUNK = 200;
    private static final int ILMENITE_CHANCE = 400;
    private static final int IRON_CHANCE = 300;
    private static final int ALUMINUM_CHANCE = 250;
    private static final int RANDOM_BLOCK_FADE_SIZE = 32;
    private static final int FADE_BLOCK_CHANCE = 5;
    private static final int NOISE_OFFSET_SIZE = 256;
    private static final float MIN_HOLLOW_SIZE = 0.6f;
    private static final float MAX_HOLLOW_SIZE = 0.8f;
    private static final int HOLLOW_CHANCE = 10;
    private static final int MIN_RADIUS_FOR_HOLLOW = 15;
    private static final float HOLLOW_LAVA_SIZE = 0.12f;
    private static final int TREE_CHANCE = 2;
    private static final int TALL_GRASS_CHANCE = 2;
    private static final int FLOWER_CHANCE = 2;
    private static final int WATER_CHANCE = 2;
    private static final int LAVA_CHANCE = 2;
    private static final int GLOWSTONE_CHANCE = 20;
    private ArrayList<AsteroidData> largeAsteroids;
    private int largeCount;
    private static HashSet<BlockVec3> chunksDone;
    private int largeAsteroidsLastChunkX;
    private int largeAsteroidsLastChunkZ;
    
    public ChunkProviderAsteroids(final World par1World, final long par2, final boolean par4) {
        super(par1World, par2, par4);
        this.ASTEROID_STONE = AsteroidBlocks.blockBasic;
        this.DIRT = Blocks.dirt;
        this.GRASS = (Block)Blocks.grass;
        this.LIGHT = Blocks.glowstone;
        this.TALL_GRASS = (Block)Blocks.tallgrass;
        this.FLOWER = (Block)Blocks.red_flower;
        this.LAVA = Blocks.lava;
        this.WATER = Blocks.water;
        this.largeAsteroids = new ArrayList<AsteroidData>();
        this.largeCount = 0;
        this.worldObj = par1World;
        this.rand = new Random(par2);
        (this.asteroidDensity = (NoiseModule)new Billowed(this.rand.nextLong(), 2, 0.25f)).setFrequency(0.009f);
        this.asteroidDensity.amplitude = 0.6f;
        (this.asteroidTurbulance = (NoiseModule)new Gradient(this.rand.nextLong(), 1, 0.2f)).setFrequency(0.08f);
        this.asteroidTurbulance.amplitude = 0.5f;
        this.asteroidSkewX = (NoiseModule)new Gradient(this.rand.nextLong(), 1, 1.0f);
        this.asteroidSkewX.amplitude = 8.0f;
        this.asteroidSkewX.frequencyX = 0.005f;
        this.asteroidSkewY = (NoiseModule)new Gradient(this.rand.nextLong(), 1, 1.0f);
        this.asteroidSkewY.amplitude = 8.0f;
        this.asteroidSkewY.frequencyY = 0.005f;
        this.asteroidSkewZ = (NoiseModule)new Gradient(this.rand.nextLong(), 1, 1.0f);
        this.asteroidSkewZ.amplitude = 8.0f;
        this.asteroidSkewZ.frequencyZ = 0.005f;
        this.coreHandler = new SpecialAsteroidBlockHandler();
        final SpecialAsteroidBlockHandler coreHandler = this.coreHandler;
        final Block asteroid_STONE = this.ASTEROID_STONE;
        this.getClass();
        coreHandler.addBlock(new SpecialAsteroidBlock(asteroid_STONE, (byte)2, 5, 0.3));
        final SpecialAsteroidBlockHandler coreHandler2 = this.coreHandler;
        final Block asteroid_STONE2 = this.ASTEROID_STONE;
        this.getClass();
        coreHandler2.addBlock(new SpecialAsteroidBlock(asteroid_STONE2, (byte)1, 7, 0.3));
        final SpecialAsteroidBlockHandler coreHandler3 = this.coreHandler;
        final Block asteroid_STONE3 = this.ASTEROID_STONE;
        this.getClass();
        coreHandler3.addBlock(new SpecialAsteroidBlock(asteroid_STONE3, (byte)0, 11, 0.25));
        if (!ConfigManagerAsteroids.disableAluminumGen) {
            this.coreHandler.addBlock(new SpecialAsteroidBlock(this.ASTEROID_STONE, (byte)3, 5, 0.2));
        }
        if (!ConfigManagerAsteroids.disableIlmeniteGen) {
            this.coreHandler.addBlock(new SpecialAsteroidBlock(this.ASTEROID_STONE, (byte)4, 4, 0.15));
        }
        if (!ConfigManagerAsteroids.disableIronGen) {
            this.coreHandler.addBlock(new SpecialAsteroidBlock(this.ASTEROID_STONE, (byte)5, 3, 0.2));
        }
        if (ConfigManagerCore.enableSiliconOreGen) {
            this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.basicBlock, (byte)8, 2, 0.15));
        }
        this.coreHandler.addBlock(new SpecialAsteroidBlock(GCBlocks.basicBlock, (byte)12, 2, 0.13));
        this.coreHandler.addBlock(new SpecialAsteroidBlock(Blocks.diamond_ore, (byte)0, 1, 0.1));
        this.shellHandler = new SpecialAsteroidBlockHandler();
        final SpecialAsteroidBlockHandler shellHandler = this.shellHandler;
        final Block asteroid_STONE4 = this.ASTEROID_STONE;
        this.getClass();
        shellHandler.addBlock(new SpecialAsteroidBlock(asteroid_STONE4, (byte)0, 1, 0.15));
        final SpecialAsteroidBlockHandler shellHandler2 = this.shellHandler;
        final Block asteroid_STONE5 = this.ASTEROID_STONE;
        this.getClass();
        shellHandler2.addBlock(new SpecialAsteroidBlock(asteroid_STONE5, (byte)1, 3, 0.15));
        final SpecialAsteroidBlockHandler shellHandler3 = this.shellHandler;
        final Block asteroid_STONE6 = this.ASTEROID_STONE;
        this.getClass();
        shellHandler3.addBlock(new SpecialAsteroidBlock(asteroid_STONE6, (byte)2, 1, 0.15));
        this.shellHandler.addBlock(new SpecialAsteroidBlock(AsteroidBlocks.blockDenseIce, (byte)0, 1, 0.15));
    }
    
    public void generateTerrain(final int chunkX, final int chunkZ, final Block[] idArray, final byte[] metaArray, final boolean flagDataOnly) {
        this.largeAsteroids.clear();
        this.largeCount = 0;
        final Random random = new Random();
        final int asteroidChance = 800;
        final int rangeY = 160;
        final int rangeSize = 20;
        for (int i = chunkX - 3; i < chunkX + 3; ++i) {
            final int minX = i * 16;
            final int maxX = minX + 16;
            for (int k = chunkZ - 3; k < chunkZ + 3; ++k) {
                final int minZ = k * 16;
                final int maxZ = minZ + 16;
                for (int x = minX; x < maxX; x += 2) {
                    for (int z = minZ; z < maxZ; z += 2) {
                        if (this.randFromPointPos(x, z) < (this.asteroidDensity.getNoise((float)x, (float)z) + 0.4) / 800.0) {
                            random.setSeed(x + z * 3067);
                            final int y = random.nextInt(160) + 48;
                            final int size = random.nextInt(20) + 5;
                            this.generateAsteroid(random, x, y, z, chunkX << 4, chunkZ << 4, size, idArray, metaArray, flagDataOnly);
                            ++this.largeCount;
                        }
                    }
                }
            }
        }
    }
    
    private void generateAsteroid(final Random rand, final int asteroidX, final int asteroidY, final int asteroidZ, final int chunkX, final int chunkZ, final int size, final Block[] blockArray, final byte[] metaArray, final boolean flagDataOnly) {
        final SpecialAsteroidBlock core = this.coreHandler.getBlock(rand, size);
        SpecialAsteroidBlock shell = null;
        if (rand.nextInt(2) == 0) {
            shell = this.shellHandler.getBlock(rand, size);
        }
        boolean isHollow = false;
        final float hollowSize = rand.nextFloat() * 0.19999999f + 0.6f;
        if (rand.nextInt(10) == 0 && size >= 15) {
            isHollow = true;
            shell = new SpecialAsteroidBlock(AsteroidBlocks.blockDenseIce, (byte)0, 1, 0.15);
        }
        ((WorldProviderAsteroids)this.worldObj.provider).addAsteroid(asteroidX, asteroidY, asteroidZ, size, isHollow ? -1 : core.index);
        final int xMin = this.clamp(Math.max(chunkX, asteroidX - size - 8 - 2) - chunkX, 0, 16);
        final int zMin = this.clamp(Math.max(chunkZ, asteroidZ - size - 8 - 2) - chunkZ, 0, 16);
        final int yMin = asteroidY - size - 8 - 2;
        final int yMax = asteroidY + size + 8 + 2;
        final int xMax = this.clamp(Math.min(chunkX + 16, asteroidX + size + 8 + 2) - chunkX, 0, 16);
        final int zMax = this.clamp(Math.min(chunkZ + 16, asteroidZ + size + 8 + 2) - chunkZ, 0, 16);
        final int xSize = xMax - xMin;
        final int ySize = yMax - yMin;
        final int zSize = zMax - zMin;
        if (xSize <= 0 || ySize <= 0 || zSize <= 0) {
            return;
        }
        final float noiseOffsetX = this.randFromPoint(asteroidX, asteroidY, asteroidZ) * 256.0f + chunkX;
        final float noiseOffsetY = this.randFromPoint(asteroidX * 7, asteroidY * 11, asteroidZ * 13) * 256.0f;
        final float noiseOffsetZ = this.randFromPoint(asteroidX * 17, asteroidY * 23, asteroidZ * 29) * 256.0f + chunkZ;
        this.setOtherAxisFrequency(1.0f / (size * 2.0f / 2.0f));
        final float[] sizeXArray = new float[ySize * zSize];
        final float[] sizeZArray = new float[xSize * ySize];
        final float[] sizeYArray = new float[xSize * zSize];
        for (int x = 0; x < xSize; ++x) {
            final int xx = x * zSize;
            final float xxx = x + noiseOffsetX;
            for (int z = 0; z < zSize; ++z) {
                sizeYArray[xx + z] = this.asteroidSkewY.getNoise(xxx, z + noiseOffsetZ);
            }
        }
        final AsteroidData asteroidData = new AsteroidData(isHollow, sizeYArray, xMin, zMin, xMax, zMax, zSize, size, asteroidX, asteroidY, asteroidZ);
        this.largeAsteroids.add(asteroidData);
        this.largeAsteroidsLastChunkX = chunkX;
        this.largeAsteroidsLastChunkZ = chunkZ;
        if (flagDataOnly) {
            return;
        }
        for (int y = 0; y < ySize; ++y) {
            final int yy = y * zSize;
            final float yyy = y + noiseOffsetY;
            for (int z2 = 0; z2 < zSize; ++z2) {
                sizeXArray[yy + z2] = this.asteroidSkewX.getNoise(yyy, z2 + noiseOffsetZ);
            }
        }
        for (int x2 = 0; x2 < xSize; ++x2) {
            final int xx2 = x2 * ySize;
            final float xxx2 = x2 + noiseOffsetX;
            for (int y2 = 0; y2 < ySize; ++y2) {
                sizeZArray[xx2 + y2] = this.asteroidSkewZ.getNoise(xxx2, y2 + noiseOffsetY);
            }
        }
        double shellThickness = 0.0;
        int terrainY = 0;
        int terrainYY = 0;
        if (shell != null) {
            shellThickness = 1.0 - shell.thickness;
        }
        for (int x3 = xMax - 1; x3 >= xMin; --x3) {
            final int indexXY = (x3 - xMin) * ySize - yMin;
            final int indexXZ = (x3 - xMin) * zSize - zMin;
            final int distanceX = asteroidX - (x3 + chunkX);
            final int indexBaseX = x3 * 256 << 4;
            final float xx3 = (float)(x3 + chunkX);
            for (int z3 = zMin; z3 < zMax; ++z3) {
                if (isHollow) {
                    final float sizeModY = sizeYArray[indexXZ + z3];
                    terrainY = this.getTerrainHeightFor(sizeModY, asteroidY, size);
                    terrainYY = this.getTerrainHeightFor(sizeModY, asteroidY - 1, size);
                }
                float sizeY = size + sizeYArray[indexXZ + z3];
                sizeY *= sizeY;
                final int distanceZ = asteroidZ - (z3 + chunkZ);
                final int indexBase = indexBaseX | z3 * 256;
                final float zz = (float)(z3 + chunkZ);
                for (int y3 = yMin; y3 < yMax; ++y3) {
                    float dSizeX = distanceX / (size + sizeXArray[(y3 - yMin) * zSize + z3 - zMin]);
                    float dSizeZ = distanceZ / (size + sizeZArray[indexXY + y3]);
                    dSizeX *= dSizeX;
                    dSizeZ *= dSizeZ;
                    int distanceY = asteroidY - y3;
                    distanceY *= distanceY;
                    float distanceAbove;
                    float distance = distanceAbove = dSizeX + distanceY / sizeY + dSizeZ;
                    distance += this.asteroidTurbulance.getNoise(xx3, (float)y3, zz);
                    if (isHollow && distance <= hollowSize) {
                        distanceAbove += this.asteroidTurbulance.getNoise(xx3, (float)(y3 + 1), zz);
                        if (distanceAbove <= 1.0f && y3 - 1 == terrainYY) {
                            final int index = indexBase | y3 + 1;
                            blockArray[index] = this.LIGHT;
                            final int n = index;
                            this.getClass();
                            metaArray[n] = 0;
                        }
                    }
                    if (distance <= 1.0f) {
                        final int index = indexBase | y3;
                        if (isHollow && distance <= hollowSize) {
                            if (y3 == terrainY) {
                                blockArray[index] = this.GRASS;
                                final int n2 = index;
                                this.getClass();
                                metaArray[n2] = 0;
                            }
                            else if (y3 < terrainY) {
                                blockArray[index] = this.DIRT;
                                final int n3 = index;
                                this.getClass();
                                metaArray[n3] = 0;
                            }
                            else {
                                blockArray[index] = Blocks.air;
                                metaArray[index] = 0;
                            }
                        }
                        else if (distance <= core.thickness) {
                            if (rand.nextBoolean()) {
                                blockArray[index] = core.block;
                                metaArray[index] = core.meta;
                            }
                            else {
                                blockArray[index] = this.ASTEROID_STONE;
                                final int n4 = index;
                                this.getClass();
                                metaArray[n4] = 0;
                            }
                        }
                        else if (shell != null && distance >= shellThickness) {
                            blockArray[index] = shell.block;
                            metaArray[index] = shell.meta;
                        }
                        else {
                            blockArray[index] = this.ASTEROID_STONE;
                            final int n5 = index;
                            this.getClass();
                            metaArray[n5] = 1;
                        }
                    }
                }
            }
        }
        if (isHollow) {
            shellThickness = 0.0;
            if (shell != null) {
                shellThickness = 1.0 - shell.thickness;
            }
            for (int x3 = xMin; x3 < xMax; ++x3) {
                final int indexXY = (x3 - xMin) * ySize - yMin;
                final int indexXZ = (x3 - xMin) * zSize - zMin;
                int distanceX = asteroidX - (x3 + chunkX);
                distanceX *= distanceX;
                final int indexBaseX = x3 * 256 << 4;
                for (int z4 = zMin; z4 < zMax; ++z4) {
                    final float sizeModY2 = sizeYArray[indexXZ + z4];
                    float sizeY = size + sizeYArray[indexXZ + z4];
                    sizeY *= sizeY;
                    int distanceZ = asteroidZ - (z4 + chunkZ);
                    distanceZ *= distanceZ;
                    final int indexBase = indexBaseX | z4 * 256;
                    for (int y4 = yMin; y4 < yMax; ++y4) {
                        float sizeX = size + sizeXArray[(y4 - yMin) * zSize + z4 - zMin];
                        float sizeZ = size + sizeZArray[indexXY + y4];
                        sizeX *= sizeX;
                        sizeZ *= sizeZ;
                        int distanceY2 = asteroidY - y4;
                        distanceY2 *= distanceY2;
                        float distance2 = distanceX / sizeX + distanceY2 / sizeY + distanceZ / sizeZ;
                        distance2 += this.asteroidTurbulance.getNoise((float)(x3 + chunkX), (float)y4, (float)(z4 + chunkZ));
                        if (distance2 <= 1.0f) {
                            final int index2 = indexBase | y4;
                            final int indexAbove = indexBase | y4 + 1;
                            if (Blocks.air == blockArray[indexAbove] && (blockArray[index2] == this.ASTEROID_STONE || blockArray[index2] == this.GRASS) && this.rand.nextInt(20) == 0) {
                                blockArray[index2] = this.LIGHT;
                                final int n6 = index2;
                                this.getClass();
                                metaArray[n6] = 0;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private final void setOtherAxisFrequency(final float frequency) {
        this.asteroidSkewX.frequencyY = frequency;
        this.asteroidSkewX.frequencyZ = frequency;
        this.asteroidSkewY.frequencyX = frequency;
        this.asteroidSkewY.frequencyZ = frequency;
        this.asteroidSkewZ.frequencyX = frequency;
        this.asteroidSkewZ.frequencyY = frequency;
    }
    
    private final int clamp(int x, final int min, final int max) {
        if (x < min) {
            x = min;
        }
        else if (x > max) {
            x = max;
        }
        return x;
    }
    
    private final double clamp(double x, final double min, final double max) {
        if (x < min) {
            x = min;
        }
        else if (x > max) {
            x = max;
        }
        return x;
    }
    
    private final int getTerrainHeightFor(final float yMod, final int asteroidY, final int asteroidSize) {
        return (int)(asteroidY - asteroidSize / 4 + yMod * 1.5f);
    }
    
    private final int getTerrainHeightAt(final int x, final int z, final float[] yModArray, final int xMin, final int zMin, final int zSize, final int asteroidY, final int asteroidSize) {
        final int index = (x - xMin) * zSize - zMin;
        if (index < yModArray.length && index >= 0) {
            final float yMod = yModArray[index];
            return this.getTerrainHeightFor(yMod, asteroidY, asteroidSize);
        }
        return 1;
    }
    
    public Chunk provideChunk(final int par1, final int par2) {
        this.rand.setSeed(par1 * 341873128712L + par2 * 132897987541L);
        final Block[] ids = new Block[65536];
        final byte[] meta = new byte[65536];
        this.generateTerrain(par1, par2, ids, meta, false);
        final Chunk var4 = new Chunk(this.worldObj, ids, meta, par1, par2);
        final byte[] var5 = var4.getBiomeArray();
        for (int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = (byte)BiomeGenBaseAsteroids.asteroid.biomeID;
        }
        this.generateSkylightMap(var4, par1, par2);
        return var4;
    }
    
    private int getIndex(final int x, final int y, final int z) {
        return x * 256 * 16 | z * 256 | y;
    }
    
    private String timeString(final long time1, final long time2) {
        final int ms100 = (int)((time2 - time1) / 10000L);
        final int msdecimal = ms100 % 100;
        final String msd = ((ms100 < 10) ? "0" : "") + ms100;
        return "" + ms100 / 100 + "." + msd + "ms";
    }
    
    private float randFromPoint(final int x, final int y, final int z) {
        int n = x + z * 57 + y * 571;
        n ^= n << 13;
        n = (n * (n * n * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE);
        return 1.0f - n / 1.07374182E9f;
    }
    
    private float randFromPoint(final int x, final int z) {
        int n = x + z * 57;
        n ^= n << 13;
        n = (n * (n * n * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE);
        return 1.0f - n / 1.07374182E9f;
    }
    
    private float randFromPointPos(final int x, final int z) {
        int n = x + z * 57;
        n ^= n << 13;
        n = (n * (n * n * 15731 + 789221) + 1376312589 & 0x3FFFFFFF);
        return 1.0f - n / 1.07374182E9f;
    }
    
    public boolean chunkExists(final int par1, final int par2) {
        return true;
    }
    
    public void populate(final IChunkProvider par1IChunkProvider, final int chunkX, final int chunkZ) {
        final int x = chunkX << 4;
        final int z = chunkZ << 4;
        if (!ChunkProviderAsteroids.chunksDone.add(new BlockVec3(x, 0, z))) {
            return;
        }
        BlockFalling.fallInstantly = true;
        this.worldObj.getBiomeGenForCoords(x + 16, z + 16);
        BlockFalling.fallInstantly = false;
        this.rand.setSeed(this.worldObj.getSeed());
        final long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        final long var8 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(chunkX * var7 + chunkZ * var8 ^ this.worldObj.getSeed());
        if (this.rand.nextBoolean()) {
            final double density = this.asteroidDensity.getNoise((float)(chunkX * 16), (float)(chunkZ * 16)) * 0.54;
            final double numOfBlocks = this.clamp(this.randFromPoint(chunkX, chunkZ), 0.4, 1.0) * 200.0 * density + 50.0;
            final int y0 = this.rand.nextInt(2);
            final int yRange = 160;
            for (int i = 0; i < numOfBlocks; ++i) {
                final int y2 = this.rand.nextInt(yRange) + 48;
                if (y0 == y2 / 16 % 2) {
                    final int px = x + this.rand.nextInt(16);
                    final int pz = z + this.rand.nextInt(16);
                    final Block block = this.ASTEROID_STONE;
                    this.getClass();
                    int meta = 1;
                    if (this.rand.nextInt(400) == 0) {
                        meta = 4;
                        if (ConfigManagerAsteroids.disableIlmeniteGen) {
                            continue;
                        }
                    }
                    else if (this.rand.nextInt(300) == 0) {
                        meta = 5;
                        if (ConfigManagerAsteroids.disableIronGen) {
                            continue;
                        }
                    }
                    else if (this.rand.nextInt(250) == 0) {
                        meta = 3;
                        if (ConfigManagerAsteroids.disableAluminumGen) {
                            continue;
                        }
                    }
                    this.worldObj.setBlock(px, y2, pz, block, meta, 2);
                    int count = 9;
                    if (!(this.worldObj.getBlock(px - 1, y2, pz) instanceof BlockAir)) {
                        count = 1;
                    }
                    else if (!(this.worldObj.getBlock(px - 2, y2, pz) instanceof BlockAir)) {
                        count = 3;
                    }
                    else if (!(this.worldObj.getBlock(px - 3, y2, pz) instanceof BlockAir)) {
                        count = 5;
                    }
                    else if (!(this.worldObj.getBlock(px - 4, y2, pz) instanceof BlockAir)) {
                        count = 7;
                    }
                    this.worldObj.setLightValue(EnumSkyBlock.Block, px - 1, y2, pz, count);
                }
            }
        }
        if (this.largeAsteroidsLastChunkX != chunkX || this.largeAsteroidsLastChunkZ != chunkZ) {
            this.generateTerrain(chunkX, chunkZ, null, null, true);
        }
        this.rand.setSeed(chunkX * var7 + chunkZ * var8 ^ this.worldObj.getSeed());
        if (!this.largeAsteroids.isEmpty()) {
            for (final AsteroidData asteroidIndex : this.largeAsteroids) {
                if (!asteroidIndex.isHollow) {
                    continue;
                }
                final float[] sizeYArray = asteroidIndex.sizeYArray;
                final int xMin = asteroidIndex.xMinArray;
                final int zMin = asteroidIndex.zMinArray;
                final int zSize = asteroidIndex.zSizeArray;
                final int asteroidY = asteroidIndex.asteroidYArray;
                final int asteroidSize = asteroidIndex.asteroidSizeArray;
                boolean treesdone = false;
                if (ConfigManagerCore.challengeAsteroidPopulation || this.rand.nextInt(2) == 0) {
                    int treeType = this.rand.nextInt(3);
                    if (treeType == 1) {
                        treeType = 0;
                    }
                    final WorldGenTrees wg = new WorldGenTrees(false, 2, 0, 0, false);
                    for (int tries = 0; tries < 5; ++tries) {
                        final int j = this.rand.nextInt(16) + x + 8;
                        final int k = this.rand.nextInt(16) + z + 8;
                        if (wg.generate(this.worldObj, this.rand, j, this.getTerrainHeightAt(j - x, k - z, sizeYArray, xMin, zMin, zSize, asteroidY, asteroidSize), k)) {
                            break;
                        }
                    }
                    treesdone = true;
                }
                if (!treesdone || this.rand.nextInt(2) == 0) {
                    final int l = this.rand.nextInt(16) + x + 8;
                    final int m = this.rand.nextInt(16) + z + 8;
                    final Block tall_GRASS = this.TALL_GRASS;
                    this.getClass();
                    new WorldGenTallGrass(tall_GRASS, 1).generate(this.worldObj, this.rand, l, this.getTerrainHeightAt(l - x, m - z, sizeYArray, xMin, zMin, zSize, asteroidY, asteroidSize), m);
                }
                if (this.rand.nextInt(2) == 0) {
                    final int l = this.rand.nextInt(16) + x + 8;
                    final int m = this.rand.nextInt(16) + z + 8;
                    new WorldGenFlowers(this.FLOWER).generate(this.worldObj, this.rand, l, this.getTerrainHeightAt(l - x, m - z, sizeYArray, xMin, zMin, zSize, asteroidY, asteroidSize), m);
                }
                if (this.rand.nextInt(2) == 0) {
                    final int l = this.rand.nextInt(16) + x + 8;
                    final int m = this.rand.nextInt(16) + z + 8;
                    new WorldGenLakes(this.LAVA).generate(this.worldObj, this.rand, l, this.getTerrainHeightAt(l - x, m - z, sizeYArray, xMin, zMin, zSize, asteroidY, asteroidSize), m);
                }
                if (this.rand.nextInt(2) != 0) {
                    continue;
                }
                final int l = this.rand.nextInt(16) + x + 8;
                final int m = this.rand.nextInt(16) + z + 8;
                new WorldGenLakes(this.WATER).generate(this.worldObj, this.rand, l, this.getTerrainHeightAt(l - x, m - z, sizeYArray, xMin, zMin, zSize, asteroidY, asteroidSize), m);
            }
        }
        for (int xx = 0; xx < 16; ++xx) {
            final int xPos = x + xx;
            for (int zz = 0; zz < 16; ++zz) {
                final int zPos = z + zz;
                for (int y3 = 16; y3 < 240; ++y3) {
                    this.worldObj.updateLightByType(EnumSkyBlock.Block, xPos, y3, zPos);
                }
            }
        }
    }
    
    public void generateSkylightMap(final Chunk chunk, final int cx, final int cz) {
        final World w = chunk.worldObj;
        final boolean flagXChunk = w.getChunkProvider().chunkExists(cx - 1, cz);
        final boolean flagZUChunk = w.getChunkProvider().chunkExists(cx, cz + 1);
        final boolean flagZDChunk = w.getChunkProvider().chunkExists(cx, cz - 1);
        final boolean flagXZUChunk = w.getChunkProvider().chunkExists(cx - 1, cz + 1);
        final boolean flagXZDChunk = w.getChunkProvider().chunkExists(cx - 1, cz - 1);
        for (int j = 0; j < 16; ++j) {
            if (chunk.getBlockStorageArray()[j] == null) {
                chunk.getBlockStorageArray()[j] = new ExtendedBlockStorage(j << 4, false);
            }
        }
        final int i = chunk.getTopFilledSegment();
        chunk.heightMapMinimum = Integer.MAX_VALUE;
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                chunk.precipitationHeightMap[k + (l << 4)] = -999;
                int y = i + 15;
                while (y > 0) {
                    if (chunk.func_150808_b(k, y - 1, l) == 0) {
                        --y;
                    }
                    else {
                        if ((chunk.heightMap[l << 4 | k] = y) < chunk.heightMapMinimum) {
                            chunk.heightMapMinimum = y;
                            break;
                        }
                        break;
                    }
                }
            }
        }
        for (final AsteroidData a : this.largeAsteroids) {
            int yMin = a.asteroidYArray - a.asteroidSizeArray;
            int yMax = a.asteroidYArray + a.asteroidSizeArray;
            int xMin = a.xMinArray;
            if (yMin < 0) {
                yMin = 0;
            }
            if (yMax > 255) {
                yMax = 255;
            }
            if (xMin == 0) {
                xMin = 1;
            }
            for (int x = a.xMax - 1; x >= xMin; --x) {
                for (int z = a.zMinArray; z < a.zMax; ++z) {
                    for (int y2 = yMin; y2 < yMax; ++y2) {
                        if (chunk.getBlock(x - 1, y2, z) instanceof BlockAir && !(chunk.getBlock(x, y2, z) instanceof BlockAir)) {
                            int count = 2;
                            if (x > 1 && chunk.getBlock(x - 2, y2, z) instanceof BlockAir) {
                                count += 2;
                            }
                            if (x > 2) {
                                if (chunk.getBlock(x - 3, y2, z) instanceof BlockAir) {
                                    count += 2;
                                }
                                if (chunk.getBlock(x - 3, y2 + 1, z) instanceof BlockAir) {
                                    ++count;
                                }
                                if (chunk.getBlock(x - 3, y2 + 1, z) instanceof BlockAir) {
                                    ++count;
                                }
                                if (z > 0 && chunk.getBlock(x - 3, y2, z - 1) instanceof BlockAir) {
                                    ++count;
                                }
                                if (z < 15 && chunk.getBlock(x - 3, y2, z + 1) instanceof BlockAir) {
                                    ++count;
                                }
                            }
                            if (x > 3) {
                                if (chunk.getBlock(x - 4, y2, z) instanceof BlockAir) {
                                    count += 2;
                                }
                                if (chunk.getBlock(x - 4, y2 + 1, z) instanceof BlockAir) {
                                    ++count;
                                }
                                if (chunk.getBlock(x - 4, y2 + 1, z) instanceof BlockAir) {
                                    ++count;
                                }
                                if (z > 0 && !(chunk.getBlock(x - 4, y2, z - 1) instanceof BlockAir)) {
                                    ++count;
                                }
                                if (z < 15 && !(chunk.getBlock(x - 4, y2, z + 1) instanceof BlockAir)) {
                                    ++count;
                                }
                            }
                            if (count > 12) {
                                count = 12;
                            }
                            chunk.func_150807_a(x - 1, y2, z, GCBlocks.brightAir, 13 - count);
                            final ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[y2 >> 4];
                            if (extendedblockstorage != null) {
                                extendedblockstorage.setExtBlocklightValue(x - 1, y2 & 0xF, z, count + 2);
                            }
                        }
                    }
                }
            }
        }
        chunk.isModified = true;
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
            return BiomeGenBaseAsteroids.asteroid.getSpawnableList(par1EnumCreatureType);
        }
        return null;
    }
    
    public BlockVec3 isLargeAsteroidAt(final int x0, final int z0) {
        for (int i0 = 0; i0 <= 32; ++i0) {
            for (int i2 = -i0; i2 <= i0; ++i2) {
                int xToCheck = (x0 >> 4) + i0;
                int zToCheck = (z0 >> 4) + i2;
                if (this.isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }
                xToCheck = (x0 >> 4) + i0;
                zToCheck = (z0 >> 4) - i2;
                if (this.isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }
                xToCheck = (x0 >> 4) - i0;
                zToCheck = (z0 >> 4) + i2;
                if (this.isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }
                xToCheck = (x0 >> 4) - i0;
                zToCheck = (z0 >> 4) - i2;
                if (this.isLargeAsteroidAt0(xToCheck * 16, zToCheck * 16)) {
                    return new BlockVec3(xToCheck * 16, 0, zToCheck * 16);
                }
            }
        }
        return null;
    }
    
    private boolean isLargeAsteroidAt0(final int x0, final int z0) {
        for (int x = x0; x < x0 + 16; x += 2) {
            for (int z = z0; z < z0 + 16; z += 2) {
                if (Math.abs(this.randFromPoint(x, z)) < (this.asteroidDensity.getNoise((float)x, (float)z) + 0.4) / 800.0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void reset() {
        ChunkProviderAsteroids.chunksDone.clear();
    }
    
    static {
        ChunkProviderAsteroids.chunksDone = new HashSet<BlockVec3>();
    }
    
    private class AsteroidData
    {
        public boolean isHollow;
        public float[] sizeYArray;
        public int xMinArray;
        public int zMinArray;
        public int xMax;
        public int zMax;
        public int zSizeArray;
        public int asteroidSizeArray;
        public int asteroidXArray;
        public int asteroidYArray;
        public int asteroidZArray;
        
        public AsteroidData(final boolean hollow, final float[] sizeYArray2, final int xMin, final int zMin, final int xmax, final int zmax, final int zSize, final int size, final int asteroidX, final int asteroidY, final int asteroidZ) {
            this.isHollow = hollow;
            this.sizeYArray = sizeYArray2.clone();
            this.xMinArray = xMin;
            this.zMinArray = zMin;
            this.xMax = xmax;
            this.zMax = zmax;
            this.zSizeArray = zSize;
            this.asteroidSizeArray = size;
            this.asteroidXArray = asteroidX;
            this.asteroidYArray = asteroidY;
            this.asteroidZArray = asteroidZ;
        }
    }
}
