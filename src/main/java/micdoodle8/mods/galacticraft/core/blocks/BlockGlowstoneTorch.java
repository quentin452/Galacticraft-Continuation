package micdoodle8.mods.galacticraft.core.blocks;

import static net.minecraftforge.common.util.ForgeDirection.*;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class BlockGlowstoneTorch extends Block implements ItemBlockDesc.IBlockShiftDesc {

    protected BlockGlowstoneTorch(String assetName) {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
        this.setLightLevel(0.85F);
        this.setStepSound(Block.soundTypeWood);
    }

    private static boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection direction) {
        return world.getBlock(x, y, z)
            .isSideSolid(world, x, y, z, direction);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
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
        final Block l = par1World.getBlock(par2, par3, par4);
        return l.canPlaceTorchOnTop(par1World, par2, par3, par4);
    }

    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, EAST)
            || BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, WEST)
            || BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, SOUTH)
            || BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, NORTH)
            || this.canPlaceTorchOn(par1World, par2, par3 - 1, par4);
    }

    @Override
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7,
        float par8, int par9) {
        int j1 = par9;

        if (par5 == 1 && this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
            j1 = 5;
        }

        if (par5 == 2 && BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, NORTH)) {
            j1 = 4;
        }

        if (par5 == 3 && BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, SOUTH)) {
            j1 = 3;
        }

        if (par5 == 4 && BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, WEST)) {
            j1 = 2;
        }

        if (par5 == 5 && BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, EAST)) {
            j1 = 1;
        }

        return j1;
    }

    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        super.updateTick(par1World, par2, par3, par4, par5Random);

        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            this.onBlockAdded(par1World, par2, par3, par4);
        }
    }

    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            if (BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, EAST)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
            } else if (BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, WEST)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
            } else if (BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, SOUTH)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
            } else if (BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, NORTH)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
            } else if (this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
            }
        }

        this.dropTorchIfCantStay(par1World, par2, par3, par4);
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
        if (this.dropTorchIfCantStay(par1World, par2, par3, par4)) {
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            boolean flag = false;

            if (!BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 - 1, par3, par4, EAST) && i1 == 1) {
                flag = true;
            }

            if (!BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2 + 1, par3, par4, WEST) && i1 == 2) {
                flag = true;
            }

            if (!BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 - 1, SOUTH) && i1 == 3) {
                flag = true;
            }

            if (!BlockGlowstoneTorch.isBlockSolidOnSide(par1World, par2, par3, par4 + 1, NORTH) && i1 == 4) {
                flag = true;
            }

            if (!this.canPlaceTorchOn(par1World, par2, par3 - 1, par4) && i1 == 5) {
                flag = true;
            }

            if (flag) {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlockToAir(par2, par3, par4);
            }
        }
    }

    protected boolean dropTorchIfCantStay(World par1World, int par2, int par3, int par4) {
        if (this.canPlaceBlockAt(par1World, par2, par3, par4)) {
            return true;
        }
        if (par1World.getBlock(par2, par3, par4) == this) {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
        }

        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3,
        Vec3 par6Vec3) {
        final int l = par1World.getBlockMetadata(par2, par3, par4) & 7;
        float f = 0.15F;

        switch (l) {
            case 1:
                this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
                break;
            case 2:
                this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
                break;
            case 3:
                this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
                break;
            case 4:
                this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
                break;
            default:
                f = 0.1F;
                this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
                break;
        }

        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }

    @Override
    public String getShiftDescription(int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }

    @Override
    public boolean showDescription(int meta) {
        return true;
    }
}
