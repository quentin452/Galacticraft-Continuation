package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.*;
import java.util.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraftforge.common.*;
import micdoodle8.mods.galacticraft.api.event.wgen.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BiomeDecoratorMoon
{
    private World worldObj;
    private Random randomGenerator;
    private int chunkX;
    private int chunkZ;
    private WorldGenerator dirtGen;
    private WorldGenerator cheeseGen;
    private WorldGenerator copperGen;
    private WorldGenerator tinGen;
    
    public BiomeDecoratorMoon(final BiomeGenBase par1BiomeGenBase) {
        this.copperGen = (WorldGenerator)new WorldGenMinableMeta(GCBlocks.blockMoon, 4, 0, true, GCBlocks.blockMoon, 4);
        this.tinGen = (WorldGenerator)new WorldGenMinableMeta(GCBlocks.blockMoon, 4, 1, true, GCBlocks.blockMoon, 4);
        this.cheeseGen = (WorldGenerator)new WorldGenMinableMeta(GCBlocks.blockMoon, 3, 2, true, GCBlocks.blockMoon, 4);
        this.dirtGen = (WorldGenerator)new WorldGenMinableMeta(GCBlocks.blockMoon, 32, 3, true, GCBlocks.blockMoon, 4);
    }
    
    public void decorate(final World worldObj, final Random rand, final int chunkX, final int chunkZ) {
        if (this.worldObj != null) {
            throw new RuntimeException("Already decorating!!");
        }
        this.worldObj = worldObj;
        this.randomGenerator = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.generateMoon();
        this.worldObj = null;
        this.randomGenerator = null;
    }
    
    void genStandardOre1(final int amountPerChunk, final WorldGenerator worldGenerator, final int minY, final int maxY) {
        for (int var5 = 0; var5 < amountPerChunk; ++var5) {
            final int var6 = this.chunkX + this.randomGenerator.nextInt(16);
            final int var7 = this.randomGenerator.nextInt(maxY - minY) + minY;
            final int var8 = this.chunkZ + this.randomGenerator.nextInt(16);
            worldGenerator.generate(this.worldObj, this.randomGenerator, var6, var7, var8);
        }
    }
    
    void generateMoon() {
        MinecraftForge.EVENT_BUS.post((Event)new GCCoreEventPopulate.Pre(this.worldObj, this.randomGenerator, this.chunkX, this.chunkZ));
        this.genStandardOre1(20, this.dirtGen, 0, 200);
        if (!ConfigManagerCore.disableCopperMoon) {
            this.genStandardOre1(26, this.copperGen, 0, 60);
        }
        if (!ConfigManagerCore.disableTinMoon) {
            this.genStandardOre1(23, this.tinGen, 0, 60);
        }
        if (!ConfigManagerCore.disableCheeseMoon) {
            this.genStandardOre1(12, this.cheeseGen, 0, 128);
        }
        MinecraftForge.EVENT_BUS.post((Event)new GCCoreEventPopulate.Post(this.worldObj, this.randomGenerator, this.chunkX, this.chunkZ));
    }
}
