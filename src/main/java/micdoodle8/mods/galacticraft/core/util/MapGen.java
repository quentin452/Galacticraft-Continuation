package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.world.gen.layer.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.*;
import java.lang.reflect.*;
import org.apache.commons.io.*;
import java.io.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.util.*;
import net.minecraft.util.*;

public class MapGen
{
    public boolean calculatingMap;
    private int ix;
    private int iz;
    private int biomeMapx0;
    private int biomeMapz0;
    private int biomeMapz00;
    private int biomeMapCx;
    private int biomeMapCz;
    private int biomeMapFactor;
    private WorldChunkManager biomeMapWCM;
    private static GenLayer biomeMapGenLayer;
    public File biomeMapFile;
    private byte[] biomeAndHeightArray;
    private int biomeMapSizeX;
    private int biomeMapSizeZ;
    private Random rand;
    private int[] heights;
    private double[] heighttemp;
    private WorldType field_147435_p;
    private BiomeGenBase[] biomesGrid;
    private BiomeGenBase[] biomesGridHeights;
    private int[] biomeCount;
    static double[] noiseField3;
    static double[] noiseField1;
    static double[] noiseField2;
    static double[] noiseField4;
    private NoiseGeneratorOctaves noiseGen1;
    private NoiseGeneratorOctaves noiseGen2;
    private NoiseGeneratorOctaves noiseGen3;
    public NoiseGeneratorOctaves noiseGen4;
    
    public MapGen(final World world, final int sx, final int sz, final int cx, final int cz, final int scale, final File file) {
        this.calculatingMap = false;
        this.ix = 0;
        this.iz = 0;
        this.biomeMapx0 = 0;
        this.biomeMapz0 = 0;
        this.biomeAndHeightArray = null;
        this.rand = new Random();
        this.heights = null;
        this.heighttemp = null;
        this.field_147435_p = WorldType.DEFAULT;
        this.biomesGrid = null;
        this.biomesGridHeights = null;
        this.biomeCount = null;
        this.biomeMapCx = cx >> 4;
        this.biomeMapCz = cz >> 4;
        if (file.exists()) {
            return;
        }
        this.biomeMapFile = file;
        this.calculatingMap = true;
        this.biomeMapSizeX = sx;
        this.biomeMapSizeZ = sz;
        this.biomeMapFactor = scale;
        final int limitX = this.biomeMapSizeX * this.biomeMapFactor / 32;
        final int limitZ = this.biomeMapSizeZ * this.biomeMapFactor / 32;
        this.biomeMapz00 = -limitZ;
        this.biomeMapx0 = -limitX;
        this.biomeMapz0 = this.biomeMapz00;
        this.ix = 0;
        this.iz = 0;
        this.biomeMapWCM = world.getWorldChunkManager();
        try {
            final Field bil = this.biomeMapWCM.getClass().getDeclaredField(VersionUtil.getNameDynamic("biomeIndexLayer"));
            bil.setAccessible(true);
            MapGen.biomeMapGenLayer = (GenLayer)bil.get(this.biomeMapWCM);
        }
        catch (Exception ex) {}
        if (MapGen.biomeMapGenLayer == null) {
            this.calculatingMap = false;
            GCLog.debug("Failed to get gen layer from World Chunk Manager.");
            return;
        }
        GCLog.debug("Starting map generation " + file.getName() + " top left " + (this.biomeMapCx - limitX) * 16 + "," + (this.biomeMapCz - limitZ) * 16);
        this.field_147435_p = world.getWorldInfo().getTerrainType();
        this.initialise(world.getSeed());
    }
    
    public void writeOutputFile(final boolean flag) {
        try {
            if (!this.biomeMapFile.exists() || (this.biomeMapFile.canWrite() && this.biomeMapFile.canRead())) {
                FileUtils.writeByteArrayToFile(this.biomeMapFile, this.biomeAndHeightArray);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        if (flag) {
            this.sendToClient(this.biomeAndHeightArray);
        }
        this.biomeAndHeightArray = null;
    }
    
    private void sendToClient(final byte[] toSend) {
        try {
            GalacticraftCore.packetPipeline.sendToAll((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE, new Object[] { this.biomeMapCx << 4, this.biomeMapCz << 4, toSend }));
        }
        catch (Exception ex) {
            System.err.println("Error sending map image to player.");
            ex.printStackTrace();
        }
    }
    
    public boolean BiomeMapOneTick() {
        final int limit = Math.min(this.biomeMapFactor, 16);
        if (this.biomeAndHeightArray == null) {
            this.biomeAndHeightArray = new byte[this.biomeMapSizeX * this.biomeMapSizeZ * 2];
            this.heights = new int[256];
            this.heighttemp = new double[825];
            this.biomeCount = new int[limit * limit];
        }
        int multifactor = this.biomeMapFactor >> 4;
        if (multifactor < 1) {
            multifactor = 1;
        }
        int imagefactor = 16 / this.biomeMapFactor;
        if (imagefactor < 1) {
            imagefactor = 1;
        }
        this.biomeMapOneChunk(this.biomeMapCx + this.biomeMapx0, this.biomeMapCz + this.biomeMapz0, this.ix, this.iz, this.biomeMapFactor, limit);
        this.biomeMapz0 += multifactor;
        this.iz += imagefactor;
        if (this.iz > this.biomeMapSizeZ - imagefactor) {
            this.iz = 0;
            if (this.ix % 25 == 8) {
                GCLog.debug("Finished map column " + this.ix + " at " + (this.biomeMapCx + this.biomeMapx0) + "," + (this.biomeMapCz + this.biomeMapz0));
            }
            this.ix += imagefactor;
            this.biomeMapz0 = this.biomeMapz00;
            this.biomeMapx0 += multifactor;
            if (this.biomeMapx0 > -this.biomeMapz00 * 4) {
                this.biomeMapx0 += this.biomeMapz00 * 8;
            }
            return this.ix > this.biomeMapSizeX - imagefactor;
        }
        return false;
    }
    
    private void biomeMapOneChunk(final int x0, final int z0, int ix, int iz, final int factor, final int limit) {
        this.biomesGrid = this.biomeMapWCM.getBiomeGenAt(this.biomesGrid, x0 << 4, z0 << 4, 16, 16, false);
        if (this.biomesGrid == null) {
            return;
        }
        this.getHeightMap(x0, z0);
        final int halfFactor = limit * limit / 2;
        final ArrayList<Integer> cols = new ArrayList<Integer>();
        for (int j = 0; j < this.biomeCount.length; ++j) {
            this.biomeCount[j] = 0;
        }
        for (int x = 0; x < 16; x += factor) {
            final int izstore = iz;
            for (int z = 0; z < 16; z += factor) {
                cols.clear();
                int maxcount = 0;
                int maxindex = -1;
                int biome = -1;
                int lastcol = -1;
                int idx = 0;
                int avgHeight = 0;
                int divisor = 0;
            Label_0333:
                for (int xx = 0; xx < limit; ++xx) {
                    final int hidx = (xx + x << 4) + z;
                    for (int zz = 0; zz < limit; ++zz) {
                        final int height = this.heights[hidx + zz];
                        avgHeight += height;
                        ++divisor;
                        final BiomeGenBase theBiome = this.biomesGrid[xx + x + (zz + z << 4)];
                        if (theBiome != null) {
                            biome = theBiome.biomeID;
                        }
                        else {
                            biome = 9;
                        }
                        if (biome != lastcol) {
                            idx = cols.indexOf(biome);
                            if (idx == -1) {
                                idx = cols.size();
                                cols.add(biome);
                            }
                            lastcol = biome;
                        }
                        final int[] biomeCount = this.biomeCount;
                        final int n = idx;
                        ++biomeCount[n];
                        if (this.biomeCount[idx] > maxcount) {
                            maxcount = this.biomeCount[idx];
                            maxindex = idx;
                            if (maxcount > halfFactor) {
                                break Label_0333;
                            }
                        }
                    }
                }
                for (int i = cols.size() - 1; i >= 0; --i) {
                    this.biomeCount[i] = 0;
                }
                final int arrayIndex = (ix * this.biomeMapSizeZ + iz) * 2;
                this.biomeAndHeightArray[arrayIndex] = (byte)(int)cols.get(maxindex);
                this.biomeAndHeightArray[arrayIndex + 1] = (byte)((avgHeight + (divisor + 1) / 2) / divisor);
                ++iz;
            }
            iz = izstore;
            ++ix;
        }
    }
    
    public void getHeightMap(final int cx, final int cz) {
        this.rand.setSeed(cx * 341873128712L + cz * 132897987541L);
        this.biomesGridHeights = this.biomeMapWCM.getBiomesForGeneration(this.biomesGridHeights, cx * 4 - 2, cz * 4 - 2, 10, 10);
        this.func_147423_a(cx * 4, 0, cz * 4);
        final double d0 = 0.125;
        final double d2 = 0.25;
        for (int xx = 0; xx < 4; ++xx) {
            final int xa = xx * 5;
            final int xb = xa + 5;
            for (int zz = 0; zz < 4; ++zz) {
                final int aa = (xa + zz) * 33;
                final int ab = aa + 33;
                final int ba = (xb + zz) * 33;
                final int bb = ba + 33;
                for (int yy = 2; yy < 18; ++yy) {
                    double d3 = this.heighttemp[aa + yy];
                    double d4 = this.heighttemp[ab + yy];
                    double d5 = this.heighttemp[ba + yy];
                    double d6 = this.heighttemp[bb + yy];
                    final double d7 = (this.heighttemp[aa + yy + 1] - d3) * 0.125;
                    final double d8 = (this.heighttemp[ab + yy + 1] - d4) * 0.125;
                    final double d9 = (this.heighttemp[ba + yy + 1] - d5) * 0.125;
                    final double d10 = (this.heighttemp[bb + yy + 1] - d6) * 0.125;
                    for (int y = 0; y < 8; ++y) {
                        double d11 = d3;
                        double d12 = d4;
                        final double d13 = (d5 - d3) * 0.25;
                        final double d14 = (d6 - d4) * 0.25;
                        final int truey = yy * 8 + y;
                        for (int x = 0; x < 4; ++x) {
                            final int idx = x + xx * 4 << 4 | zz * 4;
                            final double d15 = (d12 - d11) * 0.25;
                            double d16 = d11 - d15;
                            for (int z = 0; z < 4; ++z) {
                                if ((d16 += d15) > 0.0) {
                                    this.heights[idx + z] = truey;
                                }
                            }
                            d11 += d13;
                            d12 += d14;
                        }
                        d3 += d7;
                        d4 += d8;
                        d5 += d9;
                        d6 += d10;
                    }
                }
            }
        }
    }
    
    public void initialise(final long seed) {
        this.rand = new Random(seed);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 16);
    }
    
    private void func_147423_a(final int cx, final int cy, final int cz) {
        final double d0 = 684.412;
        final double d2 = 684.412;
        final double d3 = 512.0;
        final double d4 = 512.0;
        MapGen.noiseField4 = this.noiseGen4.generateNoiseOctaves(MapGen.noiseField4, cx, cz, 5, 5, 200.0, 200.0, 0.5);
        MapGen.noiseField3 = this.noiseGen3.generateNoiseOctaves(MapGen.noiseField3, cx, cy, cz, 5, 33, 5, 8.555150000000001, 4.277575000000001, 8.555150000000001);
        MapGen.noiseField1 = this.noiseGen1.generateNoiseOctaves(MapGen.noiseField1, cx, cy, cz, 5, 33, 5, 684.412, 684.412, 684.412);
        MapGen.noiseField2 = this.noiseGen2.generateNoiseOctaves(MapGen.noiseField2, cx, cy, cz, 5, 33, 5, 684.412, 684.412, 684.412);
        final boolean flag1 = false;
        final boolean flag2 = false;
        int l = 2;
        int i1 = 0;
        final double d5 = 8.5;
        final boolean amplified = this.field_147435_p == WorldType.AMPLIFIED;
        for (int xx = 0; xx < 5; ++xx) {
            for (int zz = 0; zz < 5; ++zz) {
                float f = 0.0f;
                float f2 = 0.0f;
                float f3 = 0.0f;
                final BiomeGenBase biomegenbase = this.biomesGridHeights[xx + 22 + zz * 10];
                for (int x = -2; x <= 2; ++x) {
                    final int baseIndex = xx + x + 22 + zz * 10;
                    for (int z = -2; z <= 2; ++z) {
                        final BiomeGenBase biomegenbase2 = this.biomesGridHeights[baseIndex + z * 10];
                        float f4 = biomegenbase2.rootHeight;
                        float f5 = biomegenbase2.heightVariation;
                        if (amplified && f4 > 0.0f) {
                            f4 += 1.0f + f4;
                            f5 = 1.0f + f5 * 4.0f;
                        }
                        float f6 = MapUtil.parabolicField[x + 12 + z * 5] / (f4 + 2.0f);
                        if (biomegenbase2.rootHeight > biomegenbase.rootHeight) {
                            f6 /= 2.0f;
                        }
                        f += f5 * f6;
                        f2 += f4 * f6;
                        f3 += f6;
                    }
                }
                f /= f3;
                f2 /= f3;
                f = f * 0.9f + 0.1f;
                f2 = f2 / 2.0f - 0.125f;
                double d6 = MapGen.noiseField4[i1] / 8000.0;
                if (d6 < 0.0) {
                    d6 = -d6 * 0.3;
                }
                d6 = d6 * 3.0 - 2.0;
                if (d6 < 0.0) {
                    d6 /= 2.0;
                    if (d6 < -1.0) {
                        d6 = -1.0;
                    }
                    d6 /= 1.4;
                    d6 /= 2.0;
                }
                else {
                    if (d6 > 1.0) {
                        d6 = 1.0;
                    }
                    d6 /= 8.0;
                }
                ++i1;
                double d7 = f2;
                final double d8 = f / 6.0;
                d7 += d6 * 0.2;
                d7 = d7 * 8.5 / 8.0;
                final double d9 = 8.5 + d7 * 4.0;
                for (int j2 = 2; j2 < 19; ++j2) {
                    double d10 = (j2 - d9) / d8;
                    if (d10 < 0.0) {
                        d10 *= 4.0;
                    }
                    final double d11 = MapGen.noiseField1[l] / 512.0;
                    final double d12 = MapGen.noiseField2[l] / 512.0;
                    final double d13 = (MapGen.noiseField3[l] / 10.0 + 1.0) / 2.0;
                    this.heighttemp[l] = MathHelper.denormalizeClamp(d11, d12, d13) - d10;
                    ++l;
                }
                l += 16;
            }
        }
    }
}
