package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockGlowstoneTorch extends Block implements ItemBlockDesc.IBlockShiftDesc
{
    protected BlockGlowstoneTorch(final String assetName) {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
        this.setLightLevel(0.85f);
        this.setStepSound(Block.soundTypeWood);
    }
    
    private static boolean isBlockSolidOnSide(final World world, final int x, final int y, final int z, final ForgeDirection direction, final boolean nope) {
        return world.getBlock(x, y, z).isSideSolid((IBlockAccess)world, x, y, z, direction);
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4) {
        return null;
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender(this);
    }
    
    private boolean canPlaceTorchOn(final World par1World, final int par2, final int par3, final int par4) {
        if (World.doesBlockHaveSolidTopSurface((IBlockAccess)par1World, par2, par3, par4)) {
            return true;
        }
        final Block l = par1World.getBlock(par2, par3, par4);
        return l.canPlaceTorchOnTop(par1World, par2, par3, par4);
    }
    
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4) {
        return isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true) || isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true) || isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true) || isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true) || this.canPlaceTorchOn(par1World, par2, par3 - 1, par4);
    }
    
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9) {
        int j1 = par9;
        if (par5 == 1 && this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
            j1 = 5;
        }
        if (par5 == 2 && isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
            j1 = 4;
        }
        if (par5 == 3 && isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
            j1 = 3;
        }
        if (par5 == 4 && isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
            j1 = 2;
        }
        if (par5 == 5 && isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
            j1 = 1;
        }
        return j1;
    }
    
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random) {
        super.updateTick(par1World, par2, par3, par4, par5Random);
        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            this.onBlockAdded(par1World, par2, par3, par4);
        }
    }
    
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4) {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
            if (isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
            }
            else if (isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
            }
            else if (isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
            }
            else if (isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
            }
            else if (this.canPlaceTorchOn(par1World, par2, par3 - 1, par4)) {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
            }
        }
        this.dropTorchIfCantStay(par1World, par2, par3, par4);
    }
    
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final Block par5) {
        if (this.dropTorchIfCantStay(par1World, par2, par3, par4)) {
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            boolean flag = false;
            if (!isBlockSolidOnSide(par1World, par2 - 1, par3, par4, ForgeDirection.EAST, true) && i1 == 1) {
                flag = true;
            }
            if (!isBlockSolidOnSide(par1World, par2 + 1, par3, par4, ForgeDirection.WEST, true) && i1 == 2) {
                flag = true;
            }
            if (!isBlockSolidOnSide(par1World, par2, par3, par4 - 1, ForgeDirection.SOUTH, true) && i1 == 3) {
                flag = true;
            }
            if (!isBlockSolidOnSide(par1World, par2, par3, par4 + 1, ForgeDirection.NORTH, true) && i1 == 4) {
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
    
    protected boolean dropTorchIfCantStay(final World par1World, final int par2, final int par3, final int par4) {
        if (!this.canPlaceBlockAt(par1World, par2, par3, par4)) {
            if (par1World.getBlock(par2, par3, par4) == this) {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlockToAir(par2, par3, par4);
            }
            return false;
        }
        return true;
    }
    
    public MovingObjectPosition collisionRayTrace(final World par1World, final int par2, final int par3, final int par4, final Vec3 par5Vec3, final Vec3 par6Vec3) {
        final int l = par1World.getBlockMetadata(par2, par3, par4) & 0x7;
        float f = 0.15f;
        if (l == 1) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - f, f * 2.0f, 0.8f, 0.5f + f);
        }
        else if (l == 2) {
            this.setBlockBounds(1.0f - f * 2.0f, 0.2f, 0.5f - f, 1.0f, 0.8f, 0.5f + f);
        }
        else if (l == 3) {
            this.setBlockBounds(0.5f - f, 0.2f, 0.0f, 0.5f + f, 0.8f, f * 2.0f);
        }
        else if (l == 4) {
            this.setBlockBounds(0.5f - f, 0.2f, 1.0f - f * 2.0f, 0.5f + f, 0.8f, 1.0f);
        }
        else {
            f = 0.1f;
            this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.6f, 0.5f + f);
        }
        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
