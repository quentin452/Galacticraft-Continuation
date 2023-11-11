package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import net.minecraft.world.gen.feature.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.util.*;

public class WorldGenTerraformTree extends WorldGenerator
{
    private final int minTreeHeight;
    private final boolean vinesGrow;
    private final int metaWood;
    private final int metaLeaves;
    
    public WorldGenTerraformTree(final boolean par1, final ItemStack sapling) {
        this(par1, 4, 0, 0, false);
    }
    
    public WorldGenTerraformTree(final boolean par1, final int par2, final int par3, final int par4, final boolean vines) {
        super(par1);
        this.minTreeHeight = par2;
        this.metaWood = par3;
        this.metaLeaves = par4;
        this.vinesGrow = vines;
    }
    
    public boolean generate(final World par1World, final Random par2Random, final int x, final int y, final int z) {
        final int l = par2Random.nextInt(3) + this.minTreeHeight;
        boolean flag = true;
        if (y < 1 || y + l + 1 > 256) {
            return false;
        }
        for (int i1 = y; i1 <= y + 1 + l; ++i1) {
            byte b0 = 1;
            if (i1 == y) {
                b0 = 0;
            }
            if (i1 >= y + 1 + l - 2) {
                b0 = 2;
            }
            b0 += 5;
            for (int l2 = x - b0; l2 <= x + b0 && flag; ++l2) {
                for (int j1 = z - b0; j1 <= z + b0 && flag; ++j1) {
                    if (i1 >= 0 && i1 < 256) {
                        final Block k1b = par1World.getBlock(l2, i1, j1);
                        final int k2 = par1World.getBlockMetadata(l2, i1, j1);
                        final boolean isAir = par1World.isAirBlock(l2, i1, j1);
                        if (!isAir && k1b != Blocks.grass && k1b != Blocks.water && k1b != Blocks.flowing_water && k1b != MarsBlocks.marsBlock && k2 != 5) {
                            flag = false;
                        }
                    }
                    else {
                        flag = false;
                    }
                }
            }
        }
        if (!flag) {
            return false;
        }
        if (y < 256 - l - 1) {
            final byte b0 = 3;
            final byte b2 = 0;
            for (int j1 = y - b0 + l; j1 <= y + l; ++j1) {
                final int k3 = j1 - (y + l);
                for (int i2 = b2 + 1 - k3 / 2, j2 = x - i2; j2 <= x + i2; ++j2) {
                    final int k2 = j2 - x;
                    for (int l3 = z - i2; l3 <= z + i2; ++l3) {
                        final int i3 = l3 - z;
                        if (Math.abs(k2) != i2 || Math.abs(i3) != i2 || (par2Random.nextInt(2) != 0 && k3 != 0)) {
                            final Block block = par1World.getBlock(j2, j1, l3);
                            if (block == null || block.canBeReplacedByLeaves((IBlockAccess)par1World, j2, j1, l3)) {
                                this.setBlockAndNotifyAdequately(par1World, j2, j1, l3, (Block)Blocks.leaves, this.metaLeaves);
                            }
                        }
                    }
                }
            }
            for (int j1 = 0; j1 < l; ++j1) {
                final Block block2 = par1World.getBlock(x, y + j1, z);
                if (block2 != null) {
                    if (block2.isAir((IBlockAccess)par1World, x, y + j1, z) || block2.isLeaves((IBlockAccess)par1World, x, y + j1, z)) {
                        this.setBlockAndNotifyAdequately(par1World, x, y + j1, z, Blocks.log, this.metaWood);
                        if (this.vinesGrow && j1 > 0) {
                            if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(x - 1, y + j1, z)) {
                                this.setBlockAndNotifyAdequately(par1World, x - 1, y + j1, z, Blocks.vine, 8);
                            }
                            if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(x + 1, y + j1, z)) {
                                this.setBlockAndNotifyAdequately(par1World, x + 1, y + j1, z, Blocks.vine, 2);
                            }
                            if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(x, y + j1, z - 1)) {
                                this.setBlockAndNotifyAdequately(par1World, x, y + j1, z - 1, Blocks.vine, 1);
                            }
                            if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(x, y + j1, z + 1)) {
                                this.setBlockAndNotifyAdequately(par1World, x, y + j1, z + 1, Blocks.vine, 4);
                            }
                        }
                    }
                }
            }
            if (this.vinesGrow) {
                for (int j1 = y - 3 + l; j1 <= y + l; ++j1) {
                    final int k3 = j1 - (y + l);
                    for (int i2 = 2 - k3 / 2, j2 = x - i2; j2 <= x + i2; ++j2) {
                        for (int k2 = z - i2; k2 <= z + i2; ++k2) {
                            final Block block2 = par1World.getBlock(j2, j1, k2);
                            if (block2 != null && block2.isLeaves((IBlockAccess)par1World, j2, j1, k2)) {
                                if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(j2 - 1, j1, k2)) {
                                    this.growVines(par1World, j2 - 1, j1, k2, 8);
                                }
                                if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(j2 + 1, j1, k2)) {
                                    this.growVines(par1World, j2 + 1, j1, k2, 2);
                                }
                                if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(j2, j1, k2 - 1)) {
                                    this.growVines(par1World, j2, j1, k2 - 1, 1);
                                }
                                if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(j2, j1, k2 + 1)) {
                                    this.growVines(par1World, j2, j1, k2 + 1, 4);
                                }
                            }
                        }
                    }
                }
                if (par2Random.nextInt(5) == 0 && l > 5) {
                    for (int j1 = 0; j1 < 2; ++j1) {
                        for (int k3 = 0; k3 < 4; ++k3) {
                            if (par2Random.nextInt(4 - j1) == 0) {
                                final int i2 = par2Random.nextInt(3);
                                this.setBlockAndNotifyAdequately(par1World, x + Direction.offsetX[Direction.rotateOpposite[k3]], y + l - 5 + j1, z + Direction.offsetZ[Direction.rotateOpposite[k3]], Blocks.cocoa, i2 << 2 | k3);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private void growVines(final World par1World, final int x, int y, final int z, final int meta) {
        this.setBlockAndNotifyAdequately(par1World, x, y, z, Blocks.vine, meta);
        int i1 = 4;
        while (true) {
            --y;
            if (!par1World.isAirBlock(x, y, z) || i1 <= 0) {
                break;
            }
            this.setBlockAndNotifyAdequately(par1World, x, y, z, Blocks.vine, meta);
            --i1;
        }
    }
}
