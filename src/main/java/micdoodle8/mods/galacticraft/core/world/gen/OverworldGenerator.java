package micdoodle8.mods.galacticraft.core.world.gen;

import cpw.mods.fml.common.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.util.*;
import net.minecraft.init.*;

public class OverworldGenerator implements IWorldGenerator
{
    private final int amountPerChunk;
    private final int maxGenerateLevel;
    private final int minGenerateLevel;
    private final int amountPerVein;
    private final Block oreBlock;
    private final int metadata;
    
    public OverworldGenerator(final Block oreBlock, final int metadata, final int amountPerChunk, final int minGenLevel, final int maxGenLevel, final int amountPerVein) {
        this.oreBlock = oreBlock;
        this.metadata = metadata;
        this.amountPerChunk = amountPerChunk;
        this.minGenerateLevel = minGenLevel;
        this.maxGenerateLevel = maxGenLevel;
        this.amountPerVein = amountPerVein;
    }
    
    public void generate(final Random random, final int chunkX, final int chunkZ, final World world, final IChunkProvider chunkGenerator, final IChunkProvider chunkProvider) {
        if (!(world.provider instanceof IGalacticraftWorldProvider)) {
            for (int i = 0; i < this.amountPerChunk; ++i) {
                final int x = chunkX * 16 + random.nextInt(16);
                final int z = chunkZ * 16 + random.nextInt(16);
                final int y = random.nextInt(Math.max(this.maxGenerateLevel - this.minGenerateLevel, 0)) + this.minGenerateLevel;
                this.generateOre(world, random, x, y, z);
            }
        }
    }
    
    private boolean generateOre(final World par1World, final Random par2Random, final int par3, final int par4, final int par5) {
        final float var6 = par2Random.nextFloat() * 3.1415927f;
        final double var7 = par3 + 8 + MathHelper.sin(var6) * this.amountPerVein / 8.0f;
        final double var8 = par3 + 8 - MathHelper.sin(var6) * this.amountPerVein / 8.0f;
        final double var9 = par5 + 8 + MathHelper.cos(var6) * this.amountPerVein / 8.0f;
        final double var10 = par5 + 8 - MathHelper.cos(var6) * this.amountPerVein / 8.0f;
        final double var11 = par4 + par2Random.nextInt(3) - 2;
        final double var12 = par4 + par2Random.nextInt(3) - 2;
        for (int var13 = 0; var13 <= this.amountPerVein; ++var13) {
            final double var14 = var7 + (var8 - var7) * var13 / this.amountPerVein;
            final double var15 = var11 + (var12 - var11) * var13 / this.amountPerVein;
            final double var16 = var9 + (var10 - var9) * var13 / this.amountPerVein;
            final double var17 = par2Random.nextDouble() * this.amountPerVein / 16.0;
            final double var18 = (MathHelper.sin(var13 * 3.1415927f / this.amountPerVein) + 1.0f) * var17 + 1.0;
            final double var19 = (MathHelper.sin(var13 * 3.1415927f / this.amountPerVein) + 1.0f) * var17 + 1.0;
            final int var20 = MathHelper.floor_double(var14 - var18 / 2.0);
            final int var21 = MathHelper.floor_double(var15 - var19 / 2.0);
            final int var22 = MathHelper.floor_double(var16 - var18 / 2.0);
            final int var23 = MathHelper.floor_double(var14 + var18 / 2.0);
            final int var24 = MathHelper.floor_double(var15 + var19 / 2.0);
            final int var25 = MathHelper.floor_double(var16 + var18 / 2.0);
            for (int var26 = var20; var26 <= var23; ++var26) {
                final double var27 = (var26 + 0.5 - var14) / (var18 / 2.0);
                if (var27 * var27 < 1.0) {
                    for (int var28 = var21; var28 <= var24; ++var28) {
                        final double var29 = (var28 + 0.5 - var15) / (var19 / 2.0);
                        if (var27 * var27 + var29 * var29 < 1.0) {
                            for (int var30 = var22; var30 <= var25; ++var30) {
                                final double var31 = (var30 + 0.5 - var16) / (var18 / 2.0);
                                final Block block = par1World.getBlock(var26, var28, var30);
                                if (var27 * var27 + var29 * var29 + var31 * var31 < 1.0 && block.isReplaceableOreGen(par1World, var26, var28, var30, Blocks.stone)) {
                                    par1World.setBlock(var26, var28, var30, this.oreBlock, this.metadata, 2);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
