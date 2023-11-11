package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import net.minecraft.item.*;

public class BlockAirLockWall extends BlockBreakable implements IPartialSealableBlock
{
    public BlockAirLockWall(final String assetName) {
        super(GalacticraftCore.TEXTURE_PREFIX + "oxygentile_3", Material.iron, false);
        this.setTickRandomly(true);
        this.setHardness(1000.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_4");
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        this.setBlockBoundsBasedOnState((IBlockAccess)world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    
    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int x, final int y, final int z) {
        final Block frameID = GCBlocks.airLockFrame;
        final Block sealID = GCBlocks.airLockSeal;
        final Block idXMin = world.getBlock(x - 1, y, z);
        final Block idXMax = world.getBlock(x + 1, y, z);
        if (idXMin != frameID && idXMax != frameID && idXMin != sealID && idXMax != sealID) {
            final float var5 = 0.25f;
            final float var6 = 0.5f;
            this.setBlockBounds(0.5f - var5, 0.0f, 0.5f - var6, 0.5f + var5, 1.0f, 0.5f + var6);
        }
        else {
            int adjacentCount = 0;
            for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (dir != ForgeDirection.UP && dir != ForgeDirection.DOWN) {
                    Vector3 thisVec = new Vector3((double)x, (double)y, (double)z);
                    thisVec = thisVec.modifyPositionFromSide(dir);
                    final Block blockID = thisVec.getBlock(world);
                    if (blockID == GCBlocks.airLockFrame || blockID == GCBlocks.airLockSeal) {
                        ++adjacentCount;
                    }
                }
            }
            if (adjacentCount == 4) {
                this.setBlockBounds(0.0f, 0.25f, 0.0f, 1.0f, 0.75f, 1.0f);
            }
            else {
                final float var5 = 0.5f;
                final float var6 = 0.25f;
                this.setBlockBounds(0.5f - var5, 0.0f, 0.5f - var6, 0.5f + var5, 1.0f, 0.5f + var6);
            }
        }
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
        return true;
    }
    
    public int quantityDropped(final Random par1Random) {
        return 0;
    }
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        return true;
    }
    
    public Item getItem(final World world, final int x, final int y, final int z) {
        return null;
    }
}
