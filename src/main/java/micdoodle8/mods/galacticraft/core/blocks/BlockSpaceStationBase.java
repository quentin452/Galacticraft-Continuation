package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.util.*;

public class BlockSpaceStationBase extends BlockContainer implements ITileEntityProvider
{
    private IIcon[] spaceStationIcons;
    
    public BlockSpaceStationBase(final String assetName) {
        super(Material.rock);
        this.setHardness(-1.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setBlockName(assetName);
    }
    
    public float getBlockHardness(final World par1World, final int par2, final int par3, final int par4) {
        return -1.0f;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        (this.spaceStationIcons = new IIcon[2])[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "space_station_top");
        this.spaceStationIcons[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "space_station_side");
        this.blockIcon = this.spaceStationIcons[0];
    }
    
    public IIcon getIcon(final int par1, final int par2) {
        switch (par1) {
            case 1: {
                return this.spaceStationIcons[0];
            }
            default: {
                return this.spaceStationIcons[1];
            }
        }
    }
    
    public void breakBlock(final World var1, final int var2, final int var3, final int var4, final Block var5, final int var6) {
        final TileEntity tileAt = var1.getTileEntity(var2, var3, var4);
        if (tileAt instanceof IMultiBlock) {
            ((IMultiBlock)tileAt).onDestroy(tileAt);
        }
        super.breakBlock(var1, var2, var3, var4, var5, var6);
    }
    
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new TileEntitySpaceStationBase();
    }
    
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entityLiving, final ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IMultiBlock) {
            ((IMultiBlock)tile).onCreate(new BlockVec3(x, y, z));
        }
    }
    
    public ItemStack getPickBlock(final MovingObjectPosition moving, final World world, final int x, final int y, final int z) {
        return null;
    }
}
