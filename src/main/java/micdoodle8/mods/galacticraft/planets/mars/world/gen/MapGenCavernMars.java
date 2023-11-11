package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.init.*;

public class MapGenCavernMars extends MapGenBaseMeta
{
    public void generate(final IChunkProvider par1IChunkProvider, final World par2World, final int par3, final int par4, final Block[] arrayOfIDs, final byte[] arrayOfMeta) {
        final int var6 = this.range;
        this.worldObj = par2World;
        this.rand.setSeed(par2World.getSeed());
        final long var7 = this.rand.nextLong();
        final long var8 = this.rand.nextLong();
        for (int var9 = par3 - var6; var9 <= par3 + var6; ++var9) {
            for (int var10 = par4 - var6; var10 <= par4 + var6; ++var10) {
                final long var11 = var9 * var7;
                final long var12 = var10 * var8;
                this.rand.setSeed(var11 ^ var12 ^ par2World.getSeed());
                this.recursiveGenerate(par2World, var9, var10, par3, par4, arrayOfIDs, arrayOfMeta);
            }
        }
    }
    
    protected void recursiveGenerate(final World par1World, final int xChunkCoord, final int zChunkCoord, final int origXChunkCoord, final int origZChunkCoord, final Block[] arrayOfIDs, final byte[] arrayOfMeta) {
        if (this.rand.nextInt(100) == 0) {
            final double xPos = xChunkCoord * 16 + this.rand.nextInt(16);
            final double yPos = 25.0;
            final double zPos = zChunkCoord * 16 + this.rand.nextInt(16);
            this.generateLargeCaveNode(this.rand.nextLong(), origXChunkCoord, origZChunkCoord, arrayOfIDs, arrayOfMeta, xPos, 25.0, zPos);
        }
    }
    
    protected void generateLargeCaveNode(final long par1, final int origXChunkCoord, final int origZChunkCoord, final Block[] arrayOfIDs, final byte[] arrayOfMeta, final double xPos, final double yPos, final double zPos) {
        this.generateCaveNode(par1, origXChunkCoord, origZChunkCoord, arrayOfIDs, arrayOfMeta, xPos, yPos, zPos, 1.0f + this.rand.nextFloat() * 6.0f, 10.0f, 10.0f, -1, -1, 0.2);
    }
    
    protected void generateCaveNode(final long par1, final int origXChunkCoord, final int origZChunkCoord, final Block[] arrayOfIDs, final byte[] arrayOfMeta, final double xPos, final double yPos, final double zPos, final float par12, float par13, float par14, int par15, int par16, final double heightMultiplier) {
        final double var19 = origXChunkCoord * 16 + 8;
        final double var20 = origZChunkCoord * 16 + 8;
        float var21 = 0.0f;
        float var22 = 0.0f;
        final Random var23 = new Random(par1);
        if (par16 <= 0) {
            final int var24 = this.range * 16 - 16;
            par16 = var24 - var23.nextInt(var24 / 4);
        }
        boolean var25 = false;
        if (par15 == -1) {
            par15 = par16 / 2;
            var25 = true;
        }
        final int var26 = var23.nextInt(par16 / 2) + par16 / 4;
        final boolean var27 = var23.nextInt(6) == 0;
        while (par15 < par16) {
            final double caveWidth = 40.0;
            final double caveHeight = 40.0 * heightMultiplier;
            if (var27) {
                par14 *= 0.92f;
            }
            else {
                par14 *= 0.7f;
            }
            par14 += var22 * 0.1f;
            par13 += var21 * 0.1f;
            var22 *= 0.9f;
            var21 *= 0.75f;
            var22 += (var23.nextFloat() - var23.nextFloat()) * var23.nextFloat() * 2.0f;
            var21 += (var23.nextFloat() - var23.nextFloat()) * var23.nextFloat() * 4.0f;
            if (!var25 && par15 == var26 && par12 > 1.0f && par16 > 0) {
                return;
            }
            if (var25 || var23.nextInt(4) != 0) {
                final double var28 = xPos - var19;
                final double var29 = zPos - var20;
                final double var30 = par16 - par15;
                final double var31 = par12 + 2.0f + 16.0f;
                if (var28 * var28 + var29 * var29 - var30 * var30 > var31 * var31) {
                    return;
                }
                if (xPos >= var19 - 16.0 - 80.0 && zPos >= var20 - 16.0 - 80.0 && xPos <= var19 + 16.0 + 80.0 && zPos <= var20 + 16.0 + 80.0) {
                    int caveMinX = MathHelper.floor_double(xPos - 40.0) - origXChunkCoord * 16 - 1;
                    int caveMaxX = MathHelper.floor_double(xPos + 40.0) - origXChunkCoord * 16 + 1;
                    int caveMinY = MathHelper.floor_double(yPos - caveHeight) - 1;
                    int caveMaxY = MathHelper.floor_double(yPos + caveHeight) + 1;
                    int caveMinZ = MathHelper.floor_double(zPos - 40.0) - origZChunkCoord * 16 - 1;
                    int caveMaxZ = MathHelper.floor_double(zPos + 40.0) - origZChunkCoord * 16 + 1;
                    if (caveMinX < 0) {
                        caveMinX = 0;
                    }
                    if (caveMaxX > 16) {
                        caveMaxX = 16;
                    }
                    if (caveMinY < 1) {
                        caveMinY = 1;
                    }
                    if (caveMaxY > 65) {
                        caveMaxY = 65;
                    }
                    if (caveMinZ < 0) {
                        caveMinZ = 0;
                    }
                    if (caveMaxZ > 16) {
                        caveMaxZ = 16;
                    }
                    final boolean isBlockWater = false;
                    for (int var32 = caveMinX; !isBlockWater && var32 < caveMaxX; ++var32) {
                        for (int var33 = caveMinZ; !isBlockWater && var33 < caveMaxZ; ++var33) {
                            for (int var34 = caveMaxY + 1; !isBlockWater && var34 >= caveMinY - 1; --var34) {
                                final int var35 = (var32 * 16 + var33) * 128 + var34;
                                if (var34 >= 0 && var34 < 128 && var34 != caveMinY - 1 && var32 != caveMinX && var32 != caveMaxX - 1 && var33 != caveMinZ && var33 != caveMaxZ - 1) {
                                    var34 = caveMinY;
                                }
                            }
                        }
                    }
                    for (int var32 = caveMinX; var32 < caveMaxX; ++var32) {
                        final double var36 = (var32 + origXChunkCoord * 16 + 0.5 - xPos) / 40.0;
                        for (int var35 = caveMinZ; var35 < caveMaxZ; ++var35) {
                            final double var37 = (var35 + origZChunkCoord * 16 + 0.5 - zPos) / 40.0;
                            if (var36 * var36 + var37 * var37 < 1.0) {
                                for (int var38 = caveMaxY - 1; var38 >= caveMinY; --var38) {
                                    final double var39 = (var38 + 0.5 - yPos) / caveHeight;
                                    if (var36 * var36 + var39 * var39 + var37 * var37 < 1.0 && var39 > -0.7) {
                                        final int coords = (var32 * 16 + var35) * 256 + var38;
                                        if (arrayOfIDs[coords] == MarsBlocks.marsBlock || arrayOfIDs[coords] == MarsBlocks.blockSludge || arrayOfIDs[coords] == MarsBlocks.vine) {
                                            arrayOfIDs[coords] = Blocks.air;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (int var32 = caveMinX; var32 < caveMaxX; ++var32) {
                        final double var36 = (var32 + origXChunkCoord * 16 + 0.5 - xPos) / 40.0;
                        for (int var35 = caveMinZ; var35 < caveMaxZ; ++var35) {
                            final double var37 = (var35 + origZChunkCoord * 16 + 0.5 - zPos) / 40.0;
                            if (var36 * var36 + var37 * var37 < 1.0) {
                                for (int var38 = caveMaxY - 1; var38 >= caveMinY; --var38) {
                                    final double var39 = (var38 + 0.5 - yPos) / caveHeight;
                                    if (var36 * var36 + var39 * var39 + var37 * var37 < 1.0 && var39 > -0.7) {
                                        final int coords = (var32 * 16 + var35) * 256 + var38;
                                        final int coordsAbove = (var32 * 16 + var35) * 256 + var38 + 1;
                                        int coordsBelow = (var32 * 16 + var35) * 256 + var38 - 1;
                                        if (Blocks.air == arrayOfIDs[coords]) {
                                            if (arrayOfIDs[coordsAbove] == MarsBlocks.marsBlock && this.rand.nextInt(200) == 0) {
                                                for (int modifier = 0; Blocks.air == arrayOfIDs[coordsBelow]; coordsBelow = (var32 * 16 + var35) * 256 + var38 - 1 + modifier) {
                                                    arrayOfIDs[coordsBelow] = MarsBlocks.vine;
                                                    arrayOfMeta[coordsBelow] = (byte)(Math.abs(modifier) % 3);
                                                    --modifier;
                                                }
                                            }
                                            else if (arrayOfIDs[coordsBelow] == MarsBlocks.marsBlock && this.rand.nextInt(200) == 0) {
                                                arrayOfIDs[coords] = MarsBlocks.blockSludge;
                                                arrayOfMeta[coords] = 0;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ++par15;
        }
    }
}
