package micdoodle8.mods.galacticraft.core.blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.block.IOxygenReliantBlock;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;

public class BlockUnlitTorch extends Block implements IOxygenReliantBlock {

    public boolean lit;
    public Block litVersion;
    public Block unlitVersion;
    public Block fallback;

    protected BlockUnlitTorch(boolean lit, String assetName) {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.lit = lit;
        this.setLightLevel(lit ? 0.9375F : 0.2F);
        this.setHardness(0.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }

    public static void register(BlockUnlitTorch unlittorch, BlockUnlitTorch littorch, Block vanillatorch) {
        littorch.litVersion = littorch;
        littorch.unlitVersion = unlittorch;
        littorch.fallback = vanillatorch;
        unlittorch.litVersion = littorch;
        unlittorch.unlitVersion = unlittorch;
        unlittorch.fallback = vanillatorch;
        GalacticraftCore.handler.registerTorchType(littorch, vanillatorch);
    }

    public Block changeState() {
        if (this.lit) {
            return this.litVersion;
        }
        return this.unlitVersion;
    }

    private static boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection direction) {
        return world.getBlock(x, y, z)
            .isSideSolid(world, x, y, z, direction);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender(this);
    }

    private boolean canPlaceTorchOn(World par1World, int par2, int par3, int par4) {
        if (World.doesBlockHaveSolidTopSurface(par1World, par2, par3, par4)) {
            return true;
        }
        final Block var5 = par1World.getBlock(par2, par3, par4);
        return var5.canPlaceTorchOnTop(par1World, par2, par3, par4);
    }

    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST)
            || BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST)
            || BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH)
            || BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH)
            || this.canPlaceTorchOn(par1World, par2, par3 - 1, par4);
    }

    @Override
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7,
        float par8, int par9) {
        int var10 = par9;

        if (par5 == 1 && this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
            var10 = 5;
        }

        if (par5 == 2 && BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH)) {
            var10 = 4;
        }

        if (par5 == 3 && BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
            var10 = 3;
        }

        if (par5 == 4 && BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST)) {
            var10 = 2;
        }

        if (par5 == 5 && BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST)) {
            var10 = 1;
        }

        return var10;
    }

    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            this.onBlockAdded(par1World, par2, par3, par4);
        } else {
            this.checkOxygen(par1World, par2, par3, par4);
        }
    }

    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            if (BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
            } else if (BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
            } else if (BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
            } else if (BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
            } else if (this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
            }
        }

        if (this.dropTorchIfCantStay(par1World, par2, par3, par4)) {
            this.checkOxygen(par1World, par2, par3, par4);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
        if (this.dropTorchIfCantStay(par1World, par2, par3, par4)) {
            final int var6 = par1World.getBlockMetadata(par2, par3, par4);
            boolean var7 = false;

            if (!BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST)
                && var6 == 1) {
                var7 = true;
            }

            if (!BlockUnlitTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST)
                && var6 == 2) {
                var7 = true;
            }

            if (!BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH)
                && var6 == 3) {
                var7 = true;
            }

            if (!BlockUnlitTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH)
                && var6 == 4) {
                var7 = true;
            }

            if (!this.canPlaceTorchOn(par1World, par2, par3 - 1, par4) && var6 == 5) {
                var7 = true;
            }

            if (var7) {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlock(par2, par3, par4, Blocks.air);
            } else {
                this.checkOxygen(par1World, par2, par3, par4);
            }
        }
    }

    private void checkOxygen(World world, int x, int y, int z) {
        if (world.provider instanceof IGalacticraftWorldProvider) {
            if (OxygenUtil.checkTorchHasOxygen(world, this, x, y, z)) {
                this.onOxygenAdded(world, x, y, z);
            } else {
                this.onOxygenRemoved(world, x, y, z);
            }
        } else {
            world.setBlock(x, y, z, this.fallback, world.getBlockMetadata(x, y, z), 2);
        }
    }

    /**
     * Tests if the block can remain at its current location and will drop as an item if it is unable to stay. Returns
     * True if it can stay and False if it drops. Args: world, x, y, z
     */
    private boolean dropTorchIfCantStay(World par1World, int par2, int par3, int par4) {
        if (this.canPlaceBlockAt(par1World, par2, par3, par4)) {
            return true;
        }
        if (par1World.getBlock(par2, par3, par4) == this) {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlock(par2, par3, par4, Blocks.air);
        }

        return false;
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    @Override
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3,
        Vec3 par6Vec3) {
        final int var7 = par1World.getBlockMetadata(par2, par3, par4) & 7;
        float var8 = 0.15F;

        switch (var7) {
            case 1:
                this.setBlockBounds(0.0F, 0.2F, 0.5F - var8, var8 * 2.0F, 0.8F, 0.5F + var8);
                break;
            case 2:
                this.setBlockBounds(1.0F - var8 * 2.0F, 0.2F, 0.5F - var8, 1.0F, 0.8F, 0.5F + var8);
                break;
            case 3:
                this.setBlockBounds(0.5F - var8, 0.2F, 0.0F, 0.5F + var8, 0.8F, var8 * 2.0F);
                break;
            case 4:
                this.setBlockBounds(0.5F - var8, 0.2F, 1.0F - var8 * 2.0F, 0.5F + var8, 0.8F, 1.0F);
                break;
            default:
                var8 = 0.1F;
                this.setBlockBounds(0.5F - var8, 0.0F, 0.5F - var8, 0.5F + var8, 0.6F, 0.5F + var8);
                break;
        }

        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        final boolean doSmoke = par5Random.nextInt(5) == 0;
        if (this.lit || doSmoke) {
            final int var6 = par1World.getBlockMetadata(par2, par3, par4);
            final double var7 = par2 + 0.5F;
            final double var9 = par3 + 0.7F;
            final double var11 = par4 + 0.5F;
            final double var13 = 0.2199999988079071D;
            final double var15 = 0.27000001072883606D;

            switch (var6) {
                case 1:
                    if (doSmoke) {
                        par1World.spawnParticle("smoke", var7 - var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
                    }
                    if (this.lit) {
                        par1World.spawnParticle("flame", var7 - var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
                    }
                    break;
                case 2:
                    if (doSmoke) {
                        par1World.spawnParticle("smoke", var7 + var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
                    }
                    if (this.lit) {
                        par1World.spawnParticle("flame", var7 + var15, var9 + var13, var11, 0.0D, 0.0D, 0.0D);
                    }
                    break;
                case 3:
                    if (doSmoke) {
                        par1World.spawnParticle("smoke", var7, var9 + var13, var11 - var15, 0.0D, 0.0D, 0.0D);
                    }
                    if (this.lit) {
                        par1World.spawnParticle("flame", var7, var9 + var13, var11 - var15, 0.0D, 0.0D, 0.0D);
                    }
                    break;
                case 4:
                    if (doSmoke) {
                        par1World.spawnParticle("smoke", var7, var9 + var13, var11 + var15, 0.0D, 0.0D, 0.0D);
                    }
                    if (this.lit) {
                        par1World.spawnParticle("flame", var7, var9 + var13, var11 + var15, 0.0D, 0.0D, 0.0D);
                    }
                    break;
                default:
                    if (doSmoke) {
                        par1World.spawnParticle("smoke", var7, var9, var11, 0.0D, 0.0D, 0.0D);
                    }
                    if (this.lit) {
                        par1World.spawnParticle("flame", var7, var9, var11, 0.0D, 0.0D, 0.0D);
                    }
                    break;
            }
        }
    }

    @Override
    public void onOxygenRemoved(World world, int x, int y, int z) {
        if (world.provider instanceof IGalacticraftWorldProvider) {
            world.setBlock(x, y, z, this.unlitVersion, world.getBlockMetadata(x, y, z), 2);
        }
    }

    @Override
    public void onOxygenAdded(World world, int x, int y, int z) {
        if (world.provider instanceof IGalacticraftWorldProvider) {
            world.setBlock(x, y, z, this.litVersion, world.getBlockMetadata(x, y, z), 2);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        final ArrayList<ItemStack> ret = new ArrayList<>();
        ret.add(new ItemStack(this.litVersion));
        return ret;
    }
}
