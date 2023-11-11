package micdoodle8.mods.galacticraft.api.prefab.world.gen;

import java.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.*;

public abstract class MapGenBaseMeta
{
    protected int range;
    protected Random rand;
    protected World worldObj;
    
    public MapGenBaseMeta() {
        this.range = 8;
        this.rand = new Random();
    }
    
    public void generate(final IChunkProvider par1IChunkProvider, final World world, final int chunkX, final int chunkZ, final Block[] blocks, final byte[] metadata) {
        this.worldObj = world;
        this.rand.setSeed(world.getSeed());
        final long r0 = this.rand.nextLong();
        final long r2 = this.rand.nextLong();
        for (int x0 = chunkX - this.range; x0 <= chunkX + this.range; ++x0) {
            for (int y0 = chunkZ - this.range; y0 <= chunkZ + this.range; ++y0) {
                final long randX = x0 * r0;
                final long randZ = y0 * r2;
                this.rand.setSeed(randX ^ randZ ^ world.getSeed());
                this.recursiveGenerate(world, x0, y0, chunkX, chunkZ, blocks, metadata);
            }
        }
    }
    
    protected void recursiveGenerate(final World world, final int xChunkCoord, final int zChunkCoord, final int origXChunkCoord, final int origZChunkCoord, final Block[] blocks, final byte[] metadata) {
    }
}
