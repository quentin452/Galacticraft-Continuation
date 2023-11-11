package micdoodle8.mods.galacticraft.api.prefab.world.gen;

import java.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.api.event.wgen.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.world.gen.feature.*;

public abstract class BiomeDecoratorSpace
{
    protected Random rand;
    protected int chunkX;
    protected int chunkZ;
    
    public void decorate(final World world, final Random random, final int chunkX, final int chunkZ) {
        if (this.getCurrentWorld() != null) {
            throw new RuntimeException("Already decorating!!");
        }
        this.setCurrentWorld(world);
        this.rand = random;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        MinecraftForge.EVENT_BUS.post((Event)new GCCoreEventPopulate.Pre(world, this.rand, this.chunkX, this.chunkZ));
        this.decorate();
        MinecraftForge.EVENT_BUS.post((Event)new GCCoreEventPopulate.Post(world, this.rand, this.chunkX, this.chunkZ));
        this.setCurrentWorld(null);
        this.rand = null;
    }
    
    protected abstract void setCurrentWorld(final World p0);
    
    protected abstract World getCurrentWorld();
    
    protected void generateOre(final int amountPerChunk, final WorldGenerator worldGenerator, final int minY, final int maxY) {
        final World currentWorld = this.getCurrentWorld();
        for (int var5 = 0; var5 < amountPerChunk; ++var5) {
            final int var6 = this.chunkX + this.rand.nextInt(16);
            final int var7 = this.rand.nextInt(maxY - minY) + minY;
            final int var8 = this.chunkZ + this.rand.nextInt(16);
            worldGenerator.generate(currentWorld, this.rand, var6, var7, var8);
        }
    }
    
    protected abstract void decorate();
}
