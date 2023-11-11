package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class BlockBrightLamp extends BlockAdvanced implements ItemBlockDesc.IBlockShiftDesc
{
    public static IIcon icon;
    
    protected BlockBrightLamp(final String assetName) {
        super(Material.glass);
        this.setHardness(0.1f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName("stone");
        this.setBlockName(assetName);
        this.setLightLevel(1.0f);
    }
    
    public int getLightValue(final IBlockAccess world, final int x, final int y, final int z) {
        final Block block = world.getBlock(x, y, z);
        if (block != this) {
            return block.getLightValue(world, x, y, z);
        }
        final int redstone = 0;
        final World w = VersionUtil.getWorld(world);
        return RedstoneUtil.isBlockReceivingRedstone(w, x, y, z) ? 0 : this.getLightValue();
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int x, final int y, final int z) {
        final double boundsMin = 0.2;
        final double boundsMax = 0.8;
        return AxisAlignedBB.getBoundingBox(x + boundsMin, y + boundsMin, z + boundsMin, x + boundsMax, y + boundsMax, z + boundsMax);
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    public boolean canPlaceBlockAt(final World par1World, final int x, final int y, final int z) {
        final BlockVec3 thisvec = new BlockVec3(x, y, z);
        for (int i = 0; i < 6; ++i) {
            if (thisvec.blockOnSideHasSolidFace(par1World, i)) {
                return true;
            }
        }
        return false;
    }
    
    public int onBlockPlaced(final World world, final int x, final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ, final int metaOld) {
        final BlockVec3 thisvec = new BlockVec3(x, y, z);
        if (thisvec.blockOnSideHasSolidFace(world, side ^ 0x1)) {
            return side ^ 0x1;
        }
        return metaOld;
    }
    
    public void onNeighborBlockChange(final World par1World, final int x, final int y, final int z, final Block par5) {
        final int side = par1World.getBlockMetadata(x, y, z);
        final BlockVec3 thisvec = new BlockVec3(x, y, z);
        if (thisvec.blockOnSideHasSolidFace(par1World, side)) {
            return;
        }
        this.dropBlockAsItem(par1World, x, y, z, 0, 0);
        par1World.setBlock(x, y, z, Blocks.air);
    }
    
    public MovingObjectPosition collisionRayTrace(final World par1World, final int x, final int y, final int z, final Vec3 par5Vec3, final Vec3 par6Vec3) {
        final int var7 = par1World.getBlockMetadata(x, y, z);
        final float var8 = 0.3f;
        if (var7 == 4) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - var8, var8 * 2.0f, 0.8f, 0.5f + var8);
        }
        else if (var7 == 5) {
            this.setBlockBounds(1.0f - var8 * 2.0f, 0.2f, 0.5f - var8, 1.0f, 0.8f, 0.5f + var8);
        }
        else if (var7 == 2) {
            this.setBlockBounds(0.5f - var8, 0.2f, 0.0f, 0.5f + var8, 0.8f, var8 * 2.0f);
        }
        else if (var7 == 3) {
            this.setBlockBounds(0.5f - var8, 0.2f, 1.0f - var8 * 2.0f, 0.5f + var8, 0.8f, 1.0f);
        }
        else if (var7 == 0) {
            this.setBlockBounds(0.5f - var8, 0.0f, 0.5f - var8, 0.5f + var8, 0.6f, 0.5f + var8);
        }
        else {
            this.setBlockBounds(0.5f - var8, 0.4f, 0.5f - var8, 0.5f + var8, 1.0f, 0.5f + var8);
        }
        return super.collisionRayTrace(par1World, x, y, z, par5Vec3, par6Vec3);
    }
    
    public boolean onUseWrench(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        if (!world.isRemote) {
            final TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityArclamp) {
                ((TileEntityArclamp)tile).facingChanged();
            }
        }
        return true;
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntityArclamp();
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
    
    public String getShiftDescription(final int meta) {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }
    
    public boolean showDescription(final int meta) {
        return true;
    }
}
