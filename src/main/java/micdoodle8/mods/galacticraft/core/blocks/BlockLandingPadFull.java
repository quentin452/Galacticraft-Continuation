package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraft.util.*;
import net.minecraft.item.*;

public class BlockLandingPadFull extends BlockAdvancedTile implements IPartialSealableBlock
{
    private IIcon[] icons;
    
    public BlockLandingPadFull(final String assetName) {
        super(Material.rock);
        this.icons = new IIcon[3];
        this.setHardness(1.0f);
        this.setResistance(10.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
        this.maxY = 0.39;
    }
    
    public int damageDropped(final int meta) {
        return meta;
    }
    
    public int quantityDropped(final Random par1Random) {
        return 9;
    }
    
    public void breakBlock(final World var1, final int var2, final int var3, final int var4, final Block var5, final int var6) {
        final TileEntity var7 = var1.getTileEntity(var2, var3, var4);
        if (var7 instanceof IMultiBlock) {
            ((IMultiBlock)var7).onDestroy(var7);
        }
        super.breakBlock(var1, var2, var3, var4, var5, var6);
    }
    
    public Item getItemDropped(final int par1, final Random par2Random, final int par3) {
        return Item.getItemFromBlock(GCBlocks.landingPad);
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        switch (world.getBlockMetadata(x, y, z)) {
            case 0: {
                return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
            }
            case 2: {
                return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
            }
            default: {
                return AxisAlignedBB.getBoundingBox(x + 0.0, y + 0.0, z + 0.0, x + 1.0, y + 0.2, z + 1.0);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World world, final int x, final int y, final int z) {
        switch (world.getBlockMetadata(x, y, z)) {
            case 0: {
                return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
            }
            case 2: {
                return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
            }
            default: {
                return AxisAlignedBB.getBoundingBox(x + 0.0, y + 0.0, z + 0.0, x + 1.0, y + 0.2, z + 1.0);
            }
        }
    }
    
    public int getRenderType() {
        return GalacticraftCore.proxy.getBlockRender((Block)this);
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.icons[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "launch_pad");
        this.icons[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "buggy_fueler");
        this.icons[2] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "buggy_fueler_blank");
        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "launch_pad");
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final int par1, final int par2) {
        switch (par2) {
            case 0: {
                return this.icons[0];
            }
            case 1: {
                return this.icons[1];
            }
            case 2: {
                return this.icons[2];
            }
            default: {
                return this.blockIcon;
            }
        }
    }
    
    public boolean canPlaceBlockAt(final World world, final int x, final int y, final int z) {
        for (int x2 = -1; x2 < 2; ++x2) {
            for (int z2 = -1; z2 < 2; ++z2) {
                if (!super.canPlaceBlockAt(world, x + x2, y, z + z2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean hasTileEntity(final int metadata) {
        return true;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        switch (metadata) {
            case 0: {
                return new TileEntityLandingPad();
            }
            case 1: {
                return new TileEntityBuggyFueler();
            }
            default: {
                return null;
            }
        }
    }
    
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final Block par5) {
        par1World.markBlockForUpdate(par2, par3, par4);
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
    
    public boolean isSealed(final World world, final int x, final int y, final int z, final ForgeDirection direction) {
        return direction == ForgeDirection.UP;
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        return new ItemStack(Item.getItemFromBlock(GCBlocks.landingPad), 1, metadata);
    }
}
