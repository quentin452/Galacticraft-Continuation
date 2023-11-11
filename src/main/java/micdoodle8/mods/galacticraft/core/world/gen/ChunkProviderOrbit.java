package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.gen.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.*;
import java.util.*;

public class ChunkProviderOrbit extends ChunkProviderGenerate
{
    private final Random rand;
    private final World worldObj;
    
    public ChunkProviderOrbit(final World par1World, final long par2, final boolean par4) {
        super(par1World, par2, par4);
        this.rand = new Random(par2);
        this.worldObj = par1World;
    }
    
    public boolean unloadQueuedChunks() {
        return false;
    }
    
    public int getLoadedChunkCount() {
        return 0;
    }
    
    public boolean saveChunks(final boolean var1, final IProgressUpdate var2) {
        return true;
    }
    
    public boolean canSave() {
        return true;
    }
    
    public Chunk provideChunk(final int par1, final int par2) {
        this.rand.setSeed(par1 * 341873128712L + par2 * 132897987541L);
        final Block[] ids = new Block[32768];
        Arrays.fill(ids, Blocks.air);
        final byte[] meta = new byte[32768];
        final Chunk var4 = new Chunk(this.worldObj, ids, meta, par1, par2);
        final byte[] biomesArray = var4.getBiomeArray();
        for (int i = 0; i < biomesArray.length; ++i) {
            biomesArray[i] = (byte)BiomeGenBaseOrbit.space.biomeID;
        }
        var4.generateSkylightMap();
        return var4;
    }
    
    public boolean chunkExists(final int par1, final int par2) {
        return true;
    }
    
    public void populate(final IChunkProvider par1IChunkProvider, final int par2, final int par3) {
        BlockFalling.fallInstantly = true;
        final int k = par2 * 16;
        final int l = par3 * 16;
        this.rand.setSeed(this.worldObj.getSeed());
        final long i1 = this.rand.nextLong() / 2L * 2L + 1L;
        final long j1 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(par2 * i1 + par3 * j1 ^ this.worldObj.getSeed());
        if (k == 0 && l == 0) {
            this.worldObj.setBlock(k, 64, l, GCBlocks.spaceStationBase, 0, 3);
            final TileEntity var8 = this.worldObj.getTileEntity(k, 64, l);
            if (var8 instanceof IMultiBlock) {
                ((IMultiBlock)var8).onCreate(new BlockVec3(k, 64, l));
            }
            new WorldGenSpaceStation().generate(this.worldObj, this.rand, k - 10, 62, l - 3);
        }
        BlockFalling.fallInstantly = false;
    }
    
    public String makeString() {
        return "OrbitLevelSource";
    }
    
    public List<?> getPossibleCreatures(final EnumCreatureType par1EnumCreatureType, final int i, final int j, final int k) {
        return null;
    }
}
