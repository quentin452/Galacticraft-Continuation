package micdoodle8.mods.galacticraft.api.prefab.world.gen;

import net.minecraft.world.biome.*;
import net.minecraft.world.gen.layer.*;
import java.util.*;
import net.minecraft.world.*;

public abstract class WorldChunkManagerSpace extends WorldChunkManager
{
    private final BiomeCache biomeCache;
    private final List<BiomeGenBase> biomesToSpawnIn;
    
    public WorldChunkManagerSpace() {
        this.biomeCache = new BiomeCache((WorldChunkManager)this);
        (this.biomesToSpawnIn = new ArrayList<BiomeGenBase>()).add(this.getBiome());
    }
    
    public List<BiomeGenBase> getBiomesToSpawnIn() {
        return this.biomesToSpawnIn;
    }
    
    public BiomeGenBase getBiomeGenAt(final int par1, final int par2) {
        return this.getBiome();
    }
    
    public float[] getRainfall(float[] par1ArrayOfFloat, final int par2, final int par3, final int par4, final int par5) {
        if (par1ArrayOfFloat == null || par1ArrayOfFloat.length < par4 * par5) {
            par1ArrayOfFloat = new float[par4 * par5];
        }
        Arrays.fill(par1ArrayOfFloat, 0, par4 * par5, 0.0f);
        return par1ArrayOfFloat;
    }
    
    public float getTemperatureAtHeight(final float par1, final int par2) {
        return par1;
    }
    
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] par1ArrayOfBiomeGenBase, final int par2, final int par3, final int par4, final int par5) {
        IntCache.resetIntCache();
        if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5) {
            par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
        }
        for (int var7 = 0; var7 < par4 * par5; ++var7) {
            par1ArrayOfBiomeGenBase[var7] = this.getBiome();
        }
        return par1ArrayOfBiomeGenBase;
    }
    
    public BiomeGenBase[] loadBlockGeneratorData(final BiomeGenBase[] par1ArrayOfBiomeGenBase, final int par2, final int par3, final int par4, final int par5) {
        return this.getBiomeGenAt(par1ArrayOfBiomeGenBase, par2, par3, par4, par5, true);
    }
    
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] par1ArrayOfBiomeGenBase, final int par2, final int par3, final int par4, final int par5, final boolean par6) {
        IntCache.resetIntCache();
        if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5) {
            par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
        }
        if (par6 && par4 == 16 && par5 == 16 && (par2 & 0xF) == 0x0 && (par3 & 0xF) == 0x0) {
            final BiomeGenBase[] var9 = this.biomeCache.getCachedBiomes(par2, par3);
            System.arraycopy(var9, 0, par1ArrayOfBiomeGenBase, 0, par4 * par5);
            return par1ArrayOfBiomeGenBase;
        }
        for (int var10 = 0; var10 < par4 * par5; ++var10) {
            par1ArrayOfBiomeGenBase[var10] = this.getBiome();
        }
        return par1ArrayOfBiomeGenBase;
    }
    
    public boolean areBiomesViable(final int par1, final int par2, final int par3, final List par4List) {
        return par4List.contains(this.getBiome());
    }
    
    public ChunkPosition findBiomePosition(final int par1, final int par2, final int par3, final List par4List, final Random par5Random) {
        final int var6 = par1 - par3 >> 2;
        final int var7 = par2 - par3 >> 2;
        final int var8 = par1 + par3 >> 2;
        final int var9 = var8 - var6 + 1;
        final int var10 = var6 + 0 % var9 << 2;
        final int var11 = var7 + 0 / var9 << 2;
        return new ChunkPosition(var10, 0, var11);
    }
    
    public void cleanupCache() {
        this.biomeCache.cleanupCache();
    }
    
    public abstract BiomeGenBase getBiome();
}
